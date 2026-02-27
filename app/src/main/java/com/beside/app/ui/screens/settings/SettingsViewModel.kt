package com.beside.app.ui.screens.settings

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beside.app.data.model.PrivacyLevel
import com.beside.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationPrivacy: PrivacyLevel = PrivacyLevel.APP_ONLY,
    val locationPrivacy: PrivacyLevel = PrivacyLevel.APP_ONLY,
    val allowedApps: List<String> = emptyList()
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch
                _uiState.value = SettingsUiState(
                    notificationPrivacy = profile.notificationPrivacy,
                    locationPrivacy = profile.locationPrivacy,
                    allowedApps = profile.allowedApps
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateNotificationPrivacy(level: PrivacyLevel) {
        _uiState.update { it.copy(notificationPrivacy = level) }
        saveSettings()
    }

    fun updateLocationPrivacy(level: PrivacyLevel) {
        _uiState.update { it.copy(locationPrivacy = level) }
        saveSettings()
    }

    fun toggleApp(packageName: String, enabled: Boolean) {
        _uiState.update {
            val apps = it.allowedApps.toMutableList()
            if (enabled) apps.add(packageName) else apps.remove(packageName)
            it.copy(allowedApps = apps)
        }
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch
                repository.updateProfile(
                    profile.copy(
                        notificationPrivacy = _uiState.value.notificationPrivacy,
                        locationPrivacy = _uiState.value.locationPrivacy,
                        allowedApps = _uiState.value.allowedApps
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(intent)
    }
}
