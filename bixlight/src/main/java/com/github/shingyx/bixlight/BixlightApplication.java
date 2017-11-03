package com.github.shingyx.bixlight;

import android.app.Application;

public class BixlightApplication extends Application {
    private long maxRunFrequencyMs = 500;

    public long getMaxRunFrequencyMs() {
        return maxRunFrequencyMs;
    }

    public void setMaxRunFrequencyMs(long maxRunFrequencyMs) {
        this.maxRunFrequencyMs = maxRunFrequencyMs;
    }
}
