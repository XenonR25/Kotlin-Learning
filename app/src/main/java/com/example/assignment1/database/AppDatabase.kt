package com.example.assignment1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assignment1.dataClass.Task
import com.example.assignment1.dataClass.TaskDao


//1.@Database defines the list of Entities(tables) and the version number.
//If we change the table structure later,we must increase the version
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    //2.This connects our DAO to the database
    abstract fun taskDao() : TaskDao

    companion object{
        //3.@Volatile ensures the instance is always up to date across different threads
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase {
            //4.If an instance already exists,return it , if not, create
        return INSTANCE ?: synchronized(this){
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase :: class.java,
                "task_database" // the name of the SQLite file
            ).build()
            INSTANCE = instance
            instance
            }
        }
    }
}