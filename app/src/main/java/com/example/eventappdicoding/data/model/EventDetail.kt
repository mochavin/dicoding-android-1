package com.example.eventappdicoding.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone // Keep TimeZone import if needed for output formatting

data class EventDetailResponse(
    // Change 'status: String' to 'error: Boolean'
    @SerializedName("error")
    val error: Boolean, // API uses 'error' (boolean)

    @SerializedName("message")
    val message: String,

    // Change key from 'data' to 'event' using @SerializedName
    @SerializedName("event") // API uses 'event'
    val data: EventDetail? // Keep variable name 'data' for consistency in ViewModel, but map from 'event'
)

data class EventDetail(
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

    // Helper property needs adjustment for 'registrants'
    val remainingQuota: Int
        get() = quota - registrants

    // Helper function needs significant adjustment for date format
    fun getFormattedBeginTime(
        inputFormatString: String = "yyyy-MM-dd HH:mm:ss", // API format
        outputFormatString: String = "dd MMMM yyyy, HH:mm", // Desired output format
        outputLocale: Locale = Locale("id", "ID"), // Indonesian Locale
        displayTimeZone: String = "WIB" // Display timezone abbreviation
    ): String {
        return try {
            // Input format - Assume server time (often WIB/GMT+7 for Dicoding, but API doesn't specify)
            val inputFormat = SimpleDateFormat(inputFormatString, Locale.getDefault())
            // Note: If the API time is guaranteed UTC, set inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            // If it's local server time (e.g., WIB), no need to set input timezone unless you want to convert FROM it.

            val date: Date? = inputFormat.parse(beginTime)

            date?.let {
                // Output format - Format for display
                val outputFormat = SimpleDateFormat(outputFormatString, outputLocale)
                // If you want to display in a specific timezone different from input assumption, set it here:
                // outputFormat.timeZone = TimeZone.getTimeZone("GMT+7") // e.g., Ensure output is WIB

                "${outputFormat.format(it)} $displayTimeZone"
            } ?: "Waktu tidak valid"
        } catch (e: Exception) {
            // Log the exception for debugging
            android.util.Log.e("EventDetail", "Error parsing date: $beginTime", e)
            "Format waktu error"
        }
    }
}