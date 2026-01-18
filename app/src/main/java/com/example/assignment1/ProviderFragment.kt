package com.example.assignment1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment1.Context.service.LocationService
import com.example.assignment1.databinding.FragmentProviderBinding

class ProviderFragment : Fragment() {

    private var _binding: FragmentProviderBinding? = null
    private val binding get() = _binding!!

    // Track if service is running to update button UI
    private var isServiceRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartSharing.setOnClickListener {
            if (!isServiceRunning) {
                startLocationService()
            } else {
                stopLocationService()
            }
        }
    }

    private fun startLocationService() {
        val intent = Intent(requireContext(), LocationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }

        isServiceRunning = true
        binding.btnStartSharing.text = "Stop Sharing"
        binding.btnStartSharing.setBackgroundResource(R.drawable.button_red)
        binding.tvStatus.text = "Status: Sharing in Background"
    }

    private fun stopLocationService() {
        val intent = Intent(requireContext(), LocationService::class.java)
        requireContext().stopService(intent)

        isServiceRunning = false
        binding.btnStartSharing.text = "Go Live"
        binding.btnStartSharing.setBackgroundResource(R.drawable.button_green)
        binding.tvStatus.text = "Status: Broadcast Stopped"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Note: We do NOT stop the service here.
        // This allows the user to leave the fragment while sharing stays alive.
    }
}