package com.example.akuntaskukirjat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TaskukirjaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var TaskukirjaNroItemView : TextView = itemView.findViewById(R.id.textViewNro)
    private var TaskukirjaNimiItemView : TextView = itemView.findViewById(R.id.textViewnimi)

    private var delButton : Button = itemView.findViewById(R.id.buttonDelete)

    fun bind(taskukirja: Taskukirja) {
        TaskukirjaNroItemView.text = taskukirja.numero.toString()
        TaskukirjaNimiItemView.text = taskukirja.nimi
    }

    companion object {
        fun create(parent: ViewGroup): TaskukirjaViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            return TaskukirjaViewHolder(view)
        }
    }

    fun getDelButton(): Button {
        return this.delButton
    }

}