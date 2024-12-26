package com.example.playlist2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist2.GameData
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist2.Game
import com.example.playlist2.R
import com.example.playlist2.adapters.GameAdapter

class FavouriteFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

}

