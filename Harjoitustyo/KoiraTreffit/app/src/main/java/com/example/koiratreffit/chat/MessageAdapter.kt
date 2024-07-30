package com.example.koiratreffit.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.koiratreffit.R
import com.google.firebase.auth.FirebaseUser

private const val VIEW_TYPE_MESSAGE_SENT: Int = 1
private const val VIEW_TYPE_MESSAGE_RECEIVED: Int = 2

class MessageAdapter(private val messages: List<Viesti>, private val currentUser : FirebaseUser) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.lahettajaID == currentUser.uid) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    // ViewHolder lähetetylle viestille
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_message_body)
        private val dateText: TextView = itemView.findViewById(R.id.text_message_date)
        private val timeText: TextView = itemView.findViewById(R.id.text_message_time)

        // Sidotaan lähetetyn viestin tiedot näkymään
        fun bind(message: Viesti) {
            messageText.text = message.teksti
            dateText.text = message.paiva
            timeText.text = message.kello
        }
    }

    // ViewHolder vastaanotetulle viestille
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_message_body)
        private val dateText: TextView = itemView.findViewById(R.id.text_message_date)
        private val timeText: TextView = itemView.findViewById(R.id.text_message_time)
        private val senderName: TextView = itemView.findViewById(R.id.text_message_sender)

        // Sidotaan vastaanotetun viestin tiedot näkymään
        fun bind(message: Viesti) {
            messageText.text = message.teksti
            dateText.text = message.paiva
            timeText.text = message.kello
            senderName.text = message.lahettajaNimi
        }
    }
}
