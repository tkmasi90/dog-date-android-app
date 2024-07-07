package com.example.gps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Luodaan tallennettujen sijaintien näkymä
class SavedLocationsActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var clearSavedButton : FloatingActionButton
    private lateinit var backButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        clearSavedButton = findViewById(R.id.clearHistoryButton)
        backButton = findViewById(R.id.backButton)

        val sharedPrefs = SharedPreferences(application)
        val locationHistory = sharedPrefs.getLocationHistory().toMutableList()

        locationAdapter = LocationAdapter(locationHistory)
        mRecyclerView.adapter = locationAdapter

        clearSavedButton.setOnClickListener {
            sharedPrefs.emptyHistory()
            locationHistory.clear()
            locationAdapter.notifyDataSetChanged()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }
}