// UiItem.kt
package com.example.assignment1.DataClass

sealed class UiItem {
    data class ProductItem(val product: ProductDto) : UiItem()
    data class UserItem(val user: UserDto) : UiItem()
    object LoadingItem : UiItem()
    data class ErrorItem(val message: String) : UiItem()
}