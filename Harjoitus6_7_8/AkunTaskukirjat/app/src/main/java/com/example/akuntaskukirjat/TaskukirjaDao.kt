package com.example.akuntaskukirjat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TaskukirjaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(taskukirja: Taskukirja)

    @Query("DELETE FROM taskukirja_table")
    fun deleteAll()

    @Query("SELECT * FROM taskukirja_table ORDER BY numero ASC")
    fun getNumberOrderedTaskukirjas(): LiveData<List<Taskukirja>>

    @Query("SELECT * FROM taskukirja_table ORDER BY LOWER(nimi) ASC")
    fun getNameOrderedTaskukirjas(): LiveData<List<Taskukirja>>

    @Delete
    fun deleteTaskukirja(taskukirja: Taskukirja)
}
