package com.example.eventappdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.model.EventDetail
import com.example.eventappdicoding.data.remote.ApiClient
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {

    private val apiService = ApiClient.instance

    private val _eventDetail = MutableLiveData<EventDetail?>()
    val eventDetail: LiveData<EventDetail?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _eventDetail.value = null // Clear previous detail
            try {
                val response = apiService.getEventDetail(eventId)
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.data
                    if (_eventDetail.value == null) {
                        _error.value = "Data detail event tidak ditemukan."
                    }
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failure: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}