package com.example.eventappdicoding.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteEvent: FavoriteEvent)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun deleteFavoriteById(eventId: String)

    @Query("SELECT * FROM favorite_events WHERE id = :eventId")
    fun getFavoriteById(eventId: String): LiveData<FavoriteEvent?> // Observe status

    @Query("SELECT * FROM favorite_events ORDER BY addedAt DESC")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>> // Observe list
}