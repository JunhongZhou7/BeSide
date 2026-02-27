package com.beside.app.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales = LocaleList(Locale.forLanguageTag(languageCode))
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
            )
        }
    }

    fun getCurrentLanguage(context: Context): String {
        val locales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales
        } else {
            AppCompatDelegate.getApplicationLocales().unwrap() as? LocaleList
        }

        return if (locales != null && !locales.isEmpty) {
            locales[0].language
        } else {
            Locale.getDefault().language
        }
    }
}
