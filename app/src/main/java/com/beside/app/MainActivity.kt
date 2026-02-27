package com.beside.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.beside.app.ui.BeSideNavHost
import com.beside.app.ui.theme.BeSideTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeSideTheme {
                BeSideNavHost()
            }
        }
    }
}
