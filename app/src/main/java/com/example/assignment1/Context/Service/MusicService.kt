package com.example.assignment1.Context.Service

import android.media.MediaPlayer
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.assignment1.R

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val CHANNEL_ID = "Music Channel"

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music)
        mediaPlayer.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Music Player").setContentText("Playing your favourite recitation...")
            .setSmallIcon(R.drawable.ic_music_store)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1,notification)
        mediaPlayer.start()

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
        CHANNEL_ID, "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager  = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        // This is called when the user swipes the app out of the "recent apps" list
        stopSelf() // This stops the service
        super.onTaskRemoved(rootIntent)
    }
}

