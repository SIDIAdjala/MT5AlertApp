package com.mt5alert.app

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editTextWebSocketUrl: EditText
    private lateinit var textViewSelectedSound: TextView
    private lateinit var buttonSelectSound: Button
    private lateinit var buttonStartService: Button
    private lateinit var buttonStopService: Button
    private lateinit var textViewStatus: TextView
    
    private var selectedRingtoneUri: Uri? = null
    
    companion object {
        private const val PREFS_NAME = "MT5AlertPrefs"
        private const val KEY_WEBSOCKET_URL = "websocket_url"
        private const val KEY_SELECTED_RINGTONE = "selected_ringtone"
        // URL par défaut pour usage local (remplacez 192.168.1.100 par votre adresse IP)
        private const val DEFAULT_WEBSOCKET_URL = "ws://192.168.1.100:8000?token=mt5_scanner_2024"
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
        private const val REQUEST_RINGTONE_PICK = 1002
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        
        initViews()
        setupClickListeners()
        loadSettings()
        requestNotificationPermission()
        updateServiceStatus()
    }
    
    private fun initViews() {
        editTextWebSocketUrl = findViewById(R.id.editTextWebSocketUrl)
        textViewSelectedSound = findViewById(R.id.textViewSelectedSound)
        buttonSelectSound = findViewById(R.id.buttonSelectSound)
        buttonStartService = findViewById(R.id.buttonStartService)
        buttonStopService = findViewById(R.id.buttonStopService)
        textViewStatus = findViewById(R.id.textViewStatus)
    }
    
    private fun setupClickListeners() {
        buttonSelectSound.setOnClickListener {
            selectRingtone()
        }
        
        buttonStartService.setOnClickListener {
            saveSettings()
            startWebSocketService()
        }
        
        buttonStopService.setOnClickListener {
            stopWebSocketService()
        }
    }
    
    private fun loadSettings() {
        val savedUrl = sharedPreferences.getString(KEY_WEBSOCKET_URL, DEFAULT_WEBSOCKET_URL)
        editTextWebSocketUrl.setText(savedUrl)
        
        val savedRingtone = sharedPreferences.getString(KEY_SELECTED_RINGTONE, null)
        if (savedRingtone != null) {
            selectedRingtoneUri = Uri.parse(savedRingtone)
            val ringtone = RingtoneManager.getRingtone(this, selectedRingtoneUri)
            textViewSelectedSound.text = "Selected: ${ringtone?.getTitle(this) ?: "Default"}"
        } else {
            textViewSelectedSound.text = "Default notification sound"
        }
    }
    
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_WEBSOCKET_URL, editTextWebSocketUrl.text.toString().trim())
        selectedRingtoneUri?.let {
            editor.putString(KEY_SELECTED_RINGTONE, it.toString())
        }
        editor.apply()
    }
    
    private fun setServiceRunningState(isRunning: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("service_was_running", isRunning)
        editor.apply()
    }
    
    private fun selectRingtone() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alert Sound")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri)
        startActivityForResult(intent, REQUEST_RINGTONE_PICK)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_RINGTONE_PICK && resultCode == RESULT_OK) {
            selectedRingtoneUri = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            
            if (selectedRingtoneUri != null) {
                val ringtone = RingtoneManager.getRingtone(this, selectedRingtoneUri)
                textViewSelectedSound.text = "Selected: ${ringtone?.getTitle(this) ?: "Custom"}"
            } else {
                textViewSelectedSound.text = "Default notification sound"
            }
            saveSettings()
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 
                    REQUEST_NOTIFICATION_PERMISSION)
            }
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            REQUEST_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Notification permission required for alerts", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun startWebSocketService() {
        val url = editTextWebSocketUrl.text.toString().trim()
        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter WebSocket URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intent = Intent(this, WebSocketForegroundService::class.java)
        intent.putExtra("websocket_url", url)
        selectedRingtoneUri?.let {
            intent.putExtra("ringtone_uri", it.toString())
        }
        
        ContextCompat.startForegroundService(this, intent)
        setServiceRunningState(true)
        Toast.makeText(this, "Starting WebSocket service...", Toast.LENGTH_SHORT).show()
        updateServiceStatus()
    }
    
    private fun stopWebSocketService() {
        val intent = Intent(this, WebSocketForegroundService::class.java)
        stopService(intent)
        setServiceRunningState(false)
        Toast.makeText(this, "Stopping WebSocket service...", Toast.LENGTH_SHORT).show()
        updateServiceStatus()
    }
    
    private fun updateServiceStatus() {
        // This is a simplified status check
        // In a real implementation, you might use a BroadcastReceiver or other mechanism
        val isServiceRunning = WebSocketForegroundService.isServiceRunning
        textViewStatus.text = if (isServiceRunning) {
            "Status: Service Running"
        } else {
            "Status: Service Stopped"
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }
}