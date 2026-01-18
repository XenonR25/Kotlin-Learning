package com.example.assignment1.manager

import android.content.Context
import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import org.json.JSONObject

class MQTTManager(private val context: Context) {
    private val TAG = "MQTT_LOG"
    private var client: Mqtt3AsyncClient? = null

    interface LocationListener {
        fun onLocationReceived(lat: Double, lng: Double)
    }

    fun connect(onConnected: () -> Unit) {
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("android_user_${System.currentTimeMillis()}")
            .serverHost("broker.hivemq.com")
            .serverPort(1883)
            .buildAsync()

        client?.connect()?.whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e(TAG, "Connection Failed: ${throwable.message}")
            } else {
                Log.d(TAG, "Connected to MQTT Broker successfully")
                onConnected()
            }
        }
    }

    fun publish(topic: String, lat: Double, lng: Double) {
        if(client?.state?.isConnected == false){
            Log.w(TAG,"⚠\uFE0F Cannot publish: MQTT not connected")
            return
        }

        val payload = "{\"lat\":$lat, \"lng\":$lng}"
        client?.publishWith()
            ?.topic(topic)
            ?.payload(payload.toByteArray())
            ?.qos(MqttQos.AT_MOST_ONCE) // Best for realtime high frequency
            ?.send()
            ?.whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Subscribe Failed: ${throwable.message}")
                } else {
                    Log.d(TAG, "Subscribed to Broker")
                }
            }
        Log.d(TAG, "Published: $payload")
    }

    fun subscribe(topic: String, listener: LocationListener) {
        client?.subscribeWith()
            ?.topicFilter(topic)
            ?.callback { publish ->
                val content = String(publish.payloadAsBytes)
                Log.d(TAG, "RAW DATA ARRIVED: $content") // Check if this shows up
                val json = JSONObject(content)
                listener.onLocationReceived(json.getDouble("lat"), json.getDouble("lng"))
            }
            ?.send()
            ?.whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Subscribe FAILED: ${throwable.message}")
                } else {
                    Log.d(TAG, "Subscribe SUCCESSFUL to topic: $topic")
                }
            }
    }

    fun disconnect(){
        Log.d(TAG, "Disconnecting  MQTT...")
        client?.disconnect()
    }
}