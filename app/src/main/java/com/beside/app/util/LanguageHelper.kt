package com.beside.app.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageHelper {

    private const val PREF_NAME = "beside_language"
    private const val KEY_LANG = "language_code"

    data class Language(
        val code: String,
        val displayName: String
    )

    val supportedLanguages = listOf(
        Language("zh", "中文"),
        Language("en", "English"),
        Language("fr", "Français"),
        Language("ja", "日本語"),
        Language("ko", "한국어")
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLanguage(context: Context, languageCode: String) {
        // 保存选择
        getPrefs(context).edit().putString(KEY_LANG, languageCode).apply()

        // 更新 locale
        updateLocale(context, languageCode)

        // 重启 Activity
        if (context is Activity) {
            context.recreate()
        }
    }

    fun getCurrentLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANG, null)
            ?: Locale.getDefault().language
    }

    fun updateLocale(context: Context, languageCode: String? = null) {
        val code = languageCode ?: getCurrentLanguage(context)
        val locale = Locale(code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * 在 Activity.attachBaseContext 中调用，确保语言生效
     */
    fun wrapContext(context: Context): Context {
        val code = getPrefs(context).getString(KEY_LANG, null) ?: return context
        val locale = Locale(code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
