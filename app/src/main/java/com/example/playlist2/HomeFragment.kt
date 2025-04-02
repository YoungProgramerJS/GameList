package com.example.playlist2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

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
            topTenAdapter = GameAdapter(games) { gameId ->
                val fragment = GameDetails.newInstance(gameId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            topTenRecyclerView.adapter = topTenAdapter
        }

        // Pobierz dane z Firestore dla New Games
        getNewGames { games ->
            newGamesAdapter = GameAdapter(games) { gameId ->
                val fragment = GameDetails.newInstance(gameId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            newGamesRecyclerView.adapter = newGamesAdapter
        }
    }

    // Funkcja do pobierania gier Top 10 z Firestore
    private fun getTopTenGames(callback: (List<Game>) -> Unit) {
        FirebaseFirestore.getInstance().collection("games")
            .orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val games = mutableListOf<Game>()
                for (document in documents) {
                    val game = document.toObject(Game::class.java)
                    games.add(game)
                }
                callback(games)
            }
    }

    private fun getNewGames(callback: (List<Game>) -> Unit) {
        FirebaseFirestore.getInstance().collection("games")
            .orderBy("releaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sortowanie malejąco według daty wydania
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val games = mutableListOf<Game>()
                for (document in documents) {
                    val game = document.toObject(Game::class.java)
                    games.add(game)
                }
                callback(games)
            }
    }


    // Adapter wewnątrz HomeFragment
    class GameAdapter(
        private val games: List<Game>,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.top10, parent, false)
            return GameViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = games[position] // Pobieramy obiekt gry

            holder.bind(game)

            // Obsługa kliknięcia na element
            holder.itemView.setOnClickListener {
                // Przekazujemy ID gry do fragmentu szczegółów
                val fragment = GameDetails.newInstance(game.title)

                // Przejście do fragmentu szczegółów gry
                (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        override fun getItemCount(): Int = games.size

        inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(game: Game) {
                val title = itemView.findViewById<TextView>(R.id.gameTitle)
                val coverImage = itemView.findViewById<ImageView>(R.id.gameImage)
                val ratingBar = itemView.findViewById<RatingBar>(R.id.gameRating)

                title.text = game.title
                ratingBar.rating = game.rating.toFloat()

                Glide.with(itemView.context)
                    .load(game.coverImageUrl)
                    .into(coverImage)
            }
        }
    }

    // Klasa Game
    data class Game(
        val title: String = "",
        val coverImageUrl: String = "",
        val rating: Double = 0.0,
        val releaseDate: String = ""
    )
}
