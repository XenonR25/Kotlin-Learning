package com.example.assignment1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodDetailsViewModel : ViewModel(){
    private val _isFavourite = MutableLiveData(false)
    val isFavourite : LiveData<Boolean> = _isFavourite

    fun toggleFavourite(){
        _isFavourite.value = !(_isFavourite.value ?: false)
    }
}