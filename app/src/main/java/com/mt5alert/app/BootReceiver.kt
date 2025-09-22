package com.mt5alert.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Boot completed, checking if service should start")
        
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // Check if user has previously started the service
                val sharedPreferences = context.getSharedPreferences("MT5AlertPrefs", Context.MODE_PRIVATE)
                val wasServiceRunning = sharedPreferences.getBoolean("service_was_running", false)
                
                if (wasServiceRunning) {
                    Log.d(TAG, "Starting WebSocket service after boot")
                    
                    val websocketUrl = sharedPreferences.getString("websocket_url", "")
                    val ringtoneUri = sharedPreferences.getString("selected_ringtone", null)
                    
                    if (!websocketUrl.isNullOrEmpty()) {
                        val serviceIntent = Intent(context, WebSocketForegroundService::class.java)
                        serviceIntent.putExtra("websocket_url", websocketUrl)
                        ringtoneUri?.let { serviceIntent.putExtra("ringtone_uri", it) }
                        
                        ContextCompat.startForegroundService(context, serviceIntent)
                    }
                }
            }
        }
    }
}