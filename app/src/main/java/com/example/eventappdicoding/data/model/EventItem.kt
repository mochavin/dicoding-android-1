package com.example.eventappdicoding.data.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    // Change 'status: String' to 'error: Boolean'
    @SerializedName("error")
    val error: Boolean, // API uses 'error' (boolean)

    @SerializedName("message")
    val message: String,

    // Change key from 'data' to 'listEvents' using @SerializedName
    @SerializedName("listEvents") // API uses 'listEvents'
    val data: List<EventItem>? // Keep variable name 'data' for consistency in ViewModel, but map from 'listEvents'
)

// EventItem can remain as is for basic list display, but be aware it's missing many fields from the actual API response if needed later.
data class EventItem(
    @SerializedName("id")
    val id: String, // Keep as String
    @SerializedName("name")
    val name: String,
    @SerializedName("summary") // Add missing field
    val summary: String?,
    @SerializedName("description")
    val description: String,
    @SerializedName("imageLogo")
    val imageLogo: String?,
    @SerializedName("mediaCover")
    val mediaCover: String?,
    @SerializedName("category") // Add missing field
    val category: String?,
    @SerializedName("ownerName")
    val ownerName: String,
    @SerializedName("cityName") // Add missing field
    val cityName: String?,
    @SerializedName("quota")
    val quota: Int,
    // Fix naming mismatch: API uses 'registrants'
    @SerializedName("registrants")
    val registrants: Int,
    @SerializedName("beginTime")
    val beginTime: String, // Format: "YYYY-MM-DD HH:MM:SS"
    @SerializedName("endTime") // Add missing field
    val endTime: String?,   // Format: "YYYY-MM-DD HH:MM:SS"
    @SerializedName("link")
    val link: String
) {
    // Helper property remains the same
    val displayImageUrl: String?
        get() = mediaCover ?: imageLogo
}