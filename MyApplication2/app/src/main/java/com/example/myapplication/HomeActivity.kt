package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var userTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        welcomeTextView = findViewById(R.id.welcomeTextView)
        userTextView = findViewById(R.id.userTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Here you can retrieve the user's information from the database or the server-side API
        // and set the welcome message and the user name

        welcomeTextView.text = "Welcome!"
        userTextView.text = "User Name"

        logoutButton.setOnClickListener {
// Here you
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
