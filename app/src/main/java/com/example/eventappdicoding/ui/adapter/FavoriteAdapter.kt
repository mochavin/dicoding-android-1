package com.example.eventappdicoding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.eventappdicoding.R
import com.example.eventappdicoding.data.local.FavoriteEvent // Import FavoriteEvent
import com.example.eventappdicoding.databinding.ItemEventBinding // Reuse item layout

class FavoriteAdapter(private val onItemClick: (FavoriteEvent) -> Unit) :
    ListAdapter<FavoriteEvent, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteEvent = getItem(position)
        holder.bind(favoriteEvent)
        holder.itemView.setOnClickListener {
            onItemClick(favoriteEvent)
        }
    }

    inner class FavoriteViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEvent) {
            binding.tvEventName.text = event.name
            binding.ivEventImage.load(event.displayImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_broken_image)
                transformations(RoundedCornersTransformation(0f)) // Standard item view
            }
            // Maybe add date added or other info if desired
        }
    }

    // DiffUtil for FavoriteEvent
    class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteEvent>() {
        override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem == newItem
        }
    }
}