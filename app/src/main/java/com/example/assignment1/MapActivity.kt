package com.example.assignment1

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.assignment1.ConsumerFragment
import com.example.assignment1.ProviderFragment
import com.example.assignment1.R
import com.example.assignment1.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false

        if (locationGranted && notificationGranted) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions are required for background sharing", Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var binding: ActivityMapBinding

    // 1. Define the callback
    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            // Logic to show main UI again
            showMainMenu()

            // Remove the fragment
            supportFragmentManager.popBackStack()

            // Disable this callback since we are back at the main menu
            isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Add callback to the dispatcher
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        binding.btnGoProvider.setOnClickListener {
            openFragment(ProviderFragment())
        }

        binding.btnGoConsumer.setOnClickListener {
            openFragment(ConsumerFragment())
        }

        // Request permissions on startup
        checkPermissions()
    }

    private fun openFragment(fragment: Fragment) {
        // Hide Main UI
        binding.buttonContainer.visibility = View.GONE
        binding.tvHeader.visibility = View.GONE
        binding.tvSubHeader.visibility = View.GONE

        // Enable back button logic
        backPressedCallback.isEnabled = true

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showMainMenu() {
        binding.buttonContainer.visibility = View.VISIBLE
        binding.tvHeader.visibility = View.VISIBLE
        binding.tvSubHeader.visibility = View.VISIBLE
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }
}