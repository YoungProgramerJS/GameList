import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FirebaseTest {

    private val db = mock<FirebaseFirestore>()
    private val collectionRef = mock<CollectionReference>()

    @Test
    fun `test addGamesToFirestore`() {
        val game = mapOf(
            "title" to "The Witcher 3: Wild Hunt",
            "genre" to "RPG",
            "publisher" to "CD Projekt Red",
            "releaseDate" to "2015-05-19",
            "description" to "An epic RPG adventure in the world of The Witcher.",
            "rating" to 9.7,
            "coverImageUrl" to "https://assetsio.gnwcdn.com/co5uct.jpg?width=1200&height=1200&fit=bounds&quality=70&format=jpg&auto=webp"
        )

        // Mockowanie metody collection, aby zwróciła mock CollectionReference
        whenever(db.collection("games")).thenReturn(collectionRef)

        // Mockowanie metody add w CollectionReference
        whenever(collectionRef.add(game)).thenReturn(mock())

        // Wywołanie metody, która dodaje grę
        db.collection("games").add(game)

        // Weryfikowanie, czy metoda add została wywołana na CollectionReference
        verify(collectionRef).add(game)
    }
}
