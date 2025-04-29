package com.example.eventappdicoding.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.remote.ApiClient
import kotlinx.coroutines.launch

class SearchEventsViewModel : ViewModel() {

    private val apiService = ApiClient.instance

    private val _searchResults = MutableLiveData<List<EventItem>>()
    val searchResults: LiveData<List<EventItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun searchEvents(query: String) {
        // Avoid searching if query is blank or if already loading
        if (query.isBlank() || _isLoading.value == true) {
            // Optionally clear results or show specific message for blank query
            if(query.isBlank()) {
                _searchResults.value = emptyList()
                _error.value = null // Clear previous errors
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _searchResults.value = emptyList() // Clear previous results
            try {
                val response = apiService.getEvents(active = -1, query = query) // active = -1 for search
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.data ?: emptyList()
                    if (_searchResults.value.isNullOrEmpty()){
                        // If API call succeeded but returned no data, set error message for no results
                        _error.value = "no_results" // Use a special key or the actual string resource ID
                    }
                } else {
                    _error.value = "Error Search: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failure Search: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}