package com.example.eventappdicoding.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey
    val id: String,
    val name: String,
    val summary: String?,
    val imageLogo: String?,
    val mediaCover: String?,
    val ownerName: String,
    val beginTime: String,
    val link: String,
    val addedAt: Long = System.currentTimeMillis() // Track when it was favorited
) {
    // Helper property consistent with EventItem/EventDetail
    val displayImageUrl: String?
        get() = mediaCover ?: imageLogo
}