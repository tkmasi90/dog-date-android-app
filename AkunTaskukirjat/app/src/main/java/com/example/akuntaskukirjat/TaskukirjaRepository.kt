package com.example.akuntaskukirjat

import android.app.Application
import androidx.lifecycle.LiveData

class TaskukirjaRepository(application : Application) {
    val db = TaskukirjaRoomDatabase.getDatabase(application)
    val mTaskukirjaDao = db.taskukirjaDao()
    val mAllTaskukirjas = mTaskukirjaDao.getNumberOrderedTaskukirjas()

    fun getAllTaskukirjas(): LiveData<List<Taskukirja>> {
        return mAllTaskukirjas
    }
    fun insert(taskukirja: Taskukirja) {
        TaskukirjaRoomDatabase.databaseWriteExecutor.execute {
            mTaskukirjaDao.insert(taskukirja)
        }
    }
    fun deleteAll() {
        TaskukirjaRoomDatabase.databaseWriteExecutor.execute {
            mTaskukirjaDao.deleteAll()
        }
    }

    fun deleteTaskukirja(taskukirja: Taskukirja) {
        TaskukirjaRoomDatabase.databaseWriteExecutor.execute {
            mTaskukirjaDao.deleteTaskukirja(taskukirja)
        }
    }
}