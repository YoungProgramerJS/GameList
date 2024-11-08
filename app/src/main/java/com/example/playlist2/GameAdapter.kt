package com.example.playlist2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist2.Game
import com.example.playlist2.R

class GameAdapter(private val gameList: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.gameImage)
        val gameTitle: TextView = view.findViewById(R.id.gameTitle)
        val gameRating: RatingBar = view.findViewById(R.id.gameRating)
        val ratingCount: TextView = view.findViewById(R.id.ratingCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top10, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]
        holder.gameTitle.text = game.title
        holder.gameRating.rating = game.rating
        holder.gameImage.setImageResource(game.imageResId)


        holder.ratingCount.text = (game.rating).toString()
    }

    override fun getItemCount() = gameList.size
}
