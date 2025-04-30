package com.example.eventappdicoding.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.eventappdicoding.data.local.FavoriteEvent
import com.example.eventappdicoding.data.repository.IEventRepository

class FavoriteViewModel(private val repository: IEventRepository) : ViewModel() {

    val allFavoriteEvents: LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvents()

}