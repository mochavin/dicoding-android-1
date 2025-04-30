package com.example.eventappdicoding.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.repository.IEventRepository
import kotlinx.coroutines.launch

class SearchEventsViewModel(private val repository: IEventRepository) : ViewModel() {

    private val _searchResults = MutableLiveData<Resource<List<EventItem>>>()
    val searchResults: LiveData<Resource<List<EventItem>>> = _searchResults

    fun searchEvents(query: String) {
        if (query.isBlank()) {
            // Clear results or show specific message for blank query
            _searchResults.value = Resource.Success(emptyList()) // Show empty state explicitly
            return
        }
        // Avoid searching if already loading
        if (_searchResults.value is Resource.Loading) return

        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            val resource = repository.getEvents(active = -1, query = query) // active = -1 for search
            _searchResults.value = resource // Post result (Success or Error)

            // Optional: Post a specific error if Success but empty list?
            // The UI layer is often better suited to interpret "Success + Empty List"
            // if (resource is Resource.Success && resource.data.isNullOrEmpty()){
            //      _searchResults.value = Resource.Error("No results found.", emptyList())
            // }
        }
    }
}