package com.example.assignment1.Hilt

import com.example.assignment1.DataClass.ProductDto
import com.example.assignment1.DataClass.UserDto
import javax.inject.Inject
import javax.inject.Named

class ProductRepository @Inject constructor(
    @Named("products") private val productApi: ApiService,
    @Named("users") private val userApi: ApiService
) {

    suspend fun getProducts(): List<ProductDto> {
        return productApi.getProducts().products
    }

    suspend fun getUsers(): List<UserDto> {
        return userApi.getUsers()
    }
}


