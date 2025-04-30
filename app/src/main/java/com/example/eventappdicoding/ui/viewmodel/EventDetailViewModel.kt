package com.example.eventappdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap // Import switchMap
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.data.repository.IEventRepository // Use Interface
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repository: IEventRepository) : ViewModel() {

    private val _eventId = MutableLiveData<String>() // Store current event ID

    // LiveData for event detail fetched from API
    val eventDetail: LiveData<Resource<EventDetail>> = _eventId.switchMap { id ->
        // Use switchMap to re-fetch if eventId changes
        // But only fetch once per ID, unless forced
        fetchEventDetailInternal(id)
    }

    // LiveData for favorite status from Database
    val isFavorite: LiveData<Boolean> = _eventId.switchMap { id ->
        repository.getFavoriteStatus(id)
    }

    // Internal LiveData to hold the fetched detail result
    private fun fetchEventDetailInternal(eventId: String): LiveData<Resource<EventDetail>> {
        val liveData = MutableLiveData<Resource<EventDetail>>()
        // Avoid refetching if already loading or success for the *same* ID
        val currentDetail = eventDetail.value
        if (currentDetail is Resource.Loading || (currentDetail is Resource.Success && currentDetail.data?.id == eventId)) {
            // If already loading or successfully loaded the same ID, return the existing LiveData
            // This requires eventDetail to be initialized before this check runs, might need adjustment
            // A simpler approach for now: always fetch if ID changes via switchMap trigger
            // return eventDetail // Need careful state management here
        }

        viewModelScope.launch {
            liveData.value = Resource.Loading()
            liveData.value = repository.getEventDetail(eventId)
        }
        return liveData
    }

    // Public function called by Fragment to set the event ID
    fun setEventId(eventId: String) {
        if (_eventId.value == eventId) return // Don't trigger if ID is the same
        _eventId.value = eventId
    }


    // Called when the favorite button is clicked
    fun toggleFavorite() {
        viewModelScope.launch {
            val currentDetailResource = eventDetail.value
            val currentFavoriteStatus = isFavorite.value ?: false

            if (currentDetailResource is Resource.Success && currentDetailResource.data != null) {
                val detail = currentDetailResource.data
                if (currentFavoriteStatus) {
                    // Currently favorite, so remove it
                    repository.removeFavoriteEvent(detail.id)
                } else {
                    // Currently not favorite, so add it
                    repository.addFavoriteEvent(detail)
                }
                // The isFavorite LiveData will update automatically due to DB changes
            } else {
                // Handle error or loading state - cannot toggle favorite
                // Log.e("EventDetailViewModel", "Cannot toggle favorite, detail not loaded.")
            }
        }
    }
}