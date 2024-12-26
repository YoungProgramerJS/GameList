package com.example.playlist2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity) // Przypisz layout ekranu logowania


        // Inicjalizacja Firebase Auth
        mAuth = FirebaseAuth.getInstance()



        val signInArrow = findViewById<ImageView>(R.id.registerArrow)
        signInArrow.setOnClickListener {
            registerUser()
        }



        // Find the TextView and set the click listener
        val createAccountTextView = findViewById<TextView>(R.id.haveAnAccountText)
        createAccountTextView.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (password == confirmPassword) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Rejestracja zakończona sukcesem
                        val user = mAuth.currentUser
                        // Przejdź do następnej aktywności
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()  // Zakończ aktywność rejestracji
                    } else {
                        // Jeśli rejestracja się nie udała
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
        }
    }
}
