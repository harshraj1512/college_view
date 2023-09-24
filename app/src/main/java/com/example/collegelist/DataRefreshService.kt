package com.example.collegelist

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataRefreshService : Service() {

    private val CHANNEL_ID = "DataRefreshServiceChannel"
    private val NOTIFICATION_ID = 12345
    private val refreshIntervalMillis: Long = 10000 // 10 seconds

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            // Perform data refresh here (fetch new data from the API)
            fetchAllUniversities()

            // Schedule the next refresh
            handler.postDelayed(this, refreshIntervalMillis)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // Start the periodic data refresh
        handler.postDelayed(refreshRunnable, refreshIntervalMillis)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DataRefreshService", "Service started")
        createNotificationChannel()

        // Create a PendingIntent to launch the MainActivity when the notification is tapped
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Refreshing Data")
            .setContentText("Updating data from the server...")
            .setSmallIcon(android.R.drawable.ic_menu_rotate) // Using ic_refresh icon
            .setContentIntent(pendingIntent)
            .build()

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification)

        // Perform data refresh here (fetch new data from the API)
        fetchAllUniversities()

        // Stop the service after refreshing data (adjust this as needed)
        //stopSelf()

        return START_NOT_STICKY
    }

    private fun fetchAllUniversities() {
        Log.d("DataRefreshService", "Fetching universities data...")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Make the API request to fetch all universities
                val universities = UniversityApi.service.getAllUniversities()

                // Handle the fetched data as needed


            } catch (e: Exception) {
                // Handle API request error
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the periodic data refresh
        handler.removeCallbacks(refreshRunnable)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Refreshing Data")
            .setContentText("Updating data from the server...")
            .setSmallIcon(android.R.drawable.ic_menu_rotate)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Data Refresh Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
