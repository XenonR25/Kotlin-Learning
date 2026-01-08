package com.example.assignment1.Context.Service

import android.Manifest
import android.content.Context
import android.content.ContextParams
import android.util.Log

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase


class LocationSyncWorker(context: Context,params: WorkerParameters) : Worker(context,params){

    override fun doWork(): Result {
        val appContext = applicationContext
        val locationClient = LocationServices.getFusedLocationProviderClient(appContext)

        val databaseURL = "https://chatmate-316e3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val database = FirebaseDatabase.getInstance(databaseURL).reference

        // 1. Double check permission before doing background work
        val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation) return Result.failure()

        return try {
            // 2. Fetch location and wait (Synchronous call for background worker)
            val location = Tasks.await(locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null))

            if (location != null) {
                Log.d("WorkManager", "Location found: ${location.latitude}, ${location.longitude}")
                val locationMap = mapOf(
                    "lat" to location.latitude,
                    "lon" to location.longitude,
                    "lastUpdated" to System.currentTimeMillis()
                )
                Log.d("WorkManager", "Uploading to Firebase path: users/user_1/currentLocation")
                // 3. Save to your specific structure
                Tasks.await(database.child("users")
                    .child("user_1")
                    .child("currentLocation")
                    .setValue(locationMap))
                Log.d("WorkManager", "Firebase Upload SUCCESSFUL")
                Result.success()
            } else {
                Log.w("WorkManager", "Location was NULL. Retrying...")
                Result.retry() // Retry if GPS signal was temporarily lost
            }
        } catch (e: Exception) {
            Log.e("WorkManager", "WorkManager FAILED: ${e.message}")
            Result.failure()
        }
    }
}