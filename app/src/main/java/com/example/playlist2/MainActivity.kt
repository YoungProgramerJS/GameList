package com.example.playlist2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist2.Game
import com.example.playlist2.GameAdapter
import com.example.playlist2.R

class MainActivity : AppCompatActivity() {

    private lateinit var topTenRecyclerView: RecyclerView
    private lateinit var latestRecyclerView: RecyclerView
    private lateinit var topTenAdapter: GameAdapter
    private lateinit var latestAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja RecyclerView
        topTenRecyclerView = findViewById(R.id.topTenRecyclerView)
        latestRecyclerView = findViewById(R.id.latestRecyclerView)

        // Konfiguracja sekcji "Top 10"
        topTenRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        topTenAdapter = GameAdapter(getTopTenGames()) // Pobieramy dane dla Top 10
        topTenRecyclerView.adapter = topTenAdapter

        // Konfiguracja sekcji "Latest"
        latestRecyclerView.layoutManager = LinearLayoutManager(this) // Domyślnie pionowy układ
        latestAdapter = GameAdapter(getLatestGames()) // Pobieramy dane dla Latest
        latestRecyclerView.adapter = latestAdapter
    }

    // Przykładowe dane dla Top 10
    private fun getTopTenGames(): List<Game> {
        return listOf(
            Game("Red Dead Redemption 2", 4.5f, R.drawable.red_dead_redemption),
            Game("Grand Theft Auto V", 4.5f, R.drawable.gta_v)
            // Możesz dodać więcej gier według potrzeb
        )
    }

    // Przykładowe dane dla najnowszych gier
    private fun getLatestGames(): List<Game> {
        return listOf(
            Game("Silent Hill 2", 4.0f, R.drawable.silent_hill_2)
            // Możesz dodać więcej gier według potrzeb
        )
    }
}
