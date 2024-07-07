package com.example.akuntaskukirjat

import android.util.Log
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class Taskukirja() : DatabaseReference.CompletionListener {
    var number: Int = 0
    var name: String = ""
    var edition: String = ""
    var pages: String = ""
    var date: String = ""
    var id: String? = null

    constructor(number: Int, name: String, edition: String, pages: String, date: String) : this() {
        this.number = number
        this.name = name
        this.edition = edition
        this.pages = pages
        this.date = date
        this.id = null
    }

    override fun toString(): String {

        return buildString {
            append("Taskukirjan numero: $number\n")
            append("Taskukirjan nimi: $name\n")
            append("Painos: $edition\n")
            append("Sivumäärä: $pages\n")
            append("Hankinta päivä: $date")
        }
    }

    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
        if (error != null) {
            Log.e("Taskukirja", "Database operation failed", error.toException())
        } else {
            Log.d("Taskukirja", "Database operation succeeded")
        }
    }
}
