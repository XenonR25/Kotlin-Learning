package com.example.assignment1.dataClass

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String,
    val description : String,
    val isComplete : Boolean = false
)
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task : Task)

    @Update
    suspend fun update(task : Task)

    @Delete
    suspend fun delete(task : Task)

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks() : Flow<List<Task>>
}