package com.example.eventappdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventappdicoding.data.model.EventItem
import com.example.eventappdicoding.data.remote.ApiClient
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val apiService = ApiClient.instance

    // LiveData untuk event aktif
    private val _activeEvents = MutableLiveData<List<EventItem>>()
    val activeEvents: LiveData<List<EventItem>> = _activeEvents

    private val _isLoadingActive = MutableLiveData<Boolean>()
    val isLoadingActive: LiveData<Boolean> = _isLoadingActive

    private val _errorActive = MutableLiveData<String?>()
    val errorActive: LiveData<String?> = _errorActive

    // LiveData untuk event selesai
    private val _finishedEvents = MutableLiveData<List<EventItem>>()
    val finishedEvents: LiveData<List<EventItem>> = _finishedEvents

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _errorFinished = MutableLiveData<String?>()
    val errorFinished: LiveData<String?> = _errorFinished


    fun fetchActiveEvents() {
        if (_activeEvents.value != null) return // Hindari load ulang jika data sudah ada
        viewModelScope.launch {
            _isLoadingActive.value = true
            _errorActive.value = null
            try {
                val response = apiService.getEvents(active = 1)
                if (response.isSuccessful) {
                    _activeEvents.value = response.body()?.data ?: emptyList()
                } else {
                    _errorActive.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorActive.value = "Failure: ${e.message}"
            } finally {
                _isLoadingActive.value = false
            }
        }
    }

    fun fetchFinishedEvents() {
        if (_finishedEvents.value != null) return // Hindari load ulang
        viewModelScope.launch {
            _isLoadingFinished.value = true
            _errorFinished.value = null
            try {
                val response = apiService.getEvents(active = 0)
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.data ?: emptyList()
                } else {
                    _errorFinished.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorFinished.value = "Failure: ${e.message}"
            } finally {
                _isLoadingFinished.value = false
            }
        }
    }
}