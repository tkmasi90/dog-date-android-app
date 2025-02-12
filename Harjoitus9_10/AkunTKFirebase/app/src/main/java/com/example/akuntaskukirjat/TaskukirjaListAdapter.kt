package com.example.akuntaskukirjat

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.snackbar.Snackbar

class TaskukirjaListAdapter(
    private val viewModel: TaskukirjaViewModel, // Add the ViewModel to the constructor
    diffCallback: DiffUtil.ItemCallback<Taskukirja>,
    private val TAG: String = "TaskukirjaListAdapter", private var taskukirja: Taskukirja? = null
) : ListAdapter<Taskukirja, TaskukirjaViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskukirjaViewHolder {
        return TaskukirjaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskukirjaViewHolder, position: Int) {
        val current = getItem(position)
        Log.d(TAG, "onBindViewHolder() -  ${current.number}")
        holder.bind(current)

        holder.itemView.setOnClickListener { v ->
            val dialogView = LayoutInflater.from(v.context).inflate(R.layout.custom_dialog, null)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
            val dialogContent = dialogView.findViewById<TextView>(R.id.dialog_content)
            val dialogButton = dialogView.findViewById<Button>(R.id.dialog_button)

            dialogTitle.text = buildString {
                append("Taskukirjan tiedot")
            }
            dialogContent.text = current.toString()

            val builder = AlertDialog.Builder(v.context)
                .setView(dialogView)

            val dialog = builder.create()

            dialogButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        val delButton = holder.getDelButton()
        delButton.setOnClickListener { v ->
            val dialogView = LayoutInflater.from(v.context).inflate(R.layout.custom_dialog, null)
            // Confirm deletion
            val builder = AlertDialog.Builder(v.context)
                .setView(dialogView)

            val dialog = builder.create()
            AlertDialog.Builder(v.context)
                .setMessage("Haluatko varmasti poistaa tämän taskukirjan?")
                .setPositiveButton("Kyllä") { _, _ ->
                    taskukirja = current
                    viewModel.deleteTaskukirja(current)
                    Snackbar.make(v, "Poistettu: ${current.number} - ${current.name}", Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Ei") { _, _ ->
                    // Do nothing
                }
                .show()
        }
    }

    class TaskukirjaDiff : DiffUtil.ItemCallback<Taskukirja>() {
        override fun areItemsTheSame(oldItem: Taskukirja, newItem: Taskukirja): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Taskukirja, newItem: Taskukirja): Boolean {
            return oldItem.number == newItem.number
        }
    }
}
