// ProductViewModel.kt
package com.example.assignment1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.DataClass.UiItem
import com.example.assignment1.Hilt.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    fun loadData() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val startTime = System.currentTimeMillis()

                val productsDeferred = async { repository.getProducts() }
                val userDeferred = async { repository.getUsers() }

                val products = productsDeferred.await()
                val users = userDeferred.await()

                val combinedList = mutableListOf<UiItem>()
                products.forEach { combinedList.add(UiItem.ProductItem(it)) }
                users.forEach { combinedList.add(UiItem.UserItem(it)) }

                _uiState.value = UiState.Success(combinedList)

                val endTime = System.currentTimeMillis()
                android.util.Log.d(
                    "TIME_CHECK",
                    "Total time: ${endTime - startTime} ms"
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val items: List<UiItem>) : UiState()
    data class Error(val message: String) : UiState()
}