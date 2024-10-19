package com.example.playlist2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.gameTitle.text = game.name
        holder.gameRating.rating = game.rating
        holder.gameImage.setImageResource(game.imageResId)
        holder.gameDesc.text = game.description
    }

    override fun getItemCount(): Int = games.size

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameTitle: TextView = view.findViewById(R.id.gameTitle)
        val gameRating: RatingBar = view.findViewById(R.id.gameRating)
        val gameImage: ImageView = view.findViewById(R.id.gameImage)
        val gameDesc: TextView = view.findViewById(R.id.gameDesc)
    }
}
