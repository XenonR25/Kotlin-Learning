package com.example.assignment1.Hilt

import com.example.assignment1.ProductResponse
import com.example.assignment1.DataClass.UserDto
import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun getProducts(): ProductResponse

    @GET("users")
    suspend fun getUsers(): List<UserDto>
}

