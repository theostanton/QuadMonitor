package com.theostanton.QuadMonitor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by theo on 26/04/2014.
 */
public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";

    private D d;

    private Intent intent;
    private final Handler handler = new Handler();

    private BluetoothAdapter bluetooth;
    private Listen listen;
    private InputStream inStream;
    private BluetoothSocket btSocket;
    private BluetoothDevice bluetoothDevice;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void onCreate() {
        super.onCreate();
        d = D.getInstance();
        Log.d(TAG, "onCreate()");
        intent = new Intent(BROADCAST_ACTION);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(this, "Connecting via bluetooth...", Toast.LENGTH_LONG).show();
        if(checkBTState()) {
            if (connectToDevice()) {
                listen = new Listen();
                listen.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(listen != null && listen.isAlive()) listen.end();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
        Toast.makeText(this, "Bluetooth Disconnected.", Toast.LENGTH_SHORT).show();
    }

    public void sendInfo(String data){
        //Log.d(TAG,"sendInfo()");
        intent.putExtra("data", data);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private boolean checkBTState() {
        if(bluetooth==null) {
            Log.e(TAG,"Bluetooth NOT supported. Aborting.");
            return false;
        } else {
            if (bluetooth.isEnabled()) {
                Log.d(TAG, "Bluetooth is enabled...");
                bluetooth.startDiscovery();
                return true;
            } else {
                Log.e(TAG,"Bluetooth is not enabled...");
            }
        }
        return false;
    }

    private boolean connectToDevice(){
        bluetoothDevice = bluetooth.getRemoteDevice("98:D3:31:50:0C:FE");
        Log.d(TAG,"Conencting to " + bluetoothDevice.getName());

        try {
            btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            Toast.makeText(this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Connection made.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "Unable to end the connection");
            }
            Log.e(TAG, "Socket creation failed");
            return false;
        }

        try {
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG,"Couldn't get inStream");
            e.printStackTrace();
            return false;
        }
        return true;

    }



    class Listen extends Thread {

        boolean running = true;

        public Listen() {
            Log.d(TAG, "Listen()");
            try {
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                Log.e("Listen", "Couldn't get instream");
            }
        }

        public void run() {
            Log.d(TAG, "Listen.start()");
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while (running) {
                try {
                    if ((line = in.readLine()) != null) {
                        String[] strings = line.split(" ");
                        float[] packet = new float[strings.length];
                        //Log.e("Values", "In");
                        for (int i = 0; i < strings.length; i++) {
                            if (strings[i].contains(".")) packet[i] = Float.parseFloat(strings[i]);
                            //Log.d(i + " ", String.valueOf(packet[i]));
                        }
                        //Log.d("string.size()",strings.length + " ");
                        long start = System.currentTimeMillis();
                        d.addToPacketQueue(packet);
                        long sortPacket = System.currentTimeMillis() - start;
                        //Log.d("sortPacket ms", String.valueOf(sortPacket));
//                        new Thread() {
//                            public void run() {
//
//                            }
//                        }.start();
                    }
                    sendBroadcast(intent);

                    Thread.sleep(1);
                } catch (Exception e) {
                    Log.e("Listener", "Error", e);
                }
            }
        }

        public int toInt(byte hb, byte lb) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[]{hb, lb});
            return bb.getShort(); // Implicitly widened to an int per JVM spec.
        }

        public void end() {
            running = false;
        }
    }

//    class Updater extends Thread {
//
//        Queue<Float[]> queue;
//
//        public Updater(){
//            queue = new LinkedList<Float[]>();
//
//        }
//
//        public void run(){
//            while(running){
//
//            }
//        }
//
//    }

}
