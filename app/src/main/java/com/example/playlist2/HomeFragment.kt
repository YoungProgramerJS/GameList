package com.example.playlist2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist2.adapters.GameAdapter
import com.example.playlist2.GameData.getTopTenGames
import com.example.playlist2.GameData.getNewGames

class HomeFragment : Fragment() {

    private lateinit var topTenRecyclerView: RecyclerView
    private lateinit var newGamesRecyclerView: RecyclerView
    private lateinit var topTenAdapter: GameAdapter
    private lateinit var newGamesAdapter: GameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicjalizacja RecyclerView dla Top 10
        topTenRecyclerView = view.findViewById(R.id.topTenRecyclerView)
        topTenRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inicjalizacja RecyclerView dla New Games
        newGamesRecyclerView = view.findViewById(R.id.newGamesRecyclerView)
        newGamesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Pobierz dane z Firestore dla Top 10
        getTopTenGames { games ->
            topTenAdapter = GameAdapter(games)
            topTenRecyclerView.adapter = topTenAdapter
        }

        // Pobierz dane z Firestore dla New Games
        getNewGames { games ->
            newGamesAdapter = GameAdapter(games)
            newGamesRecyclerView.adapter = newGamesAdapter
        }
    }
}
