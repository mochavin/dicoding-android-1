package com.example.eventappdicoding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.databinding.ItemEventHorizontalBinding

class EventListHorizontalAdapter(private val onItemClick: (EventItem) -> Unit) :
    ListAdapter<EventItem, EventListHorizontalAdapter.EventHorizontalViewHolder>(EventListAdapter.EventDiffCallback()) { // Reuse DiffCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHorizontalViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHorizontalViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    inner class EventHorizontalViewHolder(private val binding: ItemEventHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem) {
            binding.tvEventName.text = event.name
            binding.ivEventImage.load(event.displayImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_broken_image)
                transformations(RoundedCornersTransformation(8f))
            }
        }
    }
}