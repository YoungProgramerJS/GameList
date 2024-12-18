package com.example.playlist2

import SearchFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist2.Game
import com.example.playlist2.R
import com.example.playlist2.adapters.GameAdapter
import com.example.playlist2.fragments.FavouriteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var topTenRecyclerView: RecyclerView
    private lateinit var latestRecyclerView: RecyclerView
    private lateinit var topTenAdapter: GameAdapter
    private lateinit var latestAdapter: GameAdapter

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Funkcja dodająca dane
       //addGamesToFirestore()

        // Inicjalizacja BottomNavigationView

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.bottom_search -> {
                    replaceFragment(SearchFragment())
                    true
                }

                R.id.bottom_fav -> {
                    replaceFragment(FavouriteFragment())
                    true
                }

                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
        replaceFragment(HomeFragment())
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}

private fun addGamesToFirestore() {
    // Inicjalizacja Firestore
    val db = FirebaseFirestore.getInstance()

    // Lista danych gier
    val games = listOf(
        mapOf(
            "title" to "The Legend of Zelda: Breath of the Wild",
            "genre" to "Action-adventure",
            "publisher" to "Nintendo",
            "releaseDate" to "2017-03-03",
            "description" to "An open-world adventure game set in Hyrule.",
            "rating" to 9.5,
            "coverImageUrl" to "https://static.posters.cz/image/750/plakaty/zelda-breath-of-the-wild-game-cover-i34556.jpg"
        ),
        mapOf(
            "title" to "The Witcher 3: Wild Hunt",
            "genre" to "RPG",
            "publisher" to "CD Projekt Red",
            "releaseDate" to "2015-05-19",
            "description" to "An epic RPG adventure in the world of The Witcher.",
            "rating" to 9.7,
            "coverImageUrl" to "https://assetsio.gnwcdn.com/co5uct.jpg?width=1200&height=1200&fit=bounds&quality=70&format=jpg&auto=webp"
        ),
        mapOf(
            "title" to "God of War",
            "genre" to "Action-adventure",
            "publisher" to "Sony Interactive Entertainment",
            "releaseDate" to "2018-04-20",
            "description" to "A Norse mythology adventure following Kratos and his son.",
            "rating" to 9.8,
            "coverImageUrl" to "https://cdn1.epicgames.com/offer/3ddd6a590da64e3686042d108968a6b2/EGS_GodofWar_SantaMonicaStudio_S2_1200x1600-fbdf3cbc2980749091d52751ffabb7b7_1200x1600-fbdf3cbc2980749091d52751ffabb7b7"
        ),
        mapOf(
            "title" to "Cyberpunk 2077",
            "genre" to "RPG",
            "publisher" to "CD Projekt Red",
            "releaseDate" to "2020-12-10",
            "description" to "A futuristic RPG set in the open world of Night City.",
            "rating" to 8.5,
            "coverImageUrl" to "https://i.boop.pl/uploads/2022/01/FKVoOYOXoAABU4E.jpg"
        ),
        mapOf(
            "title" to "Red Dead Redemption 2",
            "genre" to "Action-adventure",
            "publisher" to "Rockstar Games",
            "releaseDate" to "2018-10-26",
            "description" to "A western-themed open-world adventure game.",
            "rating" to 9.6,
            "coverImageUrl" to "https://swiatgta.pl/wp-content/uploads/2018/05/b1dbeaf9021bcb7aa02893f8ccac3c26.jpg"
        ),
        mapOf(
            "title" to "Horizon Zero Dawn",
            "genre" to "Action-adventure",
            "publisher" to "Sony Interactive Entertainment",
            "releaseDate" to "2017-02-28",
            "description" to "Explore a post-apocalyptic world ruled by machines.",
            "rating" to 9.0,
            "coverImageUrl" to "https://image.api.playstation.com/vulcan/img/rnd/202009/2923/jAT7HjpL49A62bU7hLKXJ96b.png"
        ),
        mapOf(
            "title" to "Grand Theft Auto V",
            "genre" to "Action-adventure",
            "publisher" to "Rockstar Games",
            "releaseDate" to "2013-09-17",
            "description" to "A story of three criminals in the sprawling city of Los Santos.",
            "rating" to 9.5,
            "coverImageUrl" to "https://d-art.ppstatic.pl/kadry/k/r/1/87/bf/5cd135b006070_o_large.jpg"
        ),
        mapOf(
            "title" to "Elden Ring",
            "genre" to "Action RPG",
            "publisher" to "Bandai Namco Entertainment",
            "releaseDate" to "2022-02-25",
            "description" to "An expansive dark fantasy world designed by FromSoftware.",
            "rating" to 9.7,
            "coverImageUrl" to "https://storage.googleapis.com/pod_public/1300/216712.jpg"
        ),
        mapOf(
            "title" to "Assassin's Creed Valhalla",
            "genre" to "Action RPG",
            "publisher" to "Ubisoft",
            "releaseDate" to "2020-11-10",
            "description" to "Viking raids and exploration in the open-world of England.",
            "rating" to 8.8,
            "coverImageUrl" to "https://cdn1.epicgames.com/400347196e674de89c23cc2a7f2121db/offer/AC%20KINGDOM%20PREORDER_STANDARD%20EDITION_EPIC_Key_Art_Portrait_640x854-640x854-288120c5573756cb988b6c1968cebd86.png"
        ),
        mapOf(
            "title" to "Minecraft",
            "genre" to "Sandbox",
            "publisher" to "Mojang Studios",
            "releaseDate" to "2011-11-18",
            "description" to "Build, explore, and survive in an endless blocky world.",
            "rating" to 9.0,
            "coverImageUrl" to "https://bi.im-g.pl/im/92/a1/10/z17439634Q,Minecraft---okladka.jpg"
        ),
        mapOf(
            "title" to "Call of Duty: Modern Warfare",
            "genre" to "FPS",
            "publisher" to "Activision",
            "releaseDate" to "2019-10-25",
            "description" to "A realistic and gritty reboot of the Modern Warfare series.",
            "rating" to 8.6,
            "coverImageUrl" to "https://media.steelseriescdn.com/thumbs/filer_public/a0/dd/a0dd9115-5f79-4644-8de0-33d49645a62d/background_mobile.png__540x540_crop-scale_optimize_subsampling-2.png"
        ),
        mapOf(
            "title" to "Fortnite",
            "genre" to "Battle Royale",
            "publisher" to "Epic Games",
            "releaseDate" to "2017-07-25",
            "description" to "A popular battle royale game with building mechanics.",
            "rating" to 8.2,
            "coverImageUrl" to "https://cdn1.epicgames.com/offer/fn/Blade_1200x1600_1200x1600-fcea56f5eb92df731a89121e2b4416b5"
        ),
        mapOf(
            "title" to "Overwatch",
            "genre" to "FPS",
            "publisher" to "Blizzard Entertainment",
            "releaseDate" to "2016-05-24",
            "description" to "A team-based hero shooter with diverse characters.",
            "rating" to 8.9,
            "coverImageUrl" to "https://static.posters.cz/image/750/plakaty/overwatch-key-art-i32847.jpg"
        ),
        mapOf(
            "title" to "FIFA 22",
            "genre" to "Sports",
            "publisher" to "EA Sports",
            "releaseDate" to "2021-10-01",
            "description" to "The latest installment of EA's football simulation series.",
            "rating" to 7.5,
            "coverImageUrl" to "https://pliki.ppe.pl/storage/518a4d062435852a96fe/518a4d062435852a96fe.jpg"
        ),
        mapOf(
            "title" to "League of Legends",
            "genre" to "MOBA",
            "publisher" to "Riot Games",
            "releaseDate" to "2009-10-27",
            "description" to "A highly competitive multiplayer online battle arena game.",
            "rating" to 9.0,
            "coverImageUrl" to "https://interfaceingame.com/wp-content/uploads/league-of-legends/league-of-legends-cover-375x500.jpg"
        ),
        mapOf(
            "title" to "Apex Legends",
            "genre" to "Battle Royale",
            "publisher" to "Electronic Arts",
            "releaseDate" to "2019-02-04",
            "description" to "A team-based battle royale game with unique characters.",
            "rating" to 8.7,
            "coverImageUrl" to "https://cdn.gracza.pl/galeria/gry13/grupy/144089140.jpg"
        ),
        mapOf(
            "title" to "Dark Souls III",
            "genre" to "Action RPG",
            "publisher" to "Bandai Namco Entertainment",
            "releaseDate" to "2016-04-12",
            "description" to "A challenging and atmospheric dark fantasy RPG.",
            "rating" to 9.1,
            "coverImageUrl" to "https://image.api.playstation.com/cdn/EP0700/CUSA03365_00/OFMeAw2KhrdaEZAjW1f3tCIXbogkLpTC.png"
        ),
        mapOf(
            "title" to "Persona 5",
            "genre" to "RPG",
            "publisher" to "Atlus",
            "releaseDate" to "2016-09-15",
            "description" to "A stylish JRPG about teenage vigilantes in Tokyo.",
            "rating" to 9.5,
            "coverImageUrl" to "https://image.api.playstation.com/cdn/EP4062/CUSA06638_00/0fSaYhFhEVP183JLTwVec7qkzmaHNMS2.png"
        ),
        mapOf(
            "title" to "Super Mario Odyssey",
            "genre" to "Platformer",
            "publisher" to "Nintendo",
            "releaseDate" to "2017-10-27",
            "description" to "Mario explores diverse kingdoms in a 3D platforming adventure.",
            "rating" to 9.7,
            "coverImageUrl" to "https://a.allegroimg.com/s512/1146c7/1bf8946144ff9a10e0aea12358be/SUPER-MARIO-ODYSSEY-NINTENDO-SWITCH-NOWA-FOLIA"
        ),
        mapOf(
            "title" to "Fallout 4",
            "genre" to "RPG",
            "publisher" to "Bethesda Game Studios",
            "releaseDate" to "2015-11-10",
            "description" to "A post-apocalyptic RPG set in the Boston wasteland.",
            "rating" to 8.8,
            "coverImageUrl" to "https://image.api.playstation.com/vulcan/ap/rnd/202009/2500/D59jxQR99Jg545NKa4Nu1FmP.png"
        )
    )


    // Pętla dodająca gry do kolekcji "games"
    games.forEach { game ->
        db.collection("games")
            .add(game) // Automatyczne generowanie ID
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Dodano grę z ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Błąd podczas dodawania gry", e)
            }
    }
}
