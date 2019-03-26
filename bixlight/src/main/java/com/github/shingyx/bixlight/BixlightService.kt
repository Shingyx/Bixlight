package com.github.shingyx.bixlight

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import java.lang.ref.WeakReference

private val TAG = BixlightService::class.java.simpleName
private val BIXBY_PACKAGES = hashSetOf(
        "com.samsung.android.app.spage",
        "com.samsung.android.bixby.agent"
)

class BixlightService : AccessibilityService() {
    private lateinit var cameraManager: CameraManager
    private lateinit var bixlightPreferences: BixlightPreferences
    private var cameraId: String? = null
    private var torchCallback: CameraManager.TorchCallback? = null
    private var torchEnabled: Boolean = false
    private var lastRunMillis: Long = 0

    override fun onServiceConnected() {
        Log.v(TAG, "onServiceConnected")
        cameraManager = getSystemService(CameraManager::class.java)
        bixlightPreferences = BixlightPreferences(this)
        setupCameraIfNeeded()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val activeWindowPackage = getActiveWindowPackage()

        Log.v(TAG, "onAccessibilityEvent: [type] ${AccessibilityEvent.eventTypeToString(event.eventType)} [time] ${event.eventTime} [activeWindowPackage] $activeWindowPackage")

        val currentMillis = System.currentTimeMillis()
        val runTooSoon = currentMillis - lastRunMillis < bixlightPreferences.getSavedMaxRunFrequencyMs()

        if (runTooSoon || activeWindowPackage !in BIXBY_PACKAGES) {
            return
        }

        if (setupCameraIfNeeded()) {
            Log.v(TAG, "turning torch ${if (torchEnabled) "OFF" else "ON"}")
            lastRunMillis = currentMillis
            try {
                cameraManager.setTorchMode(cameraId, !torchEnabled)
            } catch (e: CameraAccessException) {
                Log.v(TAG, "failed to toggle torch")
            }
        }
        DelayedBackButtonTask(this).execute()
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.v(TAG, "onUnbind")
        if (torchCallback != null) {
            cameraManager.unregisterTorchCallback(torchCallback)
            Log.v(TAG, "unregistered torch callback")
        }
        return false
    }

    private fun getActiveWindowPackage(): String? {
        return rootInActiveWindow?.packageName?.toString()
    }

    private fun setupCameraIfNeeded(): Boolean {
        if (cameraId != null) {
            return true
        }
        try {
            cameraId = cameraManager.cameraIdList[0]  // Usually back camera is at 0 position
        } catch (e: CameraAccessException) {
            Log.v(TAG, "failed to set up camera")
            return false
        }

        torchEnabled = false
        torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                torchEnabled = enabled
            }
        }
        cameraManager.registerTorchCallback(torchCallback, Handler())
        Log.v(TAG, "registered torch callback")
        return true
    }

    private class DelayedBackButtonTask(context: BixlightService) : AsyncTask<Unit, Unit, Unit>() {
        private val serviceReference: WeakReference<BixlightService> = WeakReference(context)

        override fun doInBackground(vararg args: Unit) {
            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                Log.v(TAG, "interrupted")
            }
            serviceReference.get()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        }
    }
}
