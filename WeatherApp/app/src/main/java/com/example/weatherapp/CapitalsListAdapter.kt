package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.db.WeatherEntity
import kotlinx.android.synthetic.main.list_item.view.*

class CapitalsListAdapter(
    var items: List<WeatherEntity>
) : RecyclerView.Adapter<CapitalsListAdapter.CapitalsListViewHolder>() {
    class CapitalsListViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView)  {

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CapitalsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CapitalsListViewHolder(view, mListener)
    }

    override fun onBindViewHolder(
        holder: CapitalsListAdapter.CapitalsListViewHolder,
        position: Int
    ) {
        val list = mutableListOf<String>()
        for (item in items) {
            list.add(item.capital)
        }
        val sortedList = list.sorted()
        val curItem = sortedList[position]
        holder.itemView.tvCapital.text = curItem



    }

    override fun getItemCount(): Int {
        return items.size
    }

}