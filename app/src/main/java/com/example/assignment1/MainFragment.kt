package com.example.assignment1

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.assignment1.Context.Service.LocationSyncWorker
import com.example.assignment1.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import java.util.concurrent.TimeUnit


class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var locationClient : FusedLocationProviderClient


    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted : Boolean ->
        if(isGranted){
            getCurrentLocation()
        } else{
            binding.locationText.text = "Permission denied"
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        val databaseUrl = "https://chatmate-316e3-default-rtdb.asia-southeast1.firebasedatabase.app/" // Use your URL from the console
        database = FirebaseDatabase.getInstance(databaseUrl).reference

        requestNotificationPermission()
        getFcmToken()

        // Example: trigger Firebase write via button
        binding.btnSend.setOnClickListener {
            saveMessageToFirebase()
        }


        //LOCATION PERMISSION
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.getLocationBtn.setOnClickListener {
            checkAndRequestPermission()
        }
        setupLocationSchedule() // Start the background sync
        setupRemoteConfig()

        binding.btnGoToMusic.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,MusicFragment()).addToBackStack(null).commit()
        }
        binding.btnContentProvider.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                ContentProviderFragment()).addToBackStack(null).commit()
        }
    }

    private fun checkAndRequestPermission(){
        when{
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            } else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        }
    }

    private fun getCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(
            requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ) return

        locationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                displayLocation(location.latitude,location.longitude)
            } else{
                fetchFreshLocation()
            }
        }
    }

    //For fresh location
    private fun fetchFreshLocation (){
        if(ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) return

        val priority = Priority.PRIORITY_HIGH_ACCURACY
        locationClient.getCurrentLocation(priority, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if(location != null){
                    displayLocation(location.latitude,location.longitude)
                } else {
                    binding.locationText.text = "Location unavailable. Enable GPS for location"
                }
            }
    }

    private fun displayLocation(lat:Double , lon: Double){
        binding.locationText.text = "LATITUDE : $lat \n LONGTITUDE: $lon"
    }

    //WORK MANAGER , LOCATION HANDLING IN HERE
    private fun setupLocationSchedule(){
        Log.d("MainFragment", "setupLocationSchedule: Initializing WorkManager request...")
        //Constraints handling
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        //Defining the request (REQUESTING EVERY 15MINS)
        val locationSyncRequest = PeriodicWorkRequestBuilder<LocationSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // Log the Enqueue event
        Log.d("MainFragment", "setupLocationSchedule: Enqueueing unique work 'LocationSync'")
        //Enqueue the work
        val operation = WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "LocationSync",
            ExistingPeriodicWorkPolicy.REPLACE, //Keep the existing work schedule if exists
            locationSyncRequest
        )
        operation.state.observe(viewLifecycleOwner) { state ->
            Log.d("MainFragment", "Enqueue Operation State: $state")
        }
    }




















    private fun setupRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig

        // 1. Settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60 // For testing, check every minute
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // 2. Local Defaults
        remoteConfig.setDefaultsAsync(mapOf("show_background_image" to false))

        // 3. Fetch and Activate
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RC", "The fetch is successful")
                    // Get the boolean value from Firebase Console
                    val isImageEnabled = remoteConfig.getBoolean("show_background_image")

                    // Toggle visibility
                    if (isImageEnabled) {
                        binding.ivBackground.visibility = View.VISIBLE
                    } else {
                        binding.ivBackground.visibility = View.GONE
                    }
                }
            }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Current Token: $token")

            // Optional: Save it to database here as well to ensure you have it linked to this user
            database.child("users").child("user_1").child("fcmToken").setValue(token)
        }
    }

    private fun saveMessageToFirebase() {
        // 1. Clear previous errors
        binding.tilReceiver.error = null
        binding.tilMessage.error = null

        // 2. Extract values using the EditText inside the Layout
        val receiverId = binding.etReceiverId.text.toString().trim()
        val messageText = binding.etMessage.text.toString().trim()

        // 3. Validation with UI Feedback
        if (receiverId.isEmpty()) {
            binding.tilReceiver.error = "Please enter a receiver ID"
            return
        }
        if (messageText.isEmpty()) {
            binding.tilMessage.error = "Message cannot be empty"
            return
        }

        // 4. Save to Database
        val messageMap = mapOf(
            "sender" to "user_1",
            "receiver" to receiverId,
            "text" to messageText,
            "timestamp" to System.currentTimeMillis()
        )

        database.child("messages").push().setValue(messageMap)
            .addOnSuccessListener {
                // Clear inputs on success
                binding.etMessage.text?.clear()
                Log.d("Firebase", "Message sent successfully!")
            }
    }

    override fun onResume() {
        super.onResume()
        val remoteConfig = Firebase.remoteConfig
        val isImageEnabled = remoteConfig.getBoolean("show_background_image")

        // Toggle visibility
        if (isImageEnabled) {
            binding.ivBackground.visibility = View.VISIBLE
        } else {
            binding.ivBackground.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // avoid memory leaks
    }
}
