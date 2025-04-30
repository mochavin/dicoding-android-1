package com.example.eventappdicoding.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventappdicoding.data.remote.ApiClient
import com.example.eventappdicoding.data.repository.EventRepository
import com.example.eventappdicoding.data.repository.IEventRepository // Import interface
import com.example.eventappdicoding.ui.home.HomeViewModel
import com.example.eventappdicoding.ui.search.SearchEventsViewModel
import com.example.eventappdicoding.ui.viewmodel.EventDetailViewModel
import com.example.eventappdicoding.ui.viewmodel.EventsViewModel

// Use the IEventRepository interface type
class ViewModelFactory(private val repository: IEventRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Add cases for each ViewModel that needs the repository
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EventsViewModel::class.java) -> {
                EventsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EventDetailViewModel::class.java) -> {
                EventDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SearchEventsViewModel::class.java) -> {
                SearchEventsViewModel(repository) as T
            }
            // Add other ViewModels here...
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        // Use Context for potential future local data source needs
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                // Create the ApiService instance
                val apiService = ApiClient.instance
                // Create the Repository implementation
                val repository = EventRepository(apiService)
                // Create the factory instance
                INSTANCE = ViewModelFactory(repository)
                INSTANCE!!
            }
        }
    }
}