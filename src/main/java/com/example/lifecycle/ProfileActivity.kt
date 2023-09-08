package com.example.lifecycle
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var ageEditText: EditText
    private lateinit var driverLicenseCheckBox: CheckBox
    private lateinit var radioGroup: RadioGroup
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button



    // Initialize Firebase Firestore
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI components
        ageEditText = findViewById(R.id.ageEditText)
        driverLicenseCheckBox = findViewById(R.id.driverLicenseCheckBox)
        radioGroup = findViewById(R.id.radioGroup)
        emailEditText = findViewById(R.id.emailEditText)
        saveButton = findViewById(R.id.saveButton)




        val backButton = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {

            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Get user data from the LoginActivity
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        // Retrieve user data based on email and password
        db.collection("Users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // User data found, update UI with user information
                    val user = querySnapshot.documents[0].data
                    if (user != null) {
                        val userAge = user["age"].toString()

                        // Handle the 'has_license' field safely
                        val hasLicense = user["has_license"] as? Boolean ?: false

                        // Display user data in the UI components
                        ageEditText.setText(userAge)
                        driverLicenseCheckBox.isChecked = hasLicense
                        emailEditText.setText(email)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error here (e.g., show an error message)
                showError("Error retrieving user data: ${exception.message}")
            }

        // Implement code for saving user data
        saveButton.setOnClickListener {
            // Retrieve data from UI components
            val age = ageEditText.text.toString()
            val updatedHasLicense = driverLicenseCheckBox.isChecked
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedChoice = selectedRadioButton.text.toString()

            // Perform data validation
            if (isValidAge(age)) {
                // Update the user data in Firebase Firestore
                db.collection("Users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // User data found, update the data
                            val userDoc = querySnapshot.documents[0].reference
                            userDoc.update("age", age.toInt())
                            userDoc.update("has_license", updatedHasLicense)
                            userDoc.update("radio_choices", selectedChoice)
                            // Display a success message
                            showSuccess("Data saved successfully")

                            // Optionally, you can navigate to another screen here
                        }
                    }
            } else {
                // Display an error message if data is invalid
                showError("Invalid age. Please check your input.")
            }
        }
    }

    private fun isValidAge(age: String): Boolean {
        return age.isNotBlank() && age.toIntOrNull() != null
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
