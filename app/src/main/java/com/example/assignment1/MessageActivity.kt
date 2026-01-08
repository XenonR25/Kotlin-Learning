package com.example.assignment1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity



class MessageActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_message)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment())
            .commit()

    }
}





