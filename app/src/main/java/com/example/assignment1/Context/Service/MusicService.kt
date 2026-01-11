package com.example.assignment1.Context.Service

import android.media.MediaPlayer
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.assignment1.R

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "Music_Channel"
    private var currentSongIndex = 0

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val EXTRA_SONG_INDEX = "EXTRA_SONG_INDEX"
    }

    // List of songs - add your music files in res/raw/
    private val songList = listOf(
        R.raw.recitation1,
        R.raw.recitation2,
        R.raw.recitation3// Make sure these files exist in res/raw/
        // Add more songs here like:
        // R.raw.song2,
        // R.raw.song3,
        // etc.
    )

    private val songTitles = listOf(
        "Surah Al A'la",
        "Surah Al Ghashiyah",
        "Surah Ad Duha"
        // Add corresponding titles:
        // "Song 2",
        // "Song 3",
        // etc.
    )

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val songIndex = intent.getIntExtra(EXTRA_SONG_INDEX, 0)
                playSong(songIndex)
            }
            ACTION_STOP -> {
                stopMusic()
            }
            ACTION_NEXT -> {
                playNext()
            }
            ACTION_PREVIOUS -> {
                playPrevious()
            }
        }

        return START_STICKY
    }

    private fun playSong(index: Int) {
        if (index < 0 || index >= songList.size) return

        currentSongIndex = index

        // Release previous MediaPlayer if exists
        mediaPlayer?.release()

        // Create new MediaPlayer for the selected song
        mediaPlayer = MediaPlayer.create(this, songList[currentSongIndex])
        mediaPlayer?.isLooping = false

        // Set completion listener to play next song
        mediaPlayer?.setOnCompletionListener {
            playNext()
        }

        // Create notification with media controls
        val notification = createNotification()
        startForeground(1, notification)

        // Start playing
        mediaPlayer?.start()
    }

    private fun createNotification(): Notification {
        val songTitle = if (currentSongIndex < songTitles.size) {
            songTitles[currentSongIndex]
        } else {
            "Song ${currentSongIndex + 1}"
        }

        // Create intents for notification buttons
        val previousIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            this, 0, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 1, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing: $songTitle")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_previous, "Previous", previousPendingIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .build()
    }

    private fun playNext() {
        currentSongIndex = (currentSongIndex + 1) % songList.size
        playSong(currentSongIndex)
    }

    private fun playPrevious() {
        currentSongIndex = if (currentSongIndex - 1 < 0) {
            songList.size - 1
        } else {
            currentSongIndex - 1
        }
        playSong(currentSongIndex)
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }
}