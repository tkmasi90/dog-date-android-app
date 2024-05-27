package com.example.laskukone

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var firstNum: EditText
    private lateinit var secondNum: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNum = findViewById(R.id.editFirstNum)
        secondNum = findViewById(R.id.editSecondNum)
        val buttonCount = findViewById<Button>(R.id.buttonCount)

        buttonCount.setOnClickListener {buttonPressed()}
    }
    private fun buttonPressed() {
        if(firstNum.text.isNotEmpty() && secondNum.text.isNotEmpty()) {
            val firstValue = firstNum.text.toString().toDouble()
            val secondValue = secondNum.text.toString().toDouble()
            val result: Double = firstValue + secondValue

            val formattedResult = if (firstNum.text.contains(".") || secondNum.text.contains(".")) {
                result.toString()
            } else {
                result.toInt().toString()
            }

            findViewById<TextView>(R.id.textResult).text = formattedResult
            emptyFields()
        }
    }

    private fun emptyFields() {
        firstNum.text.clear()
        secondNum.text.clear()
    }
}