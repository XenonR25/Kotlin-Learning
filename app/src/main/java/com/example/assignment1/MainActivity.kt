package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val username = email.substringBefore("@")

            if (email.isEmpty()) {
                etEmail.error = "Enter email"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Enter password"
                return@setOnClickListener
            }

            if (email.contains("@gmail.com")  && password == "123456") {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FoodActivityList::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("MainActivity","Called on start()")

    }

    override fun onResume() {
        super.onResume()
        Log.e("MainActivity","Called on onResume()")

    }

    override fun onPause() {
        super.onPause()
        Log.e("MainActivity","Called on onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.e("MainActivity","Called on onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("MainActivity","Called on onDestroy()")
    }
}