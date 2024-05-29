package com.example.akuntaskukirjat

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.snackbar.Snackbar

class TaskukirjaListAdapter(
    private val viewModel: TaskukirjaViewModel, // Add the ViewModel to the constructor
    diffCallback: DiffUtil.ItemCallback<Taskukirja>
) : ListAdapter<Taskukirja, TaskukirjaViewHolder>(diffCallback) {

    private val TAG = "mun softa - adapter"
    private var taskukirja: Taskukirja? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskukirjaViewHolder {
        return TaskukirjaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskukirjaViewHolder, position: Int) {
        val current = getItem(position)
        Log.d(TAG, "onBindViewHolder() -  ${current.numero}")
        holder.bind(current)

        holder.itemView.setOnClickListener { v ->
            taskukirja = current

            val dialogView = LayoutInflater.from(v.context).inflate(R.layout.custom_dialog, null)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
            val dialogContent = dialogView.findViewById<TextView>(R.id.dialog_content)
            val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

            dialogTitle.text = "Taskukirjan tiedot"
            dialogContent.text = buildString {
                append("Taskukirjan numero: ${current.numero}\n")
                append("Taskukirjan nimi: ${current.nimi}\n")
                append("Painos: ${current.painos}\n")
                append("Hankinta päivä: ${current.pvm}")
            }

            val builder = AlertDialog.Builder(v.context)
                .setView(dialogView)

            val dialog = builder.create()

            dialogButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        val button: Button = holder.getDelButton()
        button.setOnClickListener { view ->
            taskukirja = current
            viewModel.deleteTaskukirja(current)
            Snackbar.make(view, "Poistettu: ${current.numero} - ${current.nimi}", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    fun getTaskukirja(): Taskukirja? {
        return taskukirja
    }

    class TaskukirjaDiff : DiffUtil.ItemCallback<Taskukirja>() {
        override fun areItemsTheSame(oldItem: Taskukirja, newItem: Taskukirja): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Taskukirja, newItem: Taskukirja): Boolean {
            return oldItem.numero == newItem.numero
        }
    }
}
