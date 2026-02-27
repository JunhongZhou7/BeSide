package com.beside.app.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.beside.app.data.model.ActivityEvent
import com.beside.app.data.model.PrivacyLevel
import com.beside.app.data.repository.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 通知监听服务 — 捕获其他 app 的通知，根据隐私设置上传
 */
class NotificationCaptureService : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository = UserRepository()

    // 系统级 app 我们不关心
    private val ignoredPackages = setOf(
        "com.android.systemui",
        "com.android.providers",
        "com.beside.app" // 自己
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (ignoredPackages.any { packageName.startsWith(it) }) return

        scope.launch {
            try {
                val uid = repository.currentUid ?: return@launch
                val profile = repository.getProfile(uid) ?: return@launch

                // 检查用户是否允许读取这个 app
                if (profile.allowedApps.isNotEmpty() && packageName !in profile.allowedApps) return@launch

                val extras = sbn.notification.extras
                val title = extras.getCharSequence("android.title")?.toString() ?: ""
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                val appName = getAppName(packageName)

                val event = when (profile.notificationPrivacy) {
                    PrivacyLevel.APP_ONLY -> ActivityEvent(
                        id = UUID.randomUUID().toString(),
                        userId = uid,
                        appName = appName,
                        packageName = packageName,
                        title = "",
                        summary = "用了${appName}呢~ 📱",
                        fullContent = "",
                        timestamp = Timestamp.now()
                    )
                    PrivacyLevel.SUMMARY -> ActivityEvent(
                        id = UUID.randomUUID().toString(),
                        userId = uid,
                        appName = appName,
                        packageName = packageName,
                        title = title,
                        summary = "收到了一条${appName}的消息哦~ 💌",
                        fullContent = "",
                        timestamp = Timestamp.now()
                    )
                    PrivacyLevel.FULL_CONTENT -> ActivityEvent(
                        id = UUID.randomUUID().toString(),
                        userId = uid,
                        appName = appName,
                        packageName = packageName,
                        title = title,
                        summary = text,
                        fullContent = "$title: $text",
                        timestamp = Timestamp.now()
                    )
                }

                repository.postEvent(event)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }
}
