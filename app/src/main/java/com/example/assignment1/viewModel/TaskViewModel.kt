package com.example.assignment1.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.TaskRepository
import com.example.assignment1.dataClass.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    val allTasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    //Launching in viewModelScope ensures that if the user leaves the screen,
    //the database operation is cancelled safely to prevent memory leaks
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }
    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }
    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)

    }

}