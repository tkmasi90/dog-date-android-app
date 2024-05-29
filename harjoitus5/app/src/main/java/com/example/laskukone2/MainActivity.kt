package com.example.laskukone2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var firstSum: EditText
    private lateinit var secondSum: EditText
    private lateinit var firstSub: EditText
    private lateinit var secondSub: EditText
    private lateinit var firstMult: EditText
    private lateinit var secondMult: EditText
    private lateinit var firstDiv: EditText
    private lateinit var secondDiv: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstSum = findViewById(R.id.sumFirst)
        secondSum = findViewById(R.id.sumSecond)
        firstSub = findViewById(R.id.subtractFirst)
        secondSub = findViewById(R.id.subtractSecond)
        firstMult = findViewById(R.id.multiplyFirst)
        secondMult = findViewById(R.id.multiplySecond)
        firstDiv = findViewById(R.id.divideFirst)
        secondDiv = findViewById(R.id.divideSecond)

        val sumButtonCount = findViewById<Button>(R.id.buttonSum)
        val subButtonCount = findViewById<Button>(R.id.buttonSubtract)
        val multButtonCount = findViewById<Button>(R.id.buttonMultiply)
        val divButtonCount = findViewById<Button>(R.id.buttonDivide)
        val logButton = findViewById<Button>(R.id.logButton)

        sumButtonCount.setOnClickListener {sumButtonPressed()}
        subButtonCount.setOnClickListener {subtractButtonPressed()}
        multButtonCount.setOnClickListener {multiplyButtonPressed()}
        divButtonCount.setOnClickListener {divideButtonPressed()}
        logButton.setOnClickListener{changeView()}
    }
    private fun sumButtonPressed() {
        val first: EditText = this.firstSum
        val second: EditText = this.secondSum
        if(!(firstSum.text.isEmpty() || secondSum.text.isEmpty())) {
            val result = sum()
            val formattedResult = formatResult(first, second, result)
            findViewById<TextView>(R.id.sumResult).text = formattedResult
            writeToFile("sum", first, second, formattedResult)
            emptyFields(first, second)
        }
    }

    private fun subtractButtonPressed() {
        val first: EditText = this.firstSub
        val second: EditText = this.secondSub
        if(!(first.text.isEmpty() || second.text.isEmpty())) {
            val result = subtract()
            val formattedResult = formatResult(first, second, result)
            findViewById<TextView>(R.id.subtractResult).text = formattedResult
            writeToFile("sub", first, second, formattedResult)
            emptyFields(first, second)
        }
    }

    private fun multiplyButtonPressed() {
        val first: EditText = this.firstMult
        val second: EditText = this.secondMult
        if(!(first.text.isEmpty() || second.text.isEmpty())) {
            val result = multiply()
            val formattedResult = formatResult(first, second, result)
            findViewById<TextView>(R.id.multiplyResult).text = formattedResult
            writeToFile("mult", first, second, formattedResult)
            emptyFields(first, second)
        }
    }

    private fun divideButtonPressed() {
        val first: EditText = this.firstDiv
        val second: EditText = this.secondDiv
        if(!(first.text.isEmpty() || second.text.isEmpty())) {
            val result = divide()
            val formattedResult = formatResult(first, second, result)
            findViewById<TextView>(R.id.divideResult).text = formattedResult
            writeToFile("div", first, second, formattedResult)
            emptyFields(first, second)
        }
    }

    private fun emptyFields(first:EditText, second:EditText) {
        first.text.clear()
        second.text.clear()
    }

    private fun formatResult(first:EditText, second:EditText, result: Double): String {
        return if(first.text.contains(".") || second.text.contains(".")) {
            result.toString()
        } else {
            result.toInt().toString()
        }
    }

    private fun sum(): Double {
        val result: Double = firstSum.text.toString().toDouble() + secondSum.text.toString().toDouble()
        return result
    }

    private fun subtract(): Double {
        val result: Double = firstSub.text.toString().toDouble() - secondSub.text.toString().toDouble()
        return result
    }

    private fun multiply(): Double {
        val result: Double = firstMult.text.toString().toDouble() * secondMult.text.toString().toDouble()
        return result
    }

    private fun divide(): Double {
        val result: Double = firstDiv.text.toString().toDouble() / secondDiv.text.toString().toDouble()
        return result
    }

    private fun writeToFile(operation: String, first: EditText, second: EditText, result: String) {
        val fileName = "laskinHistory.txt"
        val fileContents = when (operation) {
            "sum" -> "${first.text} + ${second.text} = $result\n"
            "sub" -> "${first.text} - ${second.text} = $result\n"
            "mult" -> "${first.text} * ${second.text} = $result\n"
            "div" -> "${first.text} / ${second.text} = $result\n"
            else -> ""
        }

        openFileOutput(fileName, MODE_APPEND).use {
            it.write(fileContents.toByteArray())
            println("File writing successful")
        }
    }

    private fun changeView() {
        val intent = Intent(this, LaskinLog::class.java)
        startActivity(intent)
    }
}