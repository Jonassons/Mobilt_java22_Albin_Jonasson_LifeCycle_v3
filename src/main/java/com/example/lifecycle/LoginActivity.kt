package com.example.lifecycle
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifecycle.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginlayout)

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Implement login logic and navigate to the next activity/fragment
        loginButton.setOnClickListener {
            // Retrieve the values entered by the user
            val enteredEmail = emailEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            // Query the Firebase Firestore collection to find the user
            db.collection("Users")
                .whereEqualTo("email", enteredEmail)
                .whereEqualTo("password", enteredPassword)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // User found, navigate to the ProfileActivity
                        val i = Intent(this@LoginActivity, ProfileActivity::class.java)
                        i.putExtra("email", enteredEmail)
                        i.putExtra("password", enteredPassword)
                        startActivity(i)
                    } else {
                        // User not found, show a toast message
                        Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during the query
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


