package com.example.eventappdicoding.data.remote

import com.example.eventappdicoding.data.model.EventDetailResponse
import com.example.eventappdicoding.data.model.EventResponse
import retrofit2.Response // Import Response dari Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int // 1 for active, 0 for finished, -1 for search
        // @Query("q") query: String? = null // Untuk search nanti
    ): Response<EventResponse> // Gunakan Response<> untuk error handling

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") eventId: String
    ): Response<EventDetailResponse> // Gunakan Response<> untuk error handling
}