package com.example.playlist2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object GameData {

    private val db = FirebaseFirestore.getInstance()

    // Funkcja pobierająca top 10 gier posortowanych po ocenie
    fun getTopTenGames(onSuccess: (List<Game>) -> Unit) {
        val gamesList = mutableListOf<Game>()
        db.collection("games")
            .orderBy("rating", Query.Direction.DESCENDING) // Sortowanie po ratingu malejąco
            .limit(10) // Ograniczenie do 10 gier
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val game = document.toObject(Game::class.java)
                    gamesList.add(game)
                }
                onSuccess(gamesList)
            }
    }
    // Funkcja pobierająca najnowsze gry posortowane po dacie wydania
    fun getNewGames(onSuccess: (List<Game>) -> Unit) {
        val gamesList = mutableListOf<Game>()
        db.collection("games")
            .orderBy("releaseDate", Query.Direction.DESCENDING) // Sortowanie po dacie malejąco (od najnowszych)
            .limit(5) // Ograniczenie do 10 gier
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val game = document.toObject(Game::class.java)
                    gamesList.add(game)
                }
                onSuccess(gamesList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Błąd podczas pobierania gier: ", exception)
            }
    }

}
