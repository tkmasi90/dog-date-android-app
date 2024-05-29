package com.example.akuntaskukirjat

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class NewTaskukirjaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPLY = "com.example.android.roomtesti3112022.REPLY"
    }

    private lateinit var mEditNumeroView: EditText
    private lateinit var mEditNimiView: EditText
    private lateinit var mEditPainosView: EditText
    private lateinit var mEditPvmView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_taskukirja)

        mEditNumeroView = findViewById(R.id.edit_numero)
        mEditNimiView = findViewById(R.id.edit_nimi)
        mEditPainosView = findViewById(R.id.edit_painos)
        mEditPvmView = findViewById(R.id.edit_pvm)

        mEditPvmView.setOnClickListener {

            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(

                this,
                { _, year, monthOfYear, dayOfMonth ->

                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    mEditPvmView.setText(dat)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        val buttonSave = findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(mEditNumeroView.text) || TextUtils.isEmpty(mEditNimiView.text)) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                val word = "${mEditNumeroView.text};${mEditNimiView.text};${mEditPainosView.text};${mEditPvmView.text}"
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(RESULT_OK, replyIntent)
            }
            finish()
        }

        val buttonBack = findViewById<Button>(R.id.button_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
