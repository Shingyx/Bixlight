package com.github.shingyx.bixlight;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String bixlightServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        View button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        bixlightServiceId = String.format("%s/.%s", getPackageName(), BixlightService.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        int resId = isBixlightEnabled() ? R.string.service_enabled : R.string.service_disabled;
        TextView serviceState = (TextView) findViewById(R.id.serviceState);
        serviceState.setText(getText(resId));
    }

    private boolean isBixlightEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        boolean ret = false;
        for (AccessibilityServiceInfo service : runningServices) {
            if (service.getId().equals(bixlightServiceId)) {
                ret = true;
                break;
            }
        }
        Log.v(TAG, "isBixlightEnabled = " + ret);
        return ret;
    }
}
