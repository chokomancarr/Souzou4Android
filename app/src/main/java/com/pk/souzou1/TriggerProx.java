package com.pk.souzou1;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import java.util.Locale;

public class TriggerProx extends EventBase {
    public float f = 0;
    public float threshold = 0;
    public float max = 0;
    public boolean invert;
    //boolean rec = false;

    SensorManager manager;
    Sensor sensor;
    SensorEventListener listener;

    public void clone (EventBase e) {
        TriggerProx t = (TriggerProx)e;
        f = t.f;
        threshold = t.threshold;
        invert = t.invert;
    }

    public byte[] GetOutput() {
        return null;
    }

    public String Serialize () {
        return type + "&" + String.format(Locale.JAPAN, "%.2f", threshold) + "&" + (invert ? "1" : "0");
    }

    public TriggerProx(MainActivity m) {
        main = m;
        manager = (SensorManager) main.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        max = sensor.getMaximumRange();
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                f = event.values[0];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public void SetThres (float p) {
        threshold = max * p;
    }

    @Override
    public void SetRun(boolean b) {
        RunState = b;
        if (b)
            StartRun();
        else
            StopRun();
    }

    public void StartRun() {
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void StopRun() {
        manager.unregisterListener(listener);
        f = invert ? 0 : max;
    }

    @Override
    public boolean Pass() {
        return invert ? (threshold < f) : (threshold > f);
    }
}
