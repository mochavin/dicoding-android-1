package com.example.eventappdicoding.data.repository

import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

interface IEventRepository {
    suspend fun getEvents(active: Int, query: String? = null): Resource<List<EventItem>>
    suspend fun getEventDetail(eventId: String): Resource<EventDetail>
}

// Implementation
class EventRepository(private val apiService: ApiService) : IEventRepository {

    override suspend fun getEvents(active: Int, query: String?): Resource<List<EventItem>> {
        // Use withContext for IO-bound operations
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEvents(active = active, query = query)
                if (response.isSuccessful) {
                    // Check the API's internal error flag if necessary
                    if (response.body()?.error == false) {
                        val data = response.body()?.data ?: emptyList()
                        Resource.Success(data)
                    } else {
                        Resource.Error(response.body()?.message ?: "API returned an error")
                    }
                } else {
                    // Handle HTTP errors (4xx, 5xx)
                    Resource.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: HttpException) {
                // Specific Retrofit/HTTP exception
                Resource.Error("HTTP Error: ${e.message()}")
            } catch (e: IOException) {
                // Network errors (no connection, timeout)
                Resource.Error("Network Error: Please check your connection. (${e.message})")
            } catch (e: Exception) {
                // Generic catch-all for other unexpected errors
                Resource.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    override suspend fun getEventDetail(eventId: String): Resource<EventDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEventDetail(eventId)
                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        response.body()?.data?.let { detail ->
                            Resource.Success(detail)
                        } ?: Resource.Error("Event detail data is null")
                    } else {
                        Resource.Error(response.body()?.message ?: "API returned an error fetching detail")
                    }
                } else {
                    Resource.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: HttpException) {
                Resource.Error("HTTP Error: ${e.message()}")
            } catch (e: IOException) {
                Resource.Error("Network Error: Please check your connection. (${e.message})")
            } catch (e: Exception) {
                Resource.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }
}