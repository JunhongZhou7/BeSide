package com.beside.app

import android.app.Application
import com.google.firebase.FirebaseApp

class BeSideApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
