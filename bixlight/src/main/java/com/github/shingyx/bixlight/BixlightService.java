package com.github.shingyx.bixlight;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class BixlightService extends AccessibilityService {

    private static final String TAG = BixlightService.class.getSimpleName();

    private CameraManager cameraManager;
    private String cameraId;
    private CameraManager.TorchCallback torchCallback;
    private boolean torchEnabled;

    @Override
    protected void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        setupCameraIfNeeded();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                AccessibilityEvent.eventTypeToString(event.getEventType()), event.getClassName(),
                event.getPackageName(), event.getEventTime(), getEventText(event)));
        if (setupCameraIfNeeded()) {
            try {
                cameraManager.setTorchMode(cameraId, !torchEnabled);
            } catch (CameraAccessException e) {
                Log.v(TAG, "failed to toggle torch");
            }
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // noop
                }
                performGlobalAction(GLOBAL_ACTION_BACK);
                return null;
            }
        }.execute();
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        if (torchCallback != null) {
            cameraManager.unregisterTorchCallback(torchCallback);
            Log.v(TAG, "unregistered torch callback");
        }
        return false;
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder builder = new StringBuilder();
        for (CharSequence s : event.getText()) {
            builder.append(s);
        }
        return builder.toString();
    }

    private boolean setupCameraIfNeeded() {
        if (cameraId != null) {
            return true;
        }
        try {
            cameraId = cameraManager.getCameraIdList()[0];  // Usually back camera is at 0 position
        } catch (CameraAccessException e) {
            Log.v(TAG, "failed to set up camera");
            return false;
        }
        torchEnabled = false;
        Handler handler = new Handler();
        torchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                torchEnabled = enabled;
            }
        };
        cameraManager.registerTorchCallback(torchCallback, handler);
        Log.v(TAG, "registered torch callback");
        return true;
    }
}
