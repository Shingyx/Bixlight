package com.github.shingyx.bixlight

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent

private val TAG = BixlightService::class.java.simpleName
private val BIXBY_PACKAGE = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
    "com.samsung.android.app.spage"
} else {
    "com.samsung.android.bixby.agent"
}

class BixlightService : AccessibilityService() {
    private lateinit var cameraManager: CameraManager
    private lateinit var handler: Handler
    private lateinit var bixlightPreferences: BixlightPreferences
    private lateinit var torchCallback: CameraManager.TorchCallback
    private lateinit var cameraId: String
    private var torchEnabled: Boolean = false
    private var lastRunMillis: Long = 0

    override fun onServiceConnected() {
        Log.v(TAG, "onServiceConnected")
        cameraManager = getSystemService(CameraManager::class.java)!!
        handler = Handler()
        bixlightPreferences = BixlightPreferences(this)
        torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                torchEnabled = enabled
            }
        }

        cameraManager.registerTorchCallback(torchCallback, handler)
        Log.v(TAG, "registered torch callback")
        hasCameraId()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val activeWindowPackage = getActiveWindowPackage()

        Log.v(TAG, "onAccessibilityEvent: [type] ${AccessibilityEvent.eventTypeToString(event.eventType)} [time] ${event.eventTime} [activeWindowPackage] $activeWindowPackage")

        val currentMillis = System.currentTimeMillis()
        val runTooSoon = currentMillis - lastRunMillis < bixlightPreferences.getSavedMaxRunFrequencyMs()

        if (runTooSoon || activeWindowPackage != BIXBY_PACKAGE) {
            return
        }

        if (hasCameraId()) {
            Log.v(TAG, "turning torch ${if (torchEnabled) "OFF" else "ON"}")
            lastRunMillis = currentMillis
            try {
                cameraManager.setTorchMode(cameraId, !torchEnabled)
            } catch (e: CameraAccessException) {
                Log.v(TAG, "failed to toggle torch")
            }
        }

        handler.postDelayed({
            performGlobalAction(GLOBAL_ACTION_BACK)
        }, 50)
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.v(TAG, "onUnbind")
        cameraManager.unregisterTorchCallback(torchCallback)
        Log.v(TAG, "unregistered torch callback")
        return false
    }

    private fun getActiveWindowPackage(): String? {
        return rootInActiveWindow?.packageName?.toString()
    }

    private fun hasCameraId(): Boolean {
        if (!this::cameraId.isInitialized) {
            try {
                cameraId = cameraManager.cameraIdList[0]  // Usually back camera is at 0 position
            } catch (e: CameraAccessException) {
                Log.v(TAG, "failed to get camera id")
                return false
            }
        }
        return true
    }
}
