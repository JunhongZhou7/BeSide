package com.beside.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.beside.app.ui.BeSideNavHost
import com.beside.app.ui.theme.BeSideTheme
import com.beside.app.util.LanguageHelper

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLocale(this)
        enableEdgeToEdge()
        setContent {
            BeSideTheme {
                BeSideNavHost()
            }
        }
    }
}
