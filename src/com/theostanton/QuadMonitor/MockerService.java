package com.theostanton.QuadMonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import com.theostanton.QuadMonitor.statics.D;

/**
 * Created by theo on 15/05/2014.
 */
public class MockerService extends Service implements SensorEventListener {

    public static final String BROADCAST_ACTION = "FRESHMOCKDATA";
    public static final String START = "START";
    public static final String STOP = "STOP";
    private static final String TAG = "MockerService";
    private SensorManager sensorManager;

    private boolean recalib = true;

    private D d;
    private Receiver receiver;
    private Intent intent;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        d = D.getInstance();
        intent = new Intent(BluetoothService.BROADCAST_ACTION);

        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(STOP);
        registerReceiver(receiver, filter);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    public void start() {
        Log.d(TAG, "Start");
        recalib = true;
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_UI);
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
//                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        Log.d(TAG, "Stop");
        sensorManager.unregisterListener(this);
        unregisterReceiver(receiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            getAccelerometer(event);
//        }
//        else if( event.sensor.getType() == Sensor.TYPE_GYROSCOPE ) {
//            getGyroscope(event);
//        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            getOrientation(event);
        }
    }

    private void getOrientation(SensorEvent event) {
        if (recalib) {
            recalib = false;
            d.setOrientationOffset(event.values);
        }
        d.setOrientation(event.values);
        sendBroadcast(intent);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(START)) {
                start();
            } else if (action.equals(STOP)) {
                stop();
            }
        }
    }
}
