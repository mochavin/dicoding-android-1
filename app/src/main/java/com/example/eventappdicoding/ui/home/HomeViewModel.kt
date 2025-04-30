package com.example.eventappdicoding.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.repository.IEventRepository // Use Interface
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: IEventRepository) : ViewModel() {

    private val maxItemsPerSection = 5 // Max items to show

    // LiveData for active events using Resource
    private val _activeEvents = MutableLiveData<Resource<List<EventItem>>>()
    val activeEvents: LiveData<Resource<List<EventItem>>> = _activeEvents

    // LiveData for finished events using Resource
    private val _finishedEvents = MutableLiveData<Resource<List<EventItem>>>()
    val finishedEvents: LiveData<Resource<List<EventItem>>> = _finishedEvents

    init {
        // Fetch data when ViewModel is created
        fetchHomeEvents()
    }

    fun fetchHomeEvents() {
        // Fetch both only if needed (e.g., on initial load or swipe refresh)
        // Check if data is null or if the current state is an error to allow retries
        if (_activeEvents.value?.data == null || _activeEvents.value is Resource.Error) {
            fetchActiveEventsForHome()
        }
        if (_finishedEvents.value?.data == null || _finishedEvents.value is Resource.Error) {
            fetchFinishedEventsForHome()
        }
    }

    private fun fetchActiveEventsForHome() {
        viewModelScope.launch {
            _activeEvents.value = Resource.Loading() // Set loading state
            val resource = repository.getEvents(active = 1, query = null)
            // Post the result (Success or Error)
            if (resource is Resource.Success) {
                // Limit the items after fetching successfully
                _activeEvents.value = Resource.Success(resource.data?.take(maxItemsPerSection) ?: emptyList())
            } else {
                _activeEvents.value = resource // Post error directly
            }
        }
    }

    private fun fetchFinishedEventsForHome() {
        viewModelScope.launch {
            _finishedEvents.value = Resource.Loading() // Set loading state
            val resource = repository.getEvents(active = 0, query = null)
            // Post the result (Success or Error)
            if (resource is Resource.Success) {
                // Limit the items after fetching successfully
                _finishedEvents.value = Resource.Success(resource.data?.take(maxItemsPerSection) ?: emptyList())
            } else {
                _finishedEvents.value = resource // Post error directly
            }
        }
    }
}