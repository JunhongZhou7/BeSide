package com.beside.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beside.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HomeUiState(
    val hasParter: Boolean = false,
    val partnerName: String = "",
    val partnerLocation: String = "未知",
    val partnerLocalTime: String = "--:--",
    val lastOnlineText: String = "未知",
    val todayEventCount: Int = 0
)

class HomeViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPartnerData()
    }

    private fun loadPartnerData() {
        viewModelScope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch
                val partnerId = profile.partnerId ?: return@launch
                
                // 监听对方数据
                repository.observePartner(partnerId).collect { partner ->
                    if (partner != null) {
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        if (partner.timezone.isNotEmpty()) {
                            timeFormat.timeZone = TimeZone.getTimeZone(partner.timezone)
                        }

                        _uiState.value = HomeUiState(
                            hasParter = true,
                            partnerName = partner.nickname.ifEmpty { "ta" },
                            partnerLocation = partner.lastLocation?.city?.ifEmpty { "未知" } ?: "未知",
                            partnerLocalTime = timeFormat.format(Date()),
                            lastOnlineText = getRelativeTime(partner.lastOnline.toDate()),
                            todayEventCount = 0 // 会通过事件流更新
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 监听今日事件数
        viewModelScope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch
                val partnerId = profile.partnerId ?: return@launch

                repository.observePartnerEvents(partnerId).collect { events ->
                    _uiState.update { it.copy(todayEventCount = events.size) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRelativeTime(date: Date): String {
        val diff = System.currentTimeMillis() - date.time
        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000} 分钟前"
            diff < 86400_000 -> "${diff / 3600_000} 小时前"
            else -> "${diff / 86400_000} 天前"
        }
    }
}
