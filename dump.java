package com.example.Android_Sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    private TextView viewAccX;
    private TextView viewAccY;
    private TextView viewAccZ;
    private TextView viewAccTot;

    private TextView viewGyroX;
    private TextView viewGyroY;
    private TextView viewGyroZ;

    private TextView viewOrientationX;
    private TextView viewOrientationY;
    private TextView viewOrientationZ;

    private float[] orientationData;
    private float[] orientationOffset;
    private boolean firstReading = true;

    private float[] gyroData;

    private float[] accData;
    private float[] lowPassComp = new float[3];
    private static float lpComp = 0.95f;
    private static float aLpComp = 0.05f;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        viewGyroX = (TextView)findViewById(R.id.sensorViewGyroX);
        viewGyroY = (TextView)findViewById(R.id.sensorViewGyroY);
        viewGyroZ = (TextView)findViewById(R.id.sensorViewGyroZ);

        viewOrientationX = (TextView)findViewById(R.id.sensorViewOrientationX);
        viewOrientationY = (TextView)findViewById(R.id.sensorViewOrientationY);
        viewOrientationZ = (TextView)findViewById(R.id.sensorViewOrientationZ);

        viewAccX = (TextView)findViewById(R.id.sensorViewAccX);
        viewAccY = (TextView)findViewById(R.id.sensorViewAccY);
        viewAccZ = (TextView)findViewById(R.id.sensorViewAccZ);
        viewAccTot = (TextView)findViewById(R.id.sensorViewAccTot);

        accData = new float[]{0.0f,0.0f,0.0f};
        gyroData = new float[]{0.0f,0.0f,0.0f};
        lowPassComp = new float[]{0.0f,0.0f,0.0f};
        orientationData = new float[]{0.0f,0.0f,0.0f};
        orientationOffset = new float[]{0.0f,0.0f,0.0f};
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        else if( event.sensor.getType() == Sensor.TYPE_GYROSCOPE ) {
            getGyroscope(event);
        }
        else if( event.sensor.getType() == Sensor.TYPE_ORIENTATION ) {
            getOrientation(event);
        }

    }



    private float[] lowPass(float[] packet) {
        for(int i=0; i<3; i++){
            lowPassComp[i]= lowPassComp[i] * lpComp + packet[i] * aLpComp;
            packet[i] = packet[i] - lowPassComp[i];
        }
        return packet;
    }

    private void getAccelerometer(SensorEvent event) {
        accData = lowPass( event.values );
        viewAccX.setText("x : " + String.format("%.2f", accData[0]) );
        viewAccY.setText("y : " + String.format("%.2f", accData[1]) );
        viewAccZ.setText("z : " + String.format("%.2f", accData[2]) );
        viewAccTot.setText("tot : " + String.format("%.2f", accData[1] + accData[1] + accData[2]));
    }



    private void getGyroscope(SensorEvent event) {
        gyroData = event.values;
        viewGyroX.setText("x : " + String.format("%.2f", gyroData[0]) );
        viewGyroY.setText("y : " + String.format("%.2f", gyroData[1]) );
        viewGyroZ.setText("z : " + String.format("%.2f", gyroData[2]) );
    }

    private void getOrientation(SensorEvent event) {
        if(firstReading) {
            for(int i=0; i<3; i++){
                orientationOffset[i] = event.values[i];
                Log.d("Orientation", String.valueOf(orientationOffset[0]));
            }
            firstReading = false;
            Log.d("Orientation", String.valueOf(event.values[0]));
        }
        for(int i=0; i<3; i++){
            orientationData[i] = event.values[i] - orientationOffset[i];
        }

        viewOrientationX.setText("x : " + String.format("%.2f", orientationData[0]) );
        viewOrientationY.setText("y : " + String.format("%.2f", orientationData[1]) );
        viewOrientationZ.setText("z : " + String.format("%.2f", orientationData[2]) );

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}