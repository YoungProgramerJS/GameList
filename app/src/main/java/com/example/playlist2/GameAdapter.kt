package com.example.playlist2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist2.Game
import com.example.playlist2.R

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top10, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int = games.size

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(game: Game) {
            val title = itemView.findViewById<TextView>(R.id.gameTitle)
            val coverImage = itemView.findViewById<ImageView>(R.id.gameImage)
            val ratingBar = itemView.findViewById<RatingBar>(R.id.gameRating) // Użyj RatingBar, a nie TextView

            title.text = game.title

            // Ustawienie oceny do RatingBar
            ratingBar.rating = game.rating.toFloat()  // RatingBar oczekuje wartości typu float

            Glide.with(itemView.context)
                .load(game.coverImageUrl)
                .into(coverImage)
        }
    }

}
