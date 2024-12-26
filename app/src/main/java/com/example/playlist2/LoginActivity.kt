package com.example.playlist2

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity) // Make sure to reference your login layout


        mAuth = FirebaseAuth.getInstance()


        val signInArrow = findViewById<ImageView>(R.id.signInArrow)
        signInArrow.setOnClickListener {
            loginUser()
        }

        // Find the TextView and set the click listener
        val createAccountTextView = findViewById<TextView>(R.id.createAccountText)
        createAccountTextView.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)


        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email i hasło nie mogą być puste", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Logowanie zakończone sukcesem
                    val user = mAuth.currentUser
                    // Przejdź do głównej aktywności
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()  // Zakończ aktywność logowania
                } else {
                    // Jeśli logowanie się nie udało
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
