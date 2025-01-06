import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist2.GameDetails
import com.example.playlist2.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var gameAdapter: GameAdapter
    private var selectedGenre: String? = null
    private var activeButton: Button? = null
    private var gamesList: List<Game> = emptyList()  // Lista gier
    private var documentList: List<DocumentSnapshot> = emptyList()  // Lista dokumentów z Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        db = FirebaseFirestore.getInstance()

        // Inicjalizacja RecyclerView z GridLayoutManager
        gameAdapter = GameAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewSearch)
        recyclerView.layoutManager = GridLayoutManager(context, 2)  // 2 kolumny
        recyclerView.adapter = gameAdapter

        // Pobranie danych z Firestore
        fetchGamesFromFirestore()

        // Ustawienie SearchView
        val searchView: androidx.appcompat.widget.SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Przeszukujemy gry po tytule
                query?.let {
                    filterGamesBySearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Przeszukujemy gry na bieżąco
                newText?.let {
                    filterGamesBySearch(it)
                }
                return true
            }
        })

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lista przycisków i odpowiadających im gatunków
        val buttonsWithGenres = mapOf(
            R.id.actionGenreButton to "Action",
            R.id.adventureGenreButton to "Adventure",
            R.id.rpgGenreButton to "RPG",
            R.id.FPSGenreButton to "FPS",
            R.id.BRGenreButton to "Battle Royale",
            R.id.mobaGenreButton to "MOBA",
            R.id.sandBoxGenreButton to "Sandbox",
            R.id.platformerGenreButton to "Platformer",
            R.id.sportsGenreButton to "Sports"
        )

        // Iterowanie po mapie przycisków i przypisywanie OnClickListener
        buttonsWithGenres.forEach { (buttonId, genre) ->
            val button = view.findViewById<Button>(buttonId)
            button.setOnClickListener {
                // Sprawdzenie, czy przycisk jest już aktywny
                if (activeButton == button) {
                    // Jeśli tak, resetuj kolor tła i tekstu
                    button.backgroundTintList = null
                    button.setTextColor(resources.getColor(R.color.white)) // Kolor tekstu na biały
                    activeButton = null
                    selectedGenre = null
                } else {
                    // Resetowanie poprzedniego aktywnego przycisku
                    activeButton?.backgroundTintList = null
                    activeButton?.setTextColor(resources.getColor(R.color.white)) // Kolor tekstu na biały

                    // Ustawienie żółtego tła i czarnego tekstu dla aktywnego przycisku
                    button.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    button.setTextColor(resources.getColor(R.color.black)) // Kolor tekstu na czarny

                    // Ustawienie aktywnego przycisku i gatunku
                    activeButton = button
                    selectedGenre = genre
                }

                // Filtrujemy gry na podstawie wybranego gatunku
                filterGamesByGenre(selectedGenre)
            }
        }
    }

    private fun filterGamesBySearch(query: String) {
        val filteredGames = if (query.isEmpty()) {
            gamesList // Pełna lista gier, jeśli pole wyszukiwania jest puste
        } else {
            gamesList.filter { it.title.contains(query, ignoreCase = true) }  // Filtrujemy gry po tytule
        }

        // Zaktualizowanie adaptera z przefiltrowaną listą gier
        gameAdapter.updateGames(filteredGames)
    }


    private fun filterGamesByGenre(genre: String?) {
        val filteredGames = if (genre == null) {
            gamesList // Pełna lista gier
        } else {
            gamesList.filter { it.genre == genre }  // Filtrujemy gry po gatunku
        }

        // Zaktualizowanie adaptera z przefiltrowaną listą gier
        gameAdapter.updateGames(filteredGames)
    }

    private fun fetchGamesFromFirestore() {
        db.collection("games")  // Nazwa kolekcji w Firestore
            .get()
            .addOnSuccessListener { documents ->
                val gameList = mutableListOf<Game>()
                val docList = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    // Tworzymy obiekt gry
                    val game = document.toObject(Game::class.java)
                    gameList.add(game)
                    docList.add(document)  // Przechowujemy dokumenty
                }
                gamesList = gameList  // Przypisujemy pobraną listę gier
                documentList = docList  // Przypisujemy dokumenty
                gameAdapter.setGames(gameList, docList)  // Ustawiamy listę gier i dokumentów w adapterze
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }
    }

    data class Game(
        val coverImageUrl: String = "",
        val title: String = "",
        val rating: Double = 0.0,
        val genre: String = "",  // Dodanie gatunku do modelu
        val description: String = "",  // Dodanie opisu do modelu
        var id: String = "",
    )

    inner class GameAdapter : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
        private var games = listOf<Game>()
        private var documents = listOf<DocumentSnapshot>()  // Przechowujemy dokumenty Firestore

        fun setGames(games: List<Game>, documents: List<DocumentSnapshot>) {
            this.games = games
            this.documents = documents  // Przechowujemy dokumenty
            notifyDataSetChanged()  // Odświeżamy adapter
        }

        fun updateGames(games: List<Game>) {
            this.games = games
            notifyDataSetChanged()  // Odświeżamy listę gier w adapterze
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.searchgame, parent, false)
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

        inner class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val gameImageView = view.findViewById<ImageView>(R.id.gameImageView)
            private val gameTitleTextView = view.findViewById<TextView>(R.id.gameTitleTextView)
            private val gameRatingTextView = view.findViewById<TextView>(R.id.gameRatingTextView)

            fun bind(game: Game) {
                // Ładowanie obrazu z URL (przy użyciu biblioteki Glide)
                Glide.with(itemView.context)
                    .load(game.coverImageUrl)
                    .into(gameImageView)

                gameTitleTextView.text = game.title
                gameRatingTextView.text = "Rating: ${game.rating}"
            }
        }
    }
}
