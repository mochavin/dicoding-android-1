package com.example.eventappdicoding.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.remote.ApiClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiService = ApiClient.instance
    private val maxItemsPerSection = 5 // Max items to show

    // LiveData for active events
    private val _activeEvents = MutableLiveData<List<EventItem>>()
    val activeEvents: LiveData<List<EventItem>> = _activeEvents

    private val _isLoadingActive = MutableLiveData<Boolean>()
    val isLoadingActive: LiveData<Boolean> = _isLoadingActive

    private val _errorActive = MutableLiveData<String?>()
    val errorActive: LiveData<String?> = _errorActive

    // LiveData for finished events
    private val _finishedEvents = MutableLiveData<List<EventItem>>()
    val finishedEvents: LiveData<List<EventItem>> = _finishedEvents

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _errorFinished = MutableLiveData<String?>()
    val errorFinished: LiveData<String?> = _errorFinished

    init {
        // Fetch data when ViewModel is created
        fetchHomeEvents()
    }


    fun fetchHomeEvents() {
        // Fetch both only if needed (e.g., on initial load or swipe refresh)
        if (_activeEvents.value == null) fetchActiveEventsForHome()
        if (_finishedEvents.value == null) fetchFinishedEventsForHome()
    }


    private fun fetchActiveEventsForHome() {
        viewModelScope.launch {
            _isLoadingActive.value = true
            _errorActive.value = null
            try {
                // Assume ApiService is modified to accept null query
                val response = apiService.getEvents(active = 1, query = null)
                if (response.isSuccessful) {
                    val allActive = response.body()?.data ?: emptyList()
                    _activeEvents.value = allActive.take(maxItemsPerSection)
                } else {
                    _errorActive.value = "Error Active: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorActive.value = "Failure Active: ${e.message}"
            } finally {
                _isLoadingActive.value = false
            }
        }
    }

    private fun fetchFinishedEventsForHome() {
        viewModelScope.launch {
            _isLoadingFinished.value = true
            _errorFinished.value = null
            try {
                // Assume ApiService is modified to accept null query
                val response = apiService.getEvents(active = 0, query = null)
                if (response.isSuccessful) {
                    val allFinished = response.body()?.data ?: emptyList()
                    _finishedEvents.value = allFinished.take(maxItemsPerSection)
                } else {
                    _errorFinished.value = "Error Finished: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorFinished.value = "Failure Finished: ${e.message}"
            } finally {
                _isLoadingFinished.value = false
            }
        }
    }
}