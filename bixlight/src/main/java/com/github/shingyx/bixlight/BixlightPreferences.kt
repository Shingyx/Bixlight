package com.github.shingyx.bixlight

import android.content.Context
import android.content.SharedPreferences

private const val SHARED_PREFERENCES_NAME = "BixlightData"
private const val SAVED_MAX_RUN_FREQUENCY_MS = "MaxRunFrequencyMs"
private const val MAX_RUN_FREQUENCY_MS_DEFAULT = 500

class BixlightPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getSavedMaxRunFrequencyMs(): Int {
        return sharedPreferences.getInt(SAVED_MAX_RUN_FREQUENCY_MS, MAX_RUN_FREQUENCY_MS_DEFAULT)
    }

    fun saveMaxRunFrequencyMs(maxRunFrequencyMs: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(SAVED_MAX_RUN_FREQUENCY_MS, maxRunFrequencyMs)
        editor.apply()
    }

}
