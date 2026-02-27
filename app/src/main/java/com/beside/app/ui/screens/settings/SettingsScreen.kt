package com.beside.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beside.app.R
import com.beside.app.data.model.PrivacyLevel
import com.beside.app.util.LanguageHelper

// 设置页的子页面
enum class SettingsPage {
    MAIN,           // 主设置列表
    NOTIFICATION,   // 通知隐私
    LOCATION,       // 位置隐私
    ALLOWED_APPS,   // 允许的 App
    LANGUAGE,        // 语言
    PERMISSION      // 通知权限
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var currentPage by remember { mutableStateOf(SettingsPage.MAIN) }

    when (currentPage) {
        SettingsPage.MAIN -> SettingsMainPage(
            onNavigate = { currentPage = it }
        )
        SettingsPage.NOTIFICATION -> NotificationPrivacyPage(
            currentLevel = uiState.notificationPrivacy,
            onLevelChange = { viewModel.updateNotificationPrivacy(it) },
            onBack = { currentPage = SettingsPage.MAIN }
        )
        SettingsPage.LOCATION -> LocationPrivacyPage(
            currentLevel = uiState.locationPrivacy,
            onLevelChange = { viewModel.updateLocationPrivacy(it) },
            onBack = { currentPage = SettingsPage.MAIN }
        )
        SettingsPage.ALLOWED_APPS -> AllowedAppsPage(
            allowedApps = uiState.allowedApps,
            onToggleApp = { pkg, checked -> viewModel.toggleApp(pkg, checked) },
            onBack = { currentPage = SettingsPage.MAIN }
        )
        SettingsPage.LANGUAGE -> LanguagePage(
            onBack = { currentPage = SettingsPage.MAIN }
        )
        SettingsPage.PERMISSION -> PermissionPage(
            onOpenSettings = { viewModel.openNotificationSettings() },
            onBack = { currentPage = SettingsPage.MAIN }
        )
    }
}

// ===== 主设置页面 =====
@Composable
fun SettingsMainPage(onNavigate: (SettingsPage) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsItem(
            icon = Icons.Filled.Notifications,
            title = stringResource(R.string.settings_notif_privacy_title),
            subtitle = stringResource(R.string.settings_notif_privacy_desc),
            onClick = { onNavigate(SettingsPage.NOTIFICATION) }
        )

        SettingsItem(
            icon = Icons.Filled.LocationOn,
            title = stringResource(R.string.settings_location_privacy_title),
            subtitle = stringResource(R.string.settings_location_privacy_desc),
            onClick = { onNavigate(SettingsPage.LOCATION) }
        )

        SettingsItem(
            icon = Icons.Filled.Apps,
            title = stringResource(R.string.settings_allowed_apps_title),
            subtitle = stringResource(R.string.settings_allowed_apps_desc),
            onClick = { onNavigate(SettingsPage.ALLOWED_APPS) }
        )

        SettingsItem(
            icon = Icons.Filled.Language,
            title = stringResource(R.string.settings_language_title),
            subtitle = stringResource(R.string.settings_language_desc),
            onClick = { onNavigate(SettingsPage.LANGUAGE) }
        )

        SettingsItem(
            icon = Icons.Filled.Security,
            title = stringResource(R.string.settings_notif_permission_title),
            subtitle = stringResource(R.string.settings_notif_permission_desc),
            onClick = { onNavigate(SettingsPage.PERMISSION) }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ===== 返回按钮头部 =====
@Composable
fun SubPageHeader(title: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// ===== 通知隐私子页 =====
@Composable
fun NotificationPrivacyPage(
    currentLevel: PrivacyLevel,
    onLevelChange: (PrivacyLevel) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SubPageHeader(stringResource(R.string.settings_notif_privacy_title), onBack)

        Text(
            text = stringResource(R.string.settings_notif_privacy_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = mapOf(
            PrivacyLevel.APP_ONLY to stringResource(R.string.settings_privacy_app_only_notif),
            PrivacyLevel.SUMMARY to stringResource(R.string.settings_privacy_summary_notif),
            PrivacyLevel.FULL_CONTENT to stringResource(R.string.settings_privacy_full_notif)
        )

        options.forEach { (level, label) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onLevelChange(level) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentLevel == level)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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

// ===== 位置隐私子页 =====
@Composable
fun LocationPrivacyPage(
    currentLevel: PrivacyLevel,
    onLevelChange: (PrivacyLevel) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SubPageHeader(stringResource(R.string.settings_location_privacy_title), onBack)

        Text(
            text = stringResource(R.string.settings_location_privacy_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val options = mapOf(
            PrivacyLevel.APP_ONLY to stringResource(R.string.settings_privacy_city),
            PrivacyLevel.SUMMARY to stringResource(R.string.settings_privacy_district),
            PrivacyLevel.FULL_CONTENT to stringResource(R.string.settings_privacy_exact)
        )

        options.forEach { (level, label) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onLevelChange(level) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentLevel == level)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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

// ===== 允许的 App 子页 =====
@Composable
fun AllowedAppsPage(
    allowedApps: List<String>,
    onToggleApp: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SubPageHeader(stringResource(R.string.settings_allowed_apps_title), onBack)

        Text(
            text = stringResource(R.string.settings_allowed_apps_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
            val isChecked = packageName in allowedApps
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onToggleApp(packageName, !isChecked) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked -> onToggleApp(packageName, checked) }
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
}

// ===== 语言子页 =====
@Composable
fun LanguagePage(onBack: () -> Unit) {
    val context = LocalContext.current
    val currentLang = remember { LanguageHelper.getCurrentLanguage(context) }
    var selectedLang by remember { mutableStateOf(currentLang) }
    val hasChanged = selectedLang != currentLang

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(bottom = if (hasChanged) 80.dp else 0.dp)
        ) {
            SubPageHeader(stringResource(R.string.settings_language_title), onBack)

            Text(
                text = stringResource(R.string.settings_language_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LanguageHelper.supportedLanguages.forEach { lang ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedLang = lang.code },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLang == lang.code)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLang == lang.code,
                            onClick = { selectedLang = lang.code }
                        )
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selectedLang == lang.code) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Apply Change 按钮（底部浮动）
        if (hasChanged) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Button(
                    onClick = {
                        LanguageHelper.setLanguage(context, selectedLang)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Apply Change ✨",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

// ===== 通知权限子页 =====
@Composable
fun PermissionPage(
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubPageHeader(stringResource(R.string.settings_notif_permission_title), onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "🔔", style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.settings_notif_permission_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.settings_notif_permission_button))
        }
    }
}
