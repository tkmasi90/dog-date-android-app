package com.example.akuntaskukirjat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Date


@Entity(tableName = "taskukirja_table")

class Taskukirja(@PrimaryKey
                 @ColumnInfo(name = "numero") val numero: Int,
                 @ColumnInfo(name = "nimi") val nimi: String,
                 @ColumnInfo(name = "painos") var painos: String,
                 @ColumnInfo(name = "pvm") val pvm: String)
