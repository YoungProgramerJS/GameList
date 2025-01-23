package com.example.playlist2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class GameDetails : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var saveButton: ImageButton
    private lateinit var userId: String
    private lateinit var commentEditText: EditText
    private lateinit var postCommentButton: Button
    private lateinit var commentsAdapter: CommentsAdapter
    private val commentsList = mutableListOf<Comment>()

    companion object {
        private const val ARG_GAME_ID = "gameId"

        fun newInstance(gameId: String): GameDetails {
            val fragment = GameDetails()
            val args = Bundle()
            args.putString(ARG_GAME_ID, gameId)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gamedetails, container, false)

        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val gameId = arguments?.getString(ARG_GAME_ID) ?: return view
        val documentRef = db.collection("games").document(gameId)

        // Inicjalizacja widoków
        saveButton = view.findViewById(R.id.saveButton)
        commentEditText = view.findViewById(R.id.commentEditText)
        postCommentButton = view.findViewById(R.id.postCommentButton)

        // Inicjalizacja widoków
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val ratingBarReadOnly: RatingBar = view.findViewById(R.id.ratingBarReadOnly)  // RatingBar tylko do odczytu
        val cancelButton: ImageButton = view.findViewById(R.id.cancelButton)
        val confirmButton: ImageButton = view.findViewById(R.id.confirmButton)

        // Inicjalizacja widoków tekstowych
        val gameCoverImageView: ImageView = view.findViewById(R.id.gameCover)
        val gameTitleTextView: TextView = view.findViewById(R.id.gameTitle)
        val publisherTextView: TextView = view.findViewById(R.id.publisher)
        val gameDescriptionTextView: TextView = view.findViewById(R.id.gameDescription)
        val genreTextView: TextView = view.findViewById(R.id.genre)
        val releaseDateTextView: TextView = view.findViewById(R.id.releaseDate)
        val statusSpinner: Spinner = view.findViewById(R.id.statusSpinner)

        // Spinner dla statusu
        val statusOptions = resources.getStringArray(R.array.status_options)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = adapter

        // Pobierz dane gry z Firestore
        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val game = document.toObject(Game::class.java)
                game?.let {
                    Picasso.get().load(it.coverImageUrl).into(gameCoverImageView)
                    gameTitleTextView.text = it.title
                    publisherTextView.text = it.publisher
                    gameDescriptionTextView.text = it.description
                    genreTextView.text = "Gatunek: ${it.genre}"
                    releaseDateTextView.text = "Data premiery: ${it.releaseDate}"

                    // Ustawienie początkowej oceny
                    ratingBarReadOnly.rating = it.rating.toFloat()
                    ratingBar.rating = it.rating.toFloat()

                    // Ustawienie początkowego statusu w Spinnerze
                    val currentStatus = it.status ?: "Planned"
                    statusSpinner.setSelection(statusOptions.indexOf(currentStatus))

                    // Obsługa kliknięcia przycisku X (anulowanie)
                    cancelButton.setOnClickListener {
                        ratingBar.visibility = View.GONE
                        ratingBarReadOnly.visibility = View.VISIBLE
                        confirmButton.visibility = View.GONE
                        cancelButton.visibility = View.GONE
                    }

                    // Obsługa kliknięcia przycisku ptaszka (zapis)
                    confirmButton.setOnClickListener {
                        val newRating = ratingBar.rating
                        db.collection("games").document(gameId).update("rating", newRating)
                            .addOnSuccessListener {
                                Log.d("RatingUpdate", "Rating updated to $newRating")
                            }
                            .addOnFailureListener { e ->
                                Log.e("RatingUpdate", "Error updating rating", e)
                            }

                        ratingBar.visibility = View.GONE
                        ratingBarReadOnly.visibility = View.VISIBLE
                        confirmButton.visibility = View.GONE
                        cancelButton.visibility = View.GONE
                    }

                    // Kliknięcie w ocenę tylko do odczytu przenosi do trybu edytowania
                    ratingBarReadOnly.setOnClickListener {
                        ratingBar.visibility = View.VISIBLE
                        ratingBarReadOnly.visibility = View.GONE
                        confirmButton.visibility = View.VISIBLE
                        cancelButton.visibility = View.VISIBLE
                    }

                    // Obsługa zmiany statusu
                    statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            val selectedStatus = statusOptions[position]
                            db.collection("games").document(gameId).update("status", selectedStatus)
                                .addOnSuccessListener {
                                    Log.d("StatusUpdate", "Updated status to $selectedStatus")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("StatusUpdate", "Error updating status", e)
                                }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    // Obsługa przycisku zapisu gry do ulubionych
                    saveButton.setOnClickListener {
                        val gameRef = db.collection("users").document(userId)
                            .collection("saved_games").document(gameId)

                        gameRef.get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                gameRef.set(mapOf(
                                    "gameId" to gameId,
                                    "status" to "Currently Playing" // Domyślny status
                                ))
                                saveButton.setImageResource(R.drawable.baseline_bookmark_24)
                            } else {
                                gameRef.delete()
                                saveButton.setImageResource(R.drawable.baseline_bookmark_border_24)
                            }
                        }
                    }

                    // Obsługa zamieszczania komentarza
                    postCommentButton.setOnClickListener {
                        val commentText = commentEditText.text.toString().trim()
                        if (commentText.isNotEmpty()) {
                            addCommentToGame(gameId, commentText)
                            commentEditText.text.clear()
                            loadComments(gameId)
                        } else {
                            Toast.makeText(context, "Wpisz komentarz!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Ładowanie komentarzy
                    loadComments(gameId)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("GameDetails", "Error getting document", exception)
        }

        // Ustawienie LayoutManager dla RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.commentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)  // Możesz użyć GridLayoutManager, jeśli chcesz
        return view
    }

    // Nowa funkcja loadComments
    private fun loadComments(gameId: String) {
        db.collection("games").document(gameId).collection("comments")
            .orderBy("timestamp")  // Nadal sortujemy po timestampie, ale go nie wyświetlamy
            .get()
            .addOnSuccessListener { commentsSnapshot ->
                commentsList.clear()
                for (doc in commentsSnapshot) {
                    val comment = doc.toObject(Comment::class.java)
                    commentsList.add(comment)
                }
                commentsAdapter = CommentsAdapter(commentsList)
                val recyclerView: RecyclerView = view?.findViewById(R.id.commentsRecyclerView)!!
                recyclerView.adapter = commentsAdapter
            }
            .addOnFailureListener { e ->
                Log.e("GameDetails", "Błąd pobierania komentarzy", e)
            }
    }

    private fun addCommentToGame(gameId: String, commentText: String) {
        val commentsRef = db.collection("games").document(gameId).collection("comments")

        val comment = hashMapOf(
            "text" to commentText,
            "userId" to userId,
            "timestamp" to FieldValue.serverTimestamp()  // Używamy serwerowego timestampu
        )

        commentsRef.add(comment)
            .addOnSuccessListener {
                Log.d("GameDetails", "Komentarz dodany pomyślnie")
                // Po dodaniu komentarza, ładujemy je ponownie
                loadComments(gameId)
            }
            .addOnFailureListener { e ->
                Log.e("GameDetails", "Błąd dodawania komentarza", e)
            }
    }

    // Adapter do wyświetlania komentarzy
    class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
            return CommentViewHolder(view)
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            val comment = comments[position]
            holder.bind(comment)
        }

        override fun getItemCount() = comments.size

        class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val userNameTextView: TextView = view.findViewById(R.id.userName)
            private val commentTextView: TextView = view.findViewById(R.id.commentText)

            fun bind(comment: Comment) {
                userNameTextView.text = comment.userId  // Możesz zastąpić 'userId' nazwą użytkownika, jeśli masz dodatkowe dane
                commentTextView.text = comment.text
            }
        }
    }

    // Model dla komentarza
    data class Comment(
        val userId: String = "",
        val text: String = "",
        val timestamp: com.google.firebase.Timestamp? = null
    )
}

