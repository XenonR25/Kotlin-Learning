package com.example.assignment1.Hilt

import com.example.assignment1.Food
import com.example.assignment1.R
import javax.inject.Inject

class FoodRepository @Inject constructor() {

    fun getFoodList(): List<Food> {
        return listOf(
            Food("Pizza", "Cheesy Italian pizza", 9.99, R.drawable.pizza),
            Food("Burger", "Juicy beef burger", 7.49, R.drawable.burger),
            Food("Dumpling", "Creamy white sauce pasta", 8.25, R.drawable.dumpling),
            Food("Nachos", "Creamy and cheesy nachos", 8.25, R.drawable.nachos),
            Food("Fried Chicken", "Crispy on the outside, tender on the inside", 8.25, R.drawable.fried),
            Food("Korean Wings", "Spicy korean wings", 8.25, R.drawable.wings),
            Food("Sushi", "Wasabi induced tuna sushi", 8.25, R.drawable.sushi),
            Food("Dumpling", "Creamy white sauce pasta", 8.25, R.drawable.dumpling),
            Food("Sushi", "Wasabi induced tuna sushi", 8.25, R.drawable.sushi),
            Food("Fried Chicken", "Crispy on the outside, tender on the inside", 8.25, R.drawable.fried),
            Food("Sushi", "Wasabi induced tuna sushi", 8.25, R.drawable.sushi)
        )
    }
}
