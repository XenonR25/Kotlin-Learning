package com.example.assignment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment1.databinding.FragmentConsumerBinding
import com.example.assignment1.manager.MQTTManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class ConsumerFragment : Fragment() {

    private var _binding: FragmentConsumerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mqttManager: MQTTManager
    private var remoteMarker: Marker? = null
    private val TOPIC = "location/realtime/demo_user"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Required for OSMdroid
        Configuration.getInstance().userAgentValue = requireContext().packageName

        _binding = FragmentConsumerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mqttManager = MQTTManager(requireContext())
        setupMap()

        mqttManager.connect {
            // Subscription happens when the button is clicked
        }

        binding.btnStartConsuming.setOnClickListener {
            binding.btnStartConsuming.text = "Listening for Updates..."
            startReceivingUpdates()
        }
    }

    private fun setupMap() {
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(16.0)

        // Default starting point (e.g., center of your city)
        val startPoint = GeoPoint(23.8103, 90.4125)
        binding.mapView.controller.setCenter(startPoint)
    }

    private fun startReceivingUpdates() {
        mqttManager.subscribe(TOPIC, object : MQTTManager.LocationListener {
            override fun onLocationReceived(lat: Double, lng: Double) {
                // Background thread to Main thread transition
                activity?.runOnUiThread {
                    if (_binding != null) {
                        updateMapMarker(lat, lng)
                    }
                }
            }
        })
    }

    private fun updateMapMarker(lat: Double, lng: Double) {
        val point = GeoPoint(lat, lng)

        if (remoteMarker == null) {
            remoteMarker = Marker(binding.mapView)
            remoteMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            remoteMarker?.title = "Remote User"
            binding.mapView.overlays.add(remoteMarker)
        }

        remoteMarker?.position = point
        binding.mapView.controller.animateTo(point)
        binding.mapView.invalidate() // Redraws the map
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mqttManager.disconnect()
        _binding = null
    }
}