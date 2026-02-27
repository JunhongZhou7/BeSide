package com.beside.app.data.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.beside.app.MainActivity
import com.beside.app.data.model.GeoPoint
import com.beside.app.data.repository.UserRepository
import com.google.android.gms.location.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.*
import java.util.*

/**
 * 后台位置同步服务 — 定期上报位置给对方看
 */
class LocationSyncService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository = UserRepository()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 15 * 60 * 1000) // 15分钟
            .setMinUpdateIntervalMillis(10 * 60 * 1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    scope.launch {
                        try {
                            val geocoder = Geocoder(this@LocationSyncService, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address = addresses?.firstOrNull()

                            val geoPoint = GeoPoint(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                city = address?.locality ?: address?.adminArea ?: "未知",
                                district = address?.subLocality ?: address?.thoroughfare ?: "",
                                updatedAt = Timestamp.now()
                            )

                            repository.updateLocation(geoPoint)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        }
    }

    private fun createNotification(): Notification {
        val channelId = "location_sync"
        val channel = NotificationChannel(channelId, "位置同步", NotificationManager.IMPORTANCE_LOW).apply {
            description = "让ta知道你在哪里呢~ 💕"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("在你身边 💕")
            .setContentText("正在为ta守护你的位置呢~")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
