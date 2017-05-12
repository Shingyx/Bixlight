package com.github.shingyx.bixlight;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String bixlightServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        bixlightServiceId = String.format("%s/.%s", getPackageName(), BixlightService.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        int resId = isBixlightEnabled() ? R.string.service_enabled : R.string.service_disabled;
        TextView serviceState = (TextView) findViewById(R.id.serviceState);
        serviceState.setText(getText(resId));
    }

    private boolean isBixlightEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo service : runningServices) {
            if (service.getId().equals(bixlightServiceId)) {
                return true;
            }
        }
        return false;
    }
}
