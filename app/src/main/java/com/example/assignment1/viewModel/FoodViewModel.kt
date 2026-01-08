package com.example.assignment1.viewModel

import androidx.lifecycle.ViewModel
import com.example.assignment1.Food
import com.example.assignment1.Hilt.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _foodList = MutableStateFlow<List<Food>>(emptyList())
    val foodList: StateFlow<List<Food>> = _foodList

    private val _buttonText = MutableStateFlow("Show Food")
    val buttonText: StateFlow<String> = _buttonText

    private var isShown = false

    fun toggle() {
        if (isShown) {
            _buttonText.value = "Show Food"
            _foodList.value = emptyList()
        } else {
            _buttonText.value = "Hide Food"
            _foodList.value = repository.getFoodList()
        }
        isShown = !isShown
    }
}
