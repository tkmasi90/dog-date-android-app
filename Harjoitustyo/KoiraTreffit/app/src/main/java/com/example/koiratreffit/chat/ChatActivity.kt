package com.example.koiratreffit.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.koiratreffit.R
import com.example.koiratreffit.koirapuistot.Koirapuisto
import com.example.koiratreffit.tapahtumat.Tapahtuma
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale


class ChatActivity : AppCompatActivity() {
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var classType: Any
    private lateinit var dbRef: DatabaseReference
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val key by lazy { intent.getStringExtra("key")!! }

    private lateinit var messageList: ArrayList<Viesti>
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList, user)
        messageRecyclerView.adapter = messageAdapter

        val classTypeString = intent.getStringExtra("classType")
        classType = when (classTypeString) {
            Koirapuisto::class.java.simpleName -> dbRef = FirebaseDatabase.getInstance().getReference("koirapuistot")
            Tapahtuma::class.java.simpleName -> dbRef = FirebaseDatabase.getInstance().getReference("tapahtumat")
            else -> throw IllegalArgumentException("Unknown class type")
        }

        CoroutineScope(Dispatchers.Main).launch {
            fetchMessages()
            addValueEventListener()
        }

        buttonSend.setOnClickListener {
            sendMessage(key)
        }
    }

    private suspend fun fetchMessages() {
        val snapshot = dbRef.child(key).child("viestit").get().await()
        val messages = addMessages(snapshot)
        messageList.clear()
        messageList.addAll(messages)
        messageAdapter.notifyDataSetChanged()
    }

    private fun addMessages(snapshot: DataSnapshot) : List<Viesti> {
        return snapshot.children.mapNotNull {
            val messageText = it.child("teksti").getValue(String::class.java)
            val senderID = it.child("lahettajaID").getValue(String::class.java)
            val senderName = it.child("lahettajaNimi").getValue(String::class.java)
            val date = it.child("paiva").getValue(String::class.java) ?: "Unknown"
            val time = it.child("kello").getValue(String::class.java) ?: "Unknown"

            if (messageText != null && senderID != null && senderName != null) {
                Viesti(messageText, senderID, senderName, date, time)
            } else {
                null
            }

        }
    }

    private fun addValueEventListener() {
        dbRef.child(key).child("viestit").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = addMessages(snapshot)
                messageList.clear()
                messageList.addAll(messages)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun sendMessage(targetID: String) {
        val messageText = editTextMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {

            val timestamp = System.currentTimeMillis()
            val time = SimpleDateFormat("hh:mm", Locale.getDefault())
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = date.format(timestamp)
            val formattedTime = time.format(timestamp)

            val message = Viesti(messageText, user.uid, user.displayName.toString(), formattedDate, formattedTime)
            dbRef.child(targetID).child("viestit").push().setValue(message)

            editTextMessage.text.clear()
        }
    }
}