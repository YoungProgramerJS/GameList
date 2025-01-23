package com.example.playlist2

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

object FirestoreStatusUpdater {

    fun addStatusToGames() {
        val db = FirebaseFirestore.getInstance()
        val gamesCollection = db.collection("games") // Zmień nazwę kolekcji, jeśli to konieczne

        gamesCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val gameId = document.id
                    val data = document.data

                    // Sprawdź, czy pole "status" już istnieje
                    if (!data.containsKey("status")) {
                        gamesCollection.document(gameId).update("status", "Planned")
                            .addOnSuccessListener {
                                Log.d("FirestoreUpdate", "Status added for game: $gameId")
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreUpdate", "Error updating game: $gameId", e)
                            }
                    } else {
                        Log.d("FirestoreUpdate", "Game $gameId already has a status")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreFetch", "Error fetching games", e)
            }
    }
}