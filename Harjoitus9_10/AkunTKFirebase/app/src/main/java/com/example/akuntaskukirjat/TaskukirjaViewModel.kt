package com.example.akuntaskukirjat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskukirjaViewModel(application: Application) : AndroidViewModel(application) {
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user?.uid
    private var db: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var setSortByName = false
    private val _taskukirjas = MutableLiveData<List<Taskukirja>>()
    private val taskukirjas: LiveData<List<Taskukirja>> get() = _taskukirjas

    init {
        fetchTaskukirjas()
    }

    private fun fetchTaskukirjas() {
        if (uid != null) {
            db.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskukirjaList = mutableListOf<Taskukirja>()
                    for (taskukirjaSnapshot in snapshot.children) {
                        val taskukirja = taskukirjaSnapshot.getValue(Taskukirja::class.java)
                        taskukirja?.id = taskukirjaSnapshot.key
                        taskukirja?.let { taskukirjaList.add(it) }
                    }
                    _taskukirjas.value = if (setSortByName) {
                        taskukirjaList.sortedWith(compareBy({ it.name }, { it.number }))
                    } else {
                        taskukirjaList.sortedWith(compareBy({ it.number }, { it.name }))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TaskukirjaViewModel", "loadTaskukirjas:onCancelled", error.toException())
                }
            })
        }
    }

    fun getAllTaskukirjas(): LiveData<List<Taskukirja>> {
        return taskukirjas
    }

    fun insert(taskukirja: Taskukirja) {
        if (uid != null) {
            db.child(uid).push().setValue(taskukirja)
        }
    }

    fun deleteAll() {
        if (uid != null) {
            db.child(uid).removeValue()
        }
    }

    fun deleteTaskukirja(taskukirja: Taskukirja) {
        if (uid != null && taskukirja.id != null) {
            db.child(uid).child(taskukirja.id!!).removeValue()
        }
    }

    fun setSortByName(sortByName: Boolean) {
        setSortByName = sortByName
        fetchTaskukirjas()
    }
}