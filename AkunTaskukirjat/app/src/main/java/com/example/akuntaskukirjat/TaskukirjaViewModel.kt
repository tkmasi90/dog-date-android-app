package com.example.akuntaskukirjat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class TaskukirjaViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: TaskukirjaRepository = TaskukirjaRepository(application)
    val mAlltaskukirjas: LiveData<List<Taskukirja>> = mRepository.getAllTaskukirjas()

    fun getAllTaskukirjas(): LiveData<List<Taskukirja>> {
        Log.d("TaskukirjaViewModel", "Get All tapahtui")
        return mAlltaskukirjas
    }

    fun insert(taskukirja: Taskukirja) {
        Log.d("TaskukirjaViewModel", "Insert tapahtui")
        mRepository.insert(taskukirja)
    }

    fun deleteAll() {
        mRepository.deleteAll()
    }

    fun deleteTaskukirja(taskukirja: Taskukirja) {
        mRepository.deleteTaskukirja(taskukirja)
    }
}