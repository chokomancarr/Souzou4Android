package com.pk.souzou1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TriggerAccel extends EventBase {
    public boolean useX = false;
    public boolean useY = false;
    public boolean useZ = false;
    public boolean invertX = false;
    public boolean invertY = false;
    public boolean invertZ = false;
    public float minX = 0;
    public float minY = 0;
    public float minZ = 0;
    public float valX, valY, valZ;
    public float gx, gy, gz;

    private SensorManager manager;
    private Sensor sensor, sensorG;
    private SensorEventListener listener, gListener;

    public void clone (EventBase e) {

    }

    public byte[] GetOutput () {
        return null;
    }

    public String Serialize () {
        return "";
    }

    public TriggerAccel() {
        manager = (SensorManager) main.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorG = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                valX = event.values[0];
                valY = event.values[1];
                valZ = event.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        gListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gx = event.values[0];
                gy = event.values[1];
                gz = event.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    public void SetRun (boolean b) {
        RunState = b;
        if (b)
            StartRun();
        else
            StopRun();
    }

    public void StartRun() {
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(gListener, sensorG, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void StopRun() {
        manager.unregisterListener(listener);
        manager.unregisterListener(gListener);
    }

    @Override
    public boolean Pass () {
        return false;
    }
}
