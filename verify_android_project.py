#!/usr/bin/env python3
"""
Android Project Structure Verification Script
This script verifies that all required Android project files are present.
"""

import os
import sys

def check_file_exists(filepath, description):
    """Check if a file exists and print status"""
    if os.path.exists(filepath):
        print(f"✓ {description}: {filepath}")
        return True
    else:
        print(f"✗ MISSING {description}: {filepath}")
        return False

def check_directory_exists(dirpath, description):
    """Check if a directory exists and print status"""
    if os.path.isdir(dirpath):
        print(f"✓ {description}: {dirpath}")
        return True
    else:
        print(f"✗ MISSING {description}: {dirpath}")
        return False

def main():
    print("=" * 60)
    print("MT5 Alert Android Project Structure Verification")
    print("=" * 60)
    
    project_root = "MT5AlertApp"
    all_good = True
    
    # Check project structure
    print("\n📁 PROJECT STRUCTURE:")
    dirs_to_check = [
        (f"{project_root}", "Project root directory"),
        (f"{project_root}/app", "App module directory"),
        (f"{project_root}/app/src/main/java/com/mt5alert/app", "Main package directory"),
        (f"{project_root}/app/src/main/res", "Resources directory"),
        (f"{project_root}/gradle/wrapper", "Gradle wrapper directory"),
    ]
    
    for dirpath, description in dirs_to_check:
        if not check_directory_exists(dirpath, description):
            all_good = False
    
    # Check essential build files
    print("\n🔧 BUILD CONFIGURATION:")
    build_files = [
        (f"{project_root}/build.gradle", "Project build.gradle"),
        (f"{project_root}/app/build.gradle", "App build.gradle"),
        (f"{project_root}/settings.gradle", "Settings.gradle"),
        (f"{project_root}/gradle.properties", "Gradle properties"),
        (f"{project_root}/gradle/wrapper/gradle-wrapper.properties", "Gradle wrapper properties"),
    ]
    
    for filepath, description in build_files:
        if not check_file_exists(filepath, description):
            all_good = False
    
    # Check Android manifest and source files
    print("\n📱 ANDROID APPLICATION FILES:")
    android_files = [
        (f"{project_root}/app/src/main/AndroidManifest.xml", "Android Manifest"),
        (f"{project_root}/app/src/main/java/com/mt5alert/app/MainActivity.kt", "Main Activity"),
        (f"{project_root}/app/src/main/java/com/mt5alert/app/WebSocketForegroundService.kt", "WebSocket Service"),
        (f"{project_root}/app/src/main/java/com/mt5alert/app/BootReceiver.kt", "Boot Receiver"),
    ]
    
    for filepath, description in android_files:
        if not check_file_exists(filepath, description):
            all_good = False
    
    # Check resource files
    print("\n🎨 RESOURCE FILES:")
    resource_files = [
        (f"{project_root}/app/src/main/res/layout/activity_main.xml", "Main layout"),
        (f"{project_root}/app/src/main/res/values/strings.xml", "String resources"),
        (f"{project_root}/app/src/main/res/values/colors.xml", "Color resources"),
        (f"{project_root}/app/src/main/res/values/themes.xml", "Theme resources"),
        (f"{project_root}/app/src/main/res/drawable/ic_notification.xml", "Notification icon"),
    ]
    
    for filepath, description in resource_files:
        if not check_file_exists(filepath, description):
            all_good = False
    
    # Project analysis
    print("\n📊 PROJECT ANALYSIS:")
    
    # Count source files
    kotlin_files = []
    for root, dirs, files in os.walk(f"{project_root}/app/src/main/java"):
        for file in files:
            if file.endswith('.kt'):
                kotlin_files.append(os.path.join(root, file))
    
    print(f"✓ Found {len(kotlin_files)} Kotlin source files")
    
    # Check manifest for required permissions
    manifest_path = f"{project_root}/app/src/main/AndroidManifest.xml"
    if os.path.exists(manifest_path):
        with open(manifest_path, 'r') as f:
            manifest_content = f.read()
            required_permissions = [
                'android.permission.INTERNET',
                'android.permission.FOREGROUND_SERVICE',
                'android.permission.WAKE_LOCK'
            ]
            
            print("🔐 PERMISSION ANALYSIS:")
            for permission in required_permissions:
                if permission in manifest_content:
                    print(f"✓ Permission: {permission}")
                else:
                    print(f"✗ Missing permission: {permission}")
                    all_good = False
    
    # Final summary
    print("\n" + "=" * 60)
    if all_good:
        print("🎉 PROJECT VERIFICATION COMPLETE: ALL FILES PRESENT!")
        print("\n📋 NEXT STEPS:")
        print("1. Open Android Studio")
        print("2. Select 'Open an existing Android Studio project'")
        print("3. Navigate to the MT5AlertApp folder")
        print("4. Android Studio will automatically configure the project")
        print("5. Build and install the APK to your Android device")
        print("\n⚠️  REQUIREMENTS:")
        print("- Android Studio 2022.2.1 or newer")
        print("- Android device or emulator running Android 5.0+ (API 21+)")
        print("- USB debugging enabled (for physical device)")
    else:
        print("❌ PROJECT VERIFICATION FAILED: Missing files detected!")
        return 1
    
    print("=" * 60)
    return 0

if __name__ == "__main__":
    sys.exit(main())