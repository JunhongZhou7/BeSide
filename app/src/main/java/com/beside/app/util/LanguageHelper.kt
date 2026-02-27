package com.beside.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageHelper {

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

    fun setLanguage(context: Context, languageCode: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
        // 重启 Activity 使语言生效
        if (context is Activity) {
            val intent = context.intent
            context.finish()
            context.startActivity(intent)
        }
    }

    fun getCurrentLanguage(context: Context): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (!locales.isEmpty) {
            locales[0]?.language ?: "zh"
        } else {
            java.util.Locale.getDefault().language
        }
    }
}
