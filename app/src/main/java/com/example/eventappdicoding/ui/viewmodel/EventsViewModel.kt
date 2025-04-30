package com.example.eventappdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.repository.IEventRepository // Use Interface
import kotlinx.coroutines.launch

class EventsViewModel(private val repository: IEventRepository) : ViewModel() {

    // LiveData untuk event aktif
    private val _activeEvents = MutableLiveData<Resource<List<EventItem>>>()
    val activeEvents: LiveData<Resource<List<EventItem>>> = _activeEvents

    // LiveData untuk event selesai
    private val _finishedEvents = MutableLiveData<Resource<List<EventItem>>>()
    val finishedEvents: LiveData<Resource<List<EventItem>>> = _finishedEvents

    fun fetchActiveEvents() {
        // Avoid load ulang if data is already present and not an error
        if (_activeEvents.value is Resource.Success) return
        viewModelScope.launch {
            _activeEvents.value = Resource.Loading()
            _activeEvents.value = repository.getEvents(active = 1)
        }
    }

    fun fetchFinishedEvents() {
        // Avoid load ulang if data is already present and not an error
        if (_finishedEvents.value is Resource.Success) return
        viewModelScope.launch {
            _finishedEvents.value = Resource.Loading()
            _finishedEvents.value = repository.getEvents(active = 0)
        }
    }
}