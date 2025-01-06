package com.example.playlist2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist2.GameDetails
import com.example.playlist2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class FavouriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var savedGameDocuments: List<DocumentSnapshot>  // Zmienna do przechowywania dokumentów

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerView = view.findViewById(R.id.favouriteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = GameAdapter(mutableListOf())
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()
        loadFavouriteGames()

        return view
    }

    private fun loadFavouriteGames() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("saved_games")
            .get()
            .addOnSuccessListener { savedGamesSnapshot ->
                // Przechowujemy dokumenty zapisanych gier
                savedGameDocuments = savedGamesSnapshot.documents

                val gameIds = savedGamesSnapshot.documents.mapNotNull { it.id }

                db.collection("games").whereIn(FieldPath.documentId(), gameIds)
                    .get()
                    .addOnSuccessListener { gamesSnapshot ->
                        val games = gamesSnapshot.documents.mapNotNull { it.toObject(Game::class.java) }
                        adapter.updateGames(games, savedGameDocuments)  // Przekazujemy dokumenty do adaptera
                    }
                    .addOnFailureListener { e ->
                        Log.e("FavouriteFragment", "Error fetching games: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FavouriteFragment", "Error fetching saved games: ${e.message}")
            }
    }

    // Adapter class for RecyclerView
    inner class GameAdapter(private var games: MutableList<Game>, private var documents: List<DocumentSnapshot> = listOf()) :
        RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

        inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
            private val gameImage: ImageView = itemView.findViewById(R.id.gameImage)
            private val ratingCount: TextView = itemView.findViewById(R.id.ratingCount)
            private val gameRating: RatingBar = itemView.findViewById(R.id.gameRating)
            private val gameDescription: TextView = itemView.findViewById(R.id.gameDescription)

            fun bind(game: Game) {
                gameTitle.text = game.title
                ratingCount.text = game.rating.toString()
                gameRating.rating = game.rating
                gameDescription.text = game.description

                Glide.with(itemView.context)
                    .load(game.coverImageUrl)
                    .into(gameImage)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite, parent, false)
            return GameViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = games[position]
            val document = documents[position]  // Pobieramy dokument z Firestore
            val gameId = document.id  // ID dokumentu Firestore

            holder.bind(game)

            holder.itemView.setOnClickListener {
                // Przekazujemy ID dokumentu do fragmentu szczegółów gry
                val fragment = GameDetails.newInstance(gameId)

                // Przejście do fragmentu szczegółów gry
                (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        override fun getItemCount(): Int = games.size

        fun updateGames(newGames: List<Game>, newDocuments: List<DocumentSnapshot>) {
            games.clear()
            games.addAll(newGames)
            documents = newDocuments  // Zaktualizowanie dokumentów
            notifyDataSetChanged()
        }
    }

    // Data class for games
    data class Game(
        val title: String = "",
        val rating: Float = 0f,
        val description: String = "",
        val coverImageUrl: String = ""
    )
}
