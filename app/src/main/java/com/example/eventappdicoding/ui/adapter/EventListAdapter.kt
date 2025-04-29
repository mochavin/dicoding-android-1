package com.example.eventappdicoding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // Import Coil
import coil.transform.RoundedCornersTransformation
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.databinding.ItemEventBinding // Import ViewBinding

class EventListAdapter(private val onItemClick: (EventItem) -> Unit) :
    ListAdapter<EventItem, EventListAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem) {
            binding.tvEventName.text = event.name
            binding.ivEventImage.load(event.displayImageUrl) {
                crossfade(true) // Animasi fade
                placeholder(R.drawable.ic_image_placeholder) // Gambar placeholder
                error(R.drawable.ic_broken_image) // Gambar jika error load
                transformations(RoundedCornersTransformation(0f,0f,0f,0f)) // Optional: corner radius
            }
        }
    }

    // DiffUtil untuk efisiensi update RecyclerView
    class EventDiffCallback : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }
    }
}