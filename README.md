# MT5 Alert Android Application

A lightweight Android application that connects to your MT5 Scanner WebSocket server and triggers loud alarms for real-time trading alerts.

## 🚀 Features

- **Real-time WebSocket connection** to MT5 Scanner server
- **Background operation** with foreground service (Android 8+ compatible)
- **Loud alarm notifications** when alerts are received
- **Customizable alert sounds** (choose from system ringtones)
- **Auto-reconnection** if connection is lost
- **Persistent connection** even when app is minimized
- **Auto-start on device boot** (optional)
- **Compatible with Android 5.0 - 11.0** (API 21-30)

## 📋 Requirements

### Development Requirements
- **Android Studio** 2022.2.1 or newer
- **Android SDK** with API level 21+ (Android 5.0+)
- **Kotlin** support enabled
- **Java 8** or newer

### Device Requirements
- **Android 5.0+** (API level 21 or higher)
- **Internet connection** (WiFi or mobile data)
- **Notification permissions** (Android 13+)
- **Background app permissions** enabled

## 🛠️ Setup Instructions

### 1. Open in Android Studio

1. Download or clone this project
2. Open **Android Studio**
3. Select **"Open an existing Android Studio project"**
4. Navigate to the `MT5AlertApp` folder
5. Click **"Open"**
6. Wait for Android Studio to sync the project

### 2. Build the Project

1. In Android Studio, go to **Build → Clean Project**
2. Then **Build → Rebuild Project**
3. Ensure no build errors appear

### 3. Generate APK

**For Development/Testing:**
1. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. APK will be generated in `app/build/outputs/apk/debug/`

**For Release:**
1. Go to **Build → Generate Signed Bundle / APK**
2. Follow the signing wizard to create a release APK

## 📱 Installation & Configuration

### 1. Install the APK

**Method A - Android Studio:**
1. Connect your Android device via USB
2. Enable **Developer Options** and **USB Debugging**
3. Click **Run** button in Android Studio

**Method B - Manual Installation:**
1. Transfer the APK file to your Android device
2. Enable **"Install from Unknown Sources"** in Settings
3. Tap the APK file to install

### 2. Configure WebSocket Connection

1. **Open the MT5 Alert app**
2. **Enter WebSocket URL:**
   - **For Replit MT5 Scanner:** `wss://your-replit-domain.replit.dev`
   - **For local network:** `ws://192.168.1.100:8080`
   - The app comes pre-configured for Replit deployment
   - Ensure your MT5 Scanner is running with WebSocket enabled

3. **Select Alert Sound:**
   - Tap **"Select Sound"** button
   - Choose from system notification sounds
   - Pick a loud, distinctive sound for alerts

4. **Start the Service:**
   - Tap **"Start Service"** button
   - Grant any requested permissions
   - Service will start and show "Connected" status

### 3. Grant Required Permissions

The app will request these permissions:

- **📡 Internet Access** - To connect to WebSocket server
- **🔔 Notification Access** - To show alert notifications  
- **🔋 Battery Optimization** - To run in background
- **📱 Foreground Service** - To maintain persistent connection

**Important:** Disable battery optimization for this app to ensure reliable background operation.

## 🔧 Configuration Examples

### WebSocket URL Examples

```
# Replit MT5 Scanner (RECOMMENDED)
wss://your-replit-domain.replit.dev

# Local network (same WiFi)
ws://192.168.1.100:8080

# Remote server with SSL
wss://your-mt5-server.com

# Local development
ws://localhost:8000
```

**🔒 Security Note:** Always use `wss://` (secure WebSocket) for internet connections. The app defaults to the secure Replit configuration.

### MT5 Scanner Setup

Your MT5 Scanner should broadcast JSON messages like:

```json
{
  "type": "alert",
  "symbol": "EURUSD",
  "signal": "BUY",
  "price": 1.0850,
  "time": "2024-01-15T10:30:00Z"
}
```

The Android app will trigger an alarm for **any** message received from the WebSocket.

## 🔔 How It Works

### Background Operation

1. **Foreground Service:** The app runs a foreground service that maintains the WebSocket connection
2. **Persistent Notification:** A small notification shows connection status
3. **Auto-Reconnect:** If connection drops, the app automatically attempts to reconnect
4. **Wake Lock:** Prevents device from sleeping and losing connection

### Alert Flow

```
MT5 Scanner → WebSocket → Android App → Alarm Sound + Notification
```

1. MT5 Scanner detects trading signal
2. Sends alert via WebSocket
3. Android app receives message instantly
4. Plays loud alarm sound
5. Shows notification with alert details

## 🛠️ Troubleshooting

### Connection Issues

**Problem:** Can't connect to WebSocket server
- ✅ Verify MT5 Scanner is running
- ✅ Check WebSocket URL is correct
- ✅ Ensure devices are on same network (if local)
- ✅ Check firewall settings on MT5 Scanner computer

**Problem:** Connection keeps dropping
- ✅ Disable battery optimization for the app
- ✅ Add app to "Auto-start" whitelist (manufacturer specific)
- ✅ Ensure stable internet connection

### Audio Issues

**Problem:** No alarm sound when alerts arrive
- ✅ Check device volume levels
- ✅ Ensure "Do Not Disturb" is disabled
- ✅ Test sound selection in app settings
- ✅ Grant notification permissions

### Background Issues

**Problem:** Service stops when app is closed
- ✅ Disable battery optimization
- ✅ Add to protected apps list (MIUI, EMUI, etc.)
- ✅ Enable "Allow background activity"
- ✅ Check foreground service permissions

## 🔧 Advanced Configuration

### Auto-Start on Boot

The app includes a boot receiver that automatically starts the service when the device boots up, if it was previously running.

### Battery Optimization

For reliable operation, disable battery optimization:

1. Go to **Settings → Battery → Battery Optimization**
2. Find **"MT5 Alert"** in the list
3. Select **"Don't optimize"**

### Manufacturer-Specific Settings

**Xiaomi (MIUI):**
- Settings → Apps → Manage Apps → MT5 Alert → Battery Saver → No restrictions

**Huawei (EMUI):**
- Settings → Apps → MT5 Alert → Battery → App Launch → Manage manually

**Samsung (One UI):**
- Settings → Apps → MT5 Alert → Battery → Optimize battery usage → Turn off

## 📊 Technical Details

### Architecture
- **Kotlin** - Main programming language
- **OkHttp** - WebSocket client library
- **Foreground Service** - Background operation
- **Notification System** - Alert delivery
- **Shared Preferences** - Configuration storage

### Supported Android Versions
- **Minimum:** Android 5.0 (API 21)
- **Target:** Android 14 (API 34)
- **Tested:** Android 5, 6, 7, 8, 9, 10, 11

### Performance
- **Memory Usage:** ~15-25 MB
- **Battery Impact:** Minimal (foreground service optimized)
- **Network Usage:** Very low (WebSocket messages only)

## 🆘 Support & Issues

### Common Issues
1. **Service won't start:** Check all permissions granted
2. **No internet:** Verify network connectivity  
3. **No sound:** Check volume and Do Not Disturb settings
4. **Connection drops:** Disable battery optimization
5. **Connection blocked on Android 9+:** Use `wss://` (secure) instead of `ws://` for internet connections
6. **Replit connection fails:** Verify the Replit domain is correct and MT5 Scanner is running

### Debug Information
- Check service status in app
- View persistent notification for connection status
- Enable USB debugging and check Android Studio logcat for detailed logs

## 📄 License

This project is provided as-is for MT5 trading alert purposes. Modify and distribute as needed for your trading setup.

---

**⚠️ Important:** This app is designed for real-time trading alerts. Ensure your MT5 Scanner and network connection are reliable for critical trading decisions.