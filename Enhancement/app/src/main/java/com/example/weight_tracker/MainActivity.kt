package com.example.weight_tracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val createAccountBtn = findViewById<Button>(R.id.btnCreateAccount)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Login
        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Look for a password saved under this specific email key
            val savedPass = prefs.getString(email, null)

            if (savedPass != null && savedPass == pass) {
                // Mark as logged in and save which user is logged in
                prefs.edit().apply {
                    putBoolean("isLoggedIn", true)
                    putString("lastLoggedInUser", email)
                    apply()
                }
                navigateToHome()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Creat account
        createAccountBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            // Validate Email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 4) {
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user already exists
            if (prefs.contains(email)) {
                Toast.makeText(this, "User already exists! Try logging in.", Toast.LENGTH_SHORT).show()
            } else {
                // Save new user; email = key, pass = value
                prefs.edit().putString(email, pass).apply()
                Toast.makeText(this, "Account Created! You can now log in.", Toast.LENGTH_SHORT).show()

                // Clear fields
                emailInput.text.clear()
                passwordInput.text.clear()
            }
        }

        // If already logged in, skip this screen
        if (prefs.getBoolean("isLoggedIn", false)) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}