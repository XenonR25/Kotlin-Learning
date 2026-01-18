package com.example.assignment1

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import com.example.assignment1.databinding.FragmentLocationSharingBinding
import com.example.assignment1.manager.MQTTManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.PI

class MapFragment : Fragment() {
    private var _binding : FragmentLocationSharingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mqttManager : MQTTManager
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    private lateinit var map : MapView
    private var marker : Marker? = null

    private val TAG = "LOCATION_APP"
    private val TOPIC = "location/realtime/demo_user"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //OSM requires a User Agent to identify your app to their servers
        Configuration.getInstance().userAgentValue = requireContext().packageName

        _binding = FragmentLocationSharingBinding.inflate(inflater, container,false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mqttManager = MQTTManager(requireContext())

        //1.Initialize Map
        map = binding.mapView
        map.setMultiTouchControls(true) // allows zooming with fingers
        val mapController = map.controller
        mapController.setZoom(15.0)

        //2.Connect to MQTT
        mqttManager.connect {
            Log.d(TAG, "MQTT connected")
            activity?.runOnUiThread {
                Toast.makeText(context, "Connected to Server", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnProvider.setOnClickListener {
            Log.d(TAG, "Acting as Provider")
            startGpsBroadcasting()
        }
        binding.btnConsumer.setOnClickListener {
            Log.d(TAG, "Acing as Consumer")
            startReceivingUpdates()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startGpsBroadcasting(){
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,3000)
            .setMinUpdateDistanceMeters(1f).build()

        fusedLocationClient.requestLocationUpdates(request, object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return

                //Log and Publish
                Log.i(TAG, "Broadcasting GPS : ${loc.latitude}, ${loc.longitude}")
                mqttManager.publish(TOPIC,loc.latitude,loc.longitude)

                //also update local map so that they can also see that
                updateMapMarker(loc.latitude,loc.longitude , false)
            }
        }, Looper.getMainLooper())
    }
    private fun startReceivingUpdates() {
        Log.d(TAG, "Consumer: Starting Subscription to $TOPIC")
        mqttManager.subscribe(TOPIC, object : MQTTManager.LocationListener {
            override fun onLocationReceived(lat: Double, lng: Double) {
                Log.i(TAG, "Consumer: DATA ARRIVED -> Lat: $lat, Lng: $lng")

                // CRITICAL: Everything touching the Map must be inside runOnUiThread
                activity?.runOnUiThread {
                    try {
                        updateMapMarker(lat, lng)

                        // Force the camera to jump to the new spot
                        val targetPoint = GeoPoint(lat, lng)
                        map.controller.animateTo(targetPoint)

                        // VERY IMPORTANT: Tell the map to redraw itself
                        map.invalidate()

                        Log.d(TAG, "Consumer: Map Updated and Redrawn")
                    } catch (e: Exception) {
                        Log.e(TAG, "Consumer UI Update Error: ${e.message}")
                    }
                }
            }
        })
    }
    private fun updateMapMarker(lat: Double, lng: Double, isRemote: Boolean = false) {
        val point = GeoPoint(lat, lng)

        // Create a NEW marker specifically for the Consumer to prove it's working
        if (!isRemote) {
            val remoteMarker = Marker(map)
            remoteMarker.position = point
            remoteMarker.title = "Remote Data"
            remoteMarker.icon = resources.getDrawable(org.osmdroid.library.R.drawable.marker_default_focused_base) // Different color
            map.overlays.add(remoteMarker)
        } else {
            // This is your local Provider marker
            if (marker == null) {
                marker = Marker(map)
                map.overlays.add(marker)
            }
            marker?.position = point
        }

        map.controller.animateTo(point)
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }
    override  fun onPause(){
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mqttManager.disconnect()
        _binding = null
    }
}