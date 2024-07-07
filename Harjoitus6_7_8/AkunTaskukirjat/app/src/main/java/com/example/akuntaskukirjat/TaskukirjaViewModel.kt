package com.example.akuntaskukirjat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class TaskukirjaViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: TaskukirjaRepository = TaskukirjaRepository(application)
    // ChatGPT:n kirjoittama koodi alkaa
    private var sortByName = MutableLiveData<Boolean>()
    private val _mAllTaskukirjas = MediatorLiveData<List<Taskukirja>>()

    init {
        this.sortByName.value = false
        _mAllTaskukirjas.addSource(mRepository.getAllTaskukirjas(false)) { taskukirjas ->
            if (this.sortByName.value == false) {
                _mAllTaskukirjas.value = taskukirjas
            }
        }
        _mAllTaskukirjas.addSource(mRepository.getAllTaskukirjas(true)) { taskukirjas ->
            if (this.sortByName.value == true) {
                _mAllTaskukirjas.value = taskukirjas
            }
        }
    }

    val mAlltaskukirjas: LiveData<List<Taskukirja>> = _mAllTaskukirjas

    fun setSortByName(sortByName: Boolean) {
        this.sortByName.value = sortByName
        if (sortByName) {
            Log.d("TaskukirjaViewModel", "SortByName = true")
            _mAllTaskukirjas.value = mRepository.getAllTaskukirjas(true).value
        } else {
            Log.d("TaskukirjaViewModel", "SortByName = false")
            _mAllTaskukirjas.value = mRepository.getAllTaskukirjas(false).value
        }
    }
    // ChatGPT:n koodi loppuu

    fun getAllTaskukirjas(): LiveData<List<Taskukirja>> {
        Log.d("TaskukirjaViewModel", "Get All tapahtui")
        return mAlltaskukirjas
    }

    fun insert(taskukirja: Taskukirja) {
        Log.d("TaskukirjaViewModel", "Insert tapahtui")
        mRepository.insert(taskukirja)
    }

    fun deleteAll() {
        Log.d("TaskukirjaViewModel", "Delete All tapahtui")
        mRepository.deleteAll()
    }

    fun deleteTaskukirja(taskukirja: Taskukirja) {
        Log.d("TaskukirjaViewModel", "Delete Taskukirja tapahtui")
        mRepository.deleteTaskukirja(taskukirja)
    }
}