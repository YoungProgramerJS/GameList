import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist2.R
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var gameAdapter: GameAdapter
    private var selectedGenre: String? = null
    private var activeButton: Button? = null
    private var gamesList: List<Game> =
        emptyList()  // Lista gier, która będzie używana do filtrowania

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

    private fun filterGamesByGenre(genre: String?) {
        val filteredGames = if (genre == null) {
            gamesList // Pełna lista gier
        } else {
            gamesList.filter { it.genre == genre }  // Filtrujemy gry po gatunku
        }

        gameAdapter.updateGames(filteredGames)  // Uaktualniamy adapter
    }

    private fun fetchGamesFromFirestore() {
        db.collection("games")  // Nazwa kolekcji w Firestore
            .get()
            .addOnSuccessListener { documents ->
                val gameList = mutableListOf<Game>()
                for (document in documents) {
                    val game = document.toObject(Game::class.java)
                    gameList.add(game)
                }
                gamesList = gameList  // Przypisujemy pobraną listę gier
                gameAdapter.setGames(gameList)  // Ustawiamy listę gier w adapterze
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
        val description: String = ""  // Dodanie opisu do modelu
    )

    inner class GameAdapter : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
        private var games = listOf<Game>()

        fun setGames(games: List<Game>) {
            this.games = games
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
            holder.bind(game)

            // Obsługa kliknięcia na element listy
            holder.itemView.setOnClickListener {
                // Wywołanie funkcji do pokazania okna dialogowego z informacjami o grze
                showGameDetailsDialog(holder.itemView.context, game)
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

        private fun showGameDetailsDialog(context: Context, game: Game) {
            val builder = android.app.AlertDialog.Builder(context, R.style.CustomDialogStyle)

            // Inicjalizacja widoku dialogu
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_game_details, null)
            builder.setView(view)

            // Pobranie elementów z widoku dialogu
            val gameTitleTextView = view.findViewById<TextView>(R.id.gameTitleTextView)
            val gameDescriptionTextView = view.findViewById<TextView>(R.id.gameDescriptionTextView)
            val gameRatingTextView = view.findViewById<TextView>(R.id.gameRatingTextView)
            val gameImageView = view.findViewById<ImageView>(R.id.gameImageView)

            // Ustawienie wartości w widoku
            gameTitleTextView.text = game.title
            gameDescriptionTextView.text = game.description
            gameRatingTextView.text = "Rating: ${game.rating}"

            Glide.with(context)
                .load(game.coverImageUrl)
                .into(gameImageView)


            // Wyświetlenie dialogu
            val dialog = builder.create()
            dialog.show()

        }
    }
    }

