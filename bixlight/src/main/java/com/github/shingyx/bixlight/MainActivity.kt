package com.github.shingyx.bixlight

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

private val TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {
    private lateinit var bixlightServiceId: String
    private lateinit var bixlightPreferences: BixlightPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        bixlightServiceId = "$packageName/.${BixlightService::class.java.simpleName}"
        bixlightPreferences = BixlightPreferences(this)

        openSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        maxFrequency.setText(bixlightPreferences.getSavedMaxRunFrequencyMs().toString())
        maxFrequency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                val input = editable.toString()
                val value = if (input.isNotEmpty()) Integer.parseInt(input) else 0
                bixlightPreferences.saveMaxRunFrequencyMs(value)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        val resId = if (isBixlightEnabled()) R.string.service_enabled else R.string.service_disabled
        serviceState.text = getText(resId)
    }

    private fun isBixlightEnabled(): Boolean {
        var ret = false
        val accessibilityManager = getSystemService(AccessibilityManager::class.java)
        if (accessibilityManager != null) {
            val runningServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
            ret = runningServices.any { it.id == bixlightServiceId }
        }
        Log.v(TAG, "isBixlightEnabled = $ret")
        return ret
    }
}
