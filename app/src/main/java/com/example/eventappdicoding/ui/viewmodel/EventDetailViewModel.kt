package com.example.eventappdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.Resource
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.data.repository.IEventRepository // Use Interface
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repository: IEventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<Resource<EventDetail>>()
    val eventDetail: LiveData<Resource<EventDetail>> = _eventDetail

    fun fetchEventDetail(eventId: String) {
        // Only fetch if not already loading or success
        if (_eventDetail.value is Resource.Loading || _eventDetail.value is Resource.Success) {
            // Optionally allow refresh by checking a flag or comparing eventId
            // if (eventId != _eventDetail.value?.data?.id || _eventDetail.value is Resource.Error) { /* proceed */ }
            // For now, simple check: don't refetch if already loading/success
            // return
        }

        viewModelScope.launch {
            _eventDetail.value = Resource.Loading()
            _eventDetail.value = repository.getEventDetail(eventId)
        }
    }
}