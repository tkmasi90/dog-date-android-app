package com.example.akuntaskukirjat

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var mTaskukirjaViewModel: TaskukirjaViewModel

    private val newWordActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val word = data?.getStringExtra(NewTaskukirjaActivity.EXTRA_REPLY)

            word?.let {
                val split = it.split(";")
                val nro = split[0]
                val nimi = split[1]
                val painos = split[2]
                val pvm = split[3]
                val taskukirja = Taskukirja(nro.toInt(), nimi, painos, pvm)
                mTaskukirjaViewModel.insert(taskukirja)
            }
        } else {
            Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        mTaskukirjaViewModel = ViewModelProvider(this)[TaskukirjaViewModel::class.java]
        val adapter = TaskukirjaListAdapter(mTaskukirjaViewModel, TaskukirjaListAdapter.TaskukirjaDiff())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mTaskukirjaViewModel.getAllTaskukirjas().observe(this) { taskukirjas ->
            adapter.submitList(taskukirjas)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewTaskukirjaActivity::class.java)
            newWordActivityResultLauncher.launch(intent)
        }

        val buttonUusi = findViewById<Button>(R.id.buttonUusi)
        buttonUusi.setOnClickListener {
            val intent = Intent(this, NewTaskukirjaActivity::class.java)
            newWordActivityResultLauncher.launch(intent)
        }

        val buttonPoistaKaikki = findViewById<Button>(R.id.buttonPoistaKaikki)
        buttonPoistaKaikki.setOnClickListener {
            val dialogView = LayoutInflater.from(it.context).inflate(R.layout.custom_dialog, null)
            // Confirm deletion
            val builder = AlertDialog.Builder(it.context)
                .setView(dialogView)

            val dialog = builder.create()

            AlertDialog.Builder(it.context)
                .setMessage("Haluatko varmasti poistaa kaikki taskukirjat?")
                .setPositiveButton("Kyllä") { _, _ ->
                    mTaskukirjaViewModel.deleteAll()
                    Snackbar.make(it, "Poistettu kaikki taskukirjat", Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Ei") { _, _ ->
                    // Do nothing
                }
                .show()
        }

        val sortNumButton = findViewById<ImageView>(R.id.sortByNumber)
        sortNumButton.setOnClickListener {
            mTaskukirjaViewModel.setSortByName(false)
        }

        val sortNameButton = findViewById<ImageView>(R.id.sortByName)
        sortNameButton.setOnClickListener {
            mTaskukirjaViewModel.setSortByName(true)
        }

    }

}
