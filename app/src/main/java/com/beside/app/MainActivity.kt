package com.beside.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.beside.app.ui.BeSideNavHost
import com.beside.app.ui.theme.BeSideTheme

class MainActivity : ComponentActivity() {
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
