package com.example.gps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val locationList: MutableList<Pair<Double, Double>>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val latTextView: TextView = view.findViewById(R.id.latitude)
        val lonTextView: TextView = view.findViewById(R.id.longitude)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.latTextView.text = "Lat: ${location.first}"
        holder.lonTextView.text = "Lon: ${location.second}"
    }
    
    override fun getItemCount() = locationList.size
}