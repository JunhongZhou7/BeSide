package com.beside.app.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beside.app.data.model.ActivityEvent
import com.beside.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimelineViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _events = MutableStateFlow<List<ActivityEvent>>(emptyList())
    val events: StateFlow<List<ActivityEvent>> = _events.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch
                val partnerId = profile.partnerId ?: return@launch

                repository.observePartnerEvents(partnerId).collect { eventList ->
                    _events.value = eventList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
