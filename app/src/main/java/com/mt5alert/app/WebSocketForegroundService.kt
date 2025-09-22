package com.mt5alert.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketForegroundService : Service() {
    
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var reconnectAttempts = 0
    private var isReconnecting = false
    private var ringtone: Ringtone? = null
    private var websocketUrl: String = ""
    private var ringtoneUri: Uri? = null
    
    companion object {
        private const val TAG = "WebSocketService"
        private const val CHANNEL_ID = "websocket_channel"
        private const val NOTIFICATION_ID = 1
        private const val MAX_RECONNECT_ATTEMPTS = 10
        private const val RECONNECT_DELAY_MS = 5000L
        
        @Volatile
        var isServiceRunning = false
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        createNotificationChannel()
        setupOkHttpClient()
        acquireWakeLock()
        isServiceRunning = true
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        
        // Get WebSocket URL and ringtone from intent
        websocketUrl = intent?.getStringExtra("websocket_url") ?: ""
        val ringtoneUriString = intent?.getStringExtra("ringtone_uri")
        ringtoneUri = if (ringtoneUriString != null) Uri.parse(ringtoneUriString) else null
        
        if (websocketUrl.isEmpty()) {
            Log.e(TAG, "No WebSocket URL provided")
            stopSelf()
            return START_NOT_STICKY
        }
        
        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification("Connecting..."))
        
        // Setup ringtone
        setupRingtone()
        
        // Start WebSocket connection
        connectWebSocket()
        
        return START_STICKY // Restart if killed
    }
    
    private fun setupOkHttpClient() {
        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MT5Alert::WebSocketWakeLock"
        )
        wakeLock?.acquire(10*60*1000L /*10 minutes*/)
    }
    
    private fun setupRingtone() {
        try {
            ringtone = if (ringtoneUri != null) {
                RingtoneManager.getRingtone(this, ringtoneUri)
            } else {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(this, defaultUri)
            }
            
            // Configure audio attributes for alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                ringtone?.audioAttributes = audioAttributes
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ringtone", e)
        }
    }
    
    private fun connectWebSocket() {
        if (webSocket != null) {
            webSocket?.close(1000, "Reconnecting")
        }
        
        val request = Request.Builder()
            .url(websocketUrl)
            .build()
        
        webSocket = client?.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                reconnectAttempts = 0
                isReconnecting = false
                updateNotification("Connected - Listening for alerts")
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")
                handleAlert(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code $reason")
                updateNotification("Connection closing...")
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code $reason")
                updateNotification("Disconnected - Attempting reconnection...")
                scheduleReconnection()
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error", t)
                updateNotification("Connection failed - Reconnecting...")
                scheduleReconnection()
            }
        })
    }
    
    private fun handleAlert(message: String) {
        Log.d(TAG, "Processing alert: $message")
        
        // Play alarm sound
        playAlertSound()
        
        // Show notification
        showAlertNotification(message)
        
        // You can add additional processing here (parse JSON, extract specific fields, etc.)
    }
    
    private fun playAlertSound() {
        try {
            ringtone?.let { ring ->
                if (!ring.isPlaying) {
                    ring.play()
                    Log.d(TAG, "Playing alert sound")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alert sound", e)
        }
    }
    
    private fun showAlertNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
                PendingIntent.FLAG_IMMUTABLE 
            else 
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val alertNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MT5 Alert!")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), alertNotification)
    }
    
    private fun scheduleReconnection() {
        if (isReconnecting || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            return
        }
        
        isReconnecting = true
        reconnectAttempts++
        
        Log.d(TAG, "Scheduling reconnection attempt $reconnectAttempts")
        
        Thread {
            try {
                Thread.sleep(RECONNECT_DELAY_MS * reconnectAttempts)
                if (isServiceRunning) {
                    connectWebSocket()
                }
            } catch (e: InterruptedException) {
                Log.d(TAG, "Reconnection interrupted")
            }
        }.start()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WebSocket Connection",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Persistent connection to MT5 Scanner"
            channel.setShowBadge(false)
            
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(statusText: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
                PendingIntent.FLAG_IMMUTABLE 
            else 
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MT5 Alert Service")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun updateNotification(statusText: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(statusText))
    }
    
    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        
        isServiceRunning = false
        isReconnecting = false
        
        // Close WebSocket connection
        webSocket?.close(1000, "Service stopped")
        webSocket = null
        
        // Shutdown HTTP client
        client?.dispatcher?.executorService?.shutdown()
        client = null
        
        // Release wake lock safely
        wakeLock?.let { wl ->
            if (wl.isHeld) {
                wl.release()
            }
        }
        wakeLock = null
        
        // Stop ringtone
        ringtone?.stop()
        ringtone = null
        
        // Update service state in preferences
        val sharedPreferences = getSharedPreferences("MT5AlertPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("service_was_running", false).apply()
        
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}