package com.beside.app.ui.screens.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beside.app.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PairingUiState(
    val myUid: String = "",
    val myCode: String = "",
    val message: String = "",
    val isError: Boolean = false
)

class PairingViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(PairingUiState())
    val uiState: StateFlow<PairingUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(myUid = repository.currentUid ?: "未登录") }
    }

    fun generateCode() {
        viewModelScope.launch {
            try {
                val code = repository.generatePairingCode()
                _uiState.update { it.copy(myCode = code, message = "邀请码生成成功啦~ 快告诉ta吧 💌") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "生成失败了呢，再试一次嘛~ 😢", isError = true) }
            }
        }
    }

    fun pairWithCode(code: String) {
        viewModelScope.launch {
            try {
                val success = repository.pairWithCode(code)
                if (success) {
                    _uiState.update { it.copy(message = "绑定成功啦！从现在开始，ta就是你的人了 💕", isError = false) }
                } else {
                    _uiState.update { it.copy(message = "找不到这个邀请码呢，再确认一下嘛~ 🤔", isError = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "配对失败了呢~ 😢", isError = true) }
            }
        }
    }

    fun pairWithUserId(uid: String) {
        viewModelScope.launch {
            try {
                val success = repository.pairWithUserId(uid)
                if (success) {
                    _uiState.update { it.copy(message = "配对请求已发送~ 等ta同意就好啦 💌", isError = false) }
                } else {
                    _uiState.update { it.copy(message = "找不到这个用户呢，ID对不对呀~ 🤔", isError = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "发送失败了呢~ 😢", isError = true) }
            }
        }
    }

    fun showMyQR() {
        _uiState.update { it.copy(message = "二维码功能正在开发中呢，再等一下嘛~ 🔧") }
    }

    fun scanQR() {
        _uiState.update { it.copy(message = "扫码功能正在开发中呢，再等一下嘛~ 🔧") }
    }
}
