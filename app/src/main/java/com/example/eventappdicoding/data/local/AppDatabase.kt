package com.example.eventappdicoding.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false) // exportSchema = true is recommended for production apps for migrations
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database" // Database file name
                )
                    // Add migrations here if needed in the future
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}