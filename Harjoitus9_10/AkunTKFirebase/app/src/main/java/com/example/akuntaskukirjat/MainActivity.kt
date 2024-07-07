package com.example.akuntaskukirjat

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mTaskukirjaViewModel: TaskukirjaViewModel

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: TaskukirjaListAdapter

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
                val sivuja = split[3]
                val pvm = split[4]
                val taskukirja = Taskukirja(nro.toInt(), nimi, painos, sivuja, pvm)
                mTaskukirjaViewModel.insert(taskukirja)
            }
        } else {
            Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize Firebase Auth and check if the user is signed in
        auth = Firebase.auth
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // Initialize ViewModel
        mTaskukirjaViewModel = ViewModelProvider(this)[TaskukirjaViewModel::class.java]

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = TaskukirjaListAdapter(mTaskukirjaViewModel, TaskukirjaListAdapter.TaskukirjaDiff())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe LiveData from ViewModel
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.sign_out_menu).isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}
