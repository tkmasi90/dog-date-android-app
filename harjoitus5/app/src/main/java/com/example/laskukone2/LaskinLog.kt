package com.example.laskukone2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LaskinLog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laskin_log)

        val logList: RecyclerView = findViewById(R.id.logList)
        val backButton: Button = findViewById(R.id.backButton)
        val logs: List<String>  = readFromFile()

        backButton.setOnClickListener{goBack()}

        logList.layoutManager = LinearLayoutManager(this)
        logList.adapter = LogAdapter(logs)

    }

    private fun readFromFile(): List<String> {
        println("reading from file")
        val fileName = "laskinHistory.txt"
        val file = File(filesDir, fileName)
        return if (file.exists()) {
            file.readLines()
        } else {
            println("LaskinHistory not found")
            emptyList()
        }
    }

    private fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}