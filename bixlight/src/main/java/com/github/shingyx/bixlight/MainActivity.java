package com.github.shingyx.bixlight;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String bixlightServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        bixlightServiceId = String.format("%s/.%s", getPackageName(), BixlightService.class.getSimpleName());

        Button openSettings = findViewById(R.id.open_settings);
        openSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        EditText throttle = findViewById(R.id.max_frequency);
        long maxRunFrequencyMs = ((BixlightApplication) getApplication()).getMaxRunFrequencyMs();
        throttle.setText(String.format(Locale.getDefault(), "%d", maxRunFrequencyMs));
        throttle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                long value = !input.isEmpty() ? Long.parseLong(editable.toString()) : 0;
                ((BixlightApplication) getApplication()).setMaxRunFrequencyMs(value);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        int resId = isBixlightEnabled() ? R.string.service_enabled : R.string.service_disabled;
        TextView serviceState = findViewById(R.id.serviceState);
        serviceState.setText(getText(resId));
    }

    private boolean isBixlightEnabled() {
        boolean ret = false;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager != null) {
            List<AccessibilityServiceInfo> runningServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo service : runningServices) {
                if (service.getId().equals(bixlightServiceId)) {
                    ret = true;
                    break;
                }
            }
        }
        Log.v(TAG, "isBixlightEnabled = " + ret);
        return ret;
    }
}
