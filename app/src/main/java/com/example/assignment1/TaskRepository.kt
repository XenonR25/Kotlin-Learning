package com.example.assignment1

import com.example.assignment1.dataClass.Task
import com.example.assignment1.dataClass.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//The repository takes the dao as a dependency

class TaskRepository @Inject constructor(private val taskDao: TaskDao){
    //Expose the flow from the DAO
    val allTasks : Flow<List<Task>> = taskDao.getAllTasks()

    //These functions run on a background thread because of the suspend function

    suspend fun insert(task: Task) = taskDao.insert(task)
    suspend fun update(task : Task) = taskDao.update(task)
    suspend fun delete(task : Task) = taskDao.delete(task)
}