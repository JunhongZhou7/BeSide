package com.beside.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beside.app.data.model.PrivacyLevel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "设置 ⚙️",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 通知隐私 =====
        PrivacySection(
            title = "🔔 通知隐私",
            description = "ta可以看到你的通知到什么程度呢~",
            currentLevel = uiState.notificationPrivacy,
            onLevelChange = { viewModel.updateNotificationPrivacy(it) },
            labels = mapOf(
                PrivacyLevel.APP_ONLY to "🟢 仅app名称 — \"ta用了微信呢~\"",
                PrivacyLevel.SUMMARY to "🟡 简要摘要 — \"ta收到了一条微信消息哦~\"",
                PrivacyLevel.FULL_CONTENT to "🔴 完整内容 — ta可以看到所有细节"
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 位置隐私 =====
        PrivacySection(
            title = "📍 位置隐私",
            description = "ta可以知道你的位置到什么程度呢~",
            currentLevel = uiState.locationPrivacy,
            onLevelChange = { viewModel.updateLocationPrivacy(it) },
            labels = mapOf(
                PrivacyLevel.APP_ONLY to "🟢 城市级别 — \"ta在上海呢~\"",
                PrivacyLevel.SUMMARY to "🟡 区域级别 — \"ta在浦东新区哦~\"",
                PrivacyLevel.FULL_CONTENT to "🔴 精确位置 — ta可以看到详细地址"
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 允许读取的app =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "📱 允许读取的App",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "选择哪些app的通知可以被ta看到呢~ 不选的话就是全部哦",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                val commonApps = listOf(
                    "微信" to "com.tencent.mm",
                    "QQ" to "com.tencent.mobileqq",
                    "Instagram" to "com.instagram.android",
                    "WhatsApp" to "com.whatsapp",
                    "UberEats" to "com.ubercab.eats",
                    "美团" to "com.sankuai.meituan",
                    "淘宝" to "com.taobao.taobao",
                    "抖音" to "com.ss.android.ugc.aweme",
                    "Spotify" to "com.spotify.music",
                    "YouTube" to "com.google.android.youtube"
                )

                commonApps.forEach { (name, packageName) ->
                    val isChecked = packageName in uiState.allowedApps
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                viewModel.toggleApp(packageName, checked)
                            }
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 通知权限引导
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "⚡ 通知读取权限",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "需要开启「通知读取权限」才能让ta看到你的动态哦~ 去系统设置里找到「在你身边」然后打开通知读取就好啦 💕",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.openNotificationSettings() }) {
                    Text("去开启权限 ✨")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PrivacySection(
    title: String,
    description: String,
    currentLevel: PrivacyLevel,
    onLevelChange: (PrivacyLevel) -> Unit,
    labels: Map<PrivacyLevel, String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            labels.forEach { (level, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLevel == level,
                        onClick = { onLevelChange(level) }
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
