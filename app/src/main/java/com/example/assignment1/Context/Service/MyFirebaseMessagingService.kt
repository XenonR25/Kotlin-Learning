package com.example.assignment1.Context.Service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: $token")

        val databaseUrl = "https://chatmate-316e3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = FirebaseDatabase.getInstance(databaseUrl)
        val myRef = database.getReference("tokens")

        // Saving token to user_1
        myRef.child("user_1").setValue(token)
            .addOnSuccessListener { Log.d("FCM", "Token saved successfully!") }
            .addOnFailureListener { e -> Log.e("FCM", "Failed to save token", e) }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var title: String? = null
        var body: String? = null

        // Priority 1: Check if there's a Notification block (Standard Console)
        if (message.notification != null) {
            title = message.notification?.title
            body = message.notification?.body
        }
        // Priority 2: If notification is empty, check the Data block (Custom API)
        else if (message.data.isNotEmpty()) {
            title = message.data["title"]
            body = message.data["body"]
        }

        // Final Fallback: If both are empty
        showNotification(title ?: "Default Title", body ?: "Default Body")
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Channel",
                NotificationManager.IMPORTANCE_HIGH // Changed to HIGH to pop up on screen
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            // Use a built-in android icon to ensure it doesn't fail due to missing resource
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For older versions
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}