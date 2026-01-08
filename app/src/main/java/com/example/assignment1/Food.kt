package com.example.assignment1

import java.io.Serializable

data class Food(
    val name: String,
    val description: String,
    val price: Double,
    val imageRes: Int
) : Serializable
