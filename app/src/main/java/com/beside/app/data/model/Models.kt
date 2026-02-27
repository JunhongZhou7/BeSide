package com.beside.app.data.model

import com.google.firebase.Timestamp

/**
 * 用户资料
 */
data class UserProfile(
    val uid: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val partnerId: String? = null,
    val pairingCode: String = "",
    val createdAt: Timestamp = Timestamp.now(),

    // 隐私设置
    val locationPrivacy: PrivacyLevel = PrivacyLevel.APP_ONLY,
    val notificationPrivacy: PrivacyLevel = PrivacyLevel.APP_ONLY,
    val allowedApps: List<String> = emptyList(),

    // 位置信息
    val lastLocation: GeoPoint? = null,
    val timezone: String = "",
    val lastOnline: Timestamp = Timestamp.now()
)

/**
 * 位置数据
 */
data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val district: String = "",
    val updatedAt: Timestamp = Timestamp.now()
)

/**
 * 隐私等级
 */
enum class PrivacyLevel {
    APP_ONLY,    // 🟢 仅app名称 "ta用了微信呢~"
    SUMMARY,     // 🟡 简要摘要 "ta收到了一条微信消息哦~"
    FULL_CONTENT // 🔴 完整内容
}

/**
 * 通知事件 — 对方的一天时间线上的每一条
 */
data class ActivityEvent(
    val id: String = "",
    val userId: String = "",
    val appName: String = "",
    val packageName: String = "",
    val title: String = "",
    val summary: String = "",
    val fullContent: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val iconUrl: String = ""
)

/**
 * 配对请求
 */
data class PairingRequest(
    val fromUid: String = "",
    val toUid: String = "",
    val code: String = "",
    val status: PairingStatus = PairingStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now()
)

enum class PairingStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
