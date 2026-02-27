package com.beside.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beside.app.data.model.UserProfile
import com.beside.app.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.TimeZone

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = ""
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var nickname = ""

    init {
        // 已登录的话直接跳过
        if (auth.currentUser != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    fun setNickname(name: String) {
        nickname = name
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
            } catch (e: Exception) {
                val msg = when {
                    "no user record" in (e.message ?: "").lowercase() -> "找不到这个账号呢，要不注册一个？ 🤔"
                    "password is invalid" in (e.message ?: "").lowercase() -> "密码不对哦，再想想嘛~ 🔑"
                    "badly formatted" in (e.message ?: "").lowercase() -> "邮箱格式不太对呢~ 📧"
                    else -> "登录失败了呢：${e.localizedMessage} 😢"
                }
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("注册失败了呢~")

                // 创建用户资料
                val profile = UserProfile(
                    uid = uid,
                    nickname = nickname.ifBlank { "小可爱" },
                    timezone = TimeZone.getDefault().id,
                    createdAt = Timestamp.now(),
                    lastOnline = Timestamp.now()
                )
                repository.updateProfile(profile)

                _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
            } catch (e: Exception) {
                val msg = when {
                    "already in use" in (e.message ?: "").lowercase() -> "这个邮箱已经被注册啦，直接登录试试？ 💌"
                    "weak password" in (e.message ?: "").lowercase() -> "密码太简单了呢，至少6位哦~ 🔐"
                    else -> "注册失败了呢：${e.localizedMessage} 😢"
                }
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }
}
