package com.example.cse535;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class RespiratoryRateService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] accelX = new float[451];
    private float[] accelY = new float[451];
    private float[] accelZ = new float[451];
    private int currentIndex = 0;
    private boolean collectingData = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startDataCollection();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDataCollection();
    }

    private void startDataCollection() {
        if (!collectingData) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            collectingData = true;
        }
    }

    private void stopDataCollection() {
        if (collectingData) {
            sensorManager.unregisterListener(this);
            collectingData = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentIndex++; // Circular buffer
            accelX[currentIndex] = event.values[0];
            accelY[currentIndex] = event.values[1];
            accelZ[currentIndex] = event.values[2];

            if (currentIndex >= 450) {
                currentIndex = 0;
                stopDataCollection(); // Process data and stop collection
                calculateAndReportRespiratoryRate();
            }
        }
    }

    private void calculateAndReportRespiratoryRate() {
        int respiratoryRate = callRespiratoryCalculator();
        Intent resultIntent = new Intent("respiratory_rate_calculated");
        resultIntent.putExtra("respiratory_rate", respiratoryRate);
        sendBroadcast(resultIntent);
    }

    private int callRespiratoryCalculator() {
        float previousValue = 10f;
        float currentValue = 0f;
        int k = 0;

        for (int i = 11; i <= 450; i++) {
            currentValue = (float) Math.sqrt(
                    Math.pow(accelZ[i], 2.0) +
                            Math.pow(accelX[i], 2.0) +
                            Math.pow(accelY[i], 2.0)
            );
            if (Math.abs(previousValue - currentValue) > 0.15) {
                k++;
            }
            previousValue = currentValue;
        }

        double ret = (double) k / 45.00;
        return (int) (ret * 30);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
