package com.example.akuntaskukirjat

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Taskukirja::class], version = 3, exportSchema = false)
abstract class TaskukirjaRoomDatabase : RoomDatabase() {

    abstract fun taskukirjaDao(): TaskukirjaDao

    companion object {
        @Volatile
        private var INSTANCE: TaskukirjaRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDatabase(context: Context): TaskukirjaRoomDatabase {
            Log.d("TaskukirjaRoomDatabase", "INSTANCE retrieval")
            return INSTANCE ?: synchronized(this) {
                Log.d("TaskukirjaRoomDatabase", "Creating new INSTANCE")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskukirjaRoomDatabase::class.java,
                    "taskukirja_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}