package com.example.assignment1.Context.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.assignment1.R
import com.example.assignment1.manager.MQTTManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService : Service() {

    private lateinit var mqttManager: MQTTManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TOPIC = "location/realtime/demo_user"
    private val NOTIFICATION_ID = 101

    override fun onCreate() {
        super.onCreate()
        mqttManager = MQTTManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Connect MQTT immediately when service starts
        mqttManager.connect {
            startLocationUpdates()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create the notification required for Foreground Services
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY // Tells Android to restart service if it gets killed
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()
        fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                // The MQTT Publish now happens from the background!
                mqttManager.publish(TOPIC, loc.latitude, loc.longitude)
            }
        }, Looper.getMainLooper())
    }

    private fun createNotification(): Notification {
        val channelId = "location_sharing_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Location Sharing", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sharing Location")
            .setContentText("Your live location is being shared via MQTT")
            .setSmallIcon(R.drawable.location) // Replace with your icon
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        // Stop MQTT and GPS only when the service is explicitly stopped
        mqttManager.disconnect()
        super.onDestroy()
    }
}