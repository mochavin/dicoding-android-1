package com.example.eventappdicoding.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.local.FavoriteEvent
import com.example.eventappdicoding.data.local.FavoriteEventDao
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

interface IEventRepository {
    // Remote
    suspend fun getEvents(active: Int, query: String? = null): Resource<List<EventItem>>
    suspend fun getEventDetail(eventId: String): Resource<EventDetail>

    // Local (Favorites)
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>
    fun getFavoriteStatus(eventId: String): LiveData<Boolean> // Simplified status check
    suspend fun addFavoriteEvent(event: EventDetail)
    suspend fun removeFavoriteEvent(eventId: String)
}

// Implementation
class EventRepository(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao // Inject DAO
) : IEventRepository {

    // --- Remote Operations ---
    override suspend fun getEvents(active: Int, query: String?): Resource<List<EventItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEvents(active = active, query = query)
                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        val data = response.body()?.data ?: emptyList()
                        Resource.Success(data)
                    } else {
                        Resource.Error(response.body()?.message ?: "API returned an error")
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

    // --- Local Favorite Operations ---

    override fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavorites()
    }

    // Observe the FavoriteEvent? and map it to Boolean LiveData using the extension function
    override fun getFavoriteStatus(eventId: String): LiveData<Boolean> {
        // Use the .map extension function directly on the LiveData returned by the DAO
        return favoriteEventDao.getFavoriteById(eventId).map { favoriteEvent ->
            favoriteEvent != null // True if favorite exists, false otherwise
        }
    }


    override suspend fun addFavoriteEvent(event: EventDetail) {
        withContext(Dispatchers.IO) {
            val favoriteEvent = FavoriteEvent(
                id = event.id,
                name = event.name,
                summary = event.summary,
                imageLogo = event.imageLogo,
                mediaCover = event.mediaCover,
                ownerName = event.ownerName,
                beginTime = event.beginTime,
                link = event.link
            )
            favoriteEventDao.insertFavorite(favoriteEvent)
        }
    }

    override suspend fun removeFavoriteEvent(eventId: String) {
        withContext(Dispatchers.IO) {
            favoriteEventDao.deleteFavoriteById(eventId)
        }
    }
}