package com.theostanton.QuadMonitor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by theo on 26/04/2014.
 */
public class BluetoothService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String BROADCAST_ACTION = "FRESHBTDATA";
    public static final String BTSENDMESSAGE = "BTSENDMESSAGE";
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        private static final String TAG = "BTBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BTSENDMESSAGE)) {
                int id = intent.getIntExtra("id", 0);
                int value = intent.getIntExtra("Value", 0);
                //Log.d(TAG, "Message I = " + id);
                //Log.d(TAG, "Message Value = " + value);
                sendMessage(id, getInt16(value));
            } else {
                Log.d(TAG, action);
            }
        }
    };
    public static final int KPid = 1;
    public static final int KIid = 2;
    public static final int KDid = 3;
    public static final int RATEid = 4;
    public static final int COMPid = 5;
    public static final int MAXINTEGRALid = 6;
    public static final int THROTTLEid = D.RXTHROTTLE;
    public static final int YAWid = D.RXYAW;
    public static final int PITCHLid = D.RXPITCH;
    public static final int ROLLid = D.RXROLL;
    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static InputStream inStream;
    private static OutputStream outStream;
    private final Handler handler = new Handler();
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
    private D d;
    private Intent intent;
    private BluetoothAdapter bluetooth;
    private Listen listen;
    private BluetoothSocket btSocket;
    private BluetoothDevice bluetoothDevice;

    public static void sendMessage(String message) {
        Log.d(TAG, "sendMessage() : " + message);
    }

    public static void getCoeffs() { // requests coeff values
        Log.d(TAG, "getCoeffs()");
        try {
            outStream.write((byte) (KPid + 100));
            outStream.write((byte) (KIid + 100));
            outStream.write((byte) (KDid + 100));
            Log.d(TAG, "Request sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setCoeffs() { // requests coeff values
        Log.d(TAG, "setCoeffs()");
        sendMessage(KPid, getInt16(D.getpVal()));
        sendMessage(KIid, getInt16(D.getiVal()));
        sendMessage(KDid, getInt16(D.getdVal()));
    }

    public static String getValue(int id){
        Log.d(TAG,"Getting id: " + id);
        try {
            outStream.write((byte)(id+100));
            return "Sent";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Not Sent";
    }

    public static void sendValue(int id, int value) {
        sendMessage(id, getInt16(value));
    }

    private static void sendMessage(int id, byte[] bytes) {
        Log.d(TAG, "Sending id : " + id);
        byte[] data = new byte[bytes.length + 1];
        data[0] = (byte)id;
        data[1] = bytes[1]; // MSB
        data[2] = bytes[0]; // LSB

        // data[0] = 1;
        // data[1] = 0;
        // data[2] = 3;

        try {
            outStream.write(data);
        } catch (IOException e) {
            Log.d(TAG,"outStream.write() error");
            e.printStackTrace();
        }
    }

    private static byte[] getInt16(int val) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) val; // LSB
        bytes[1] = (byte) (val >> 8); // MSB
        // Log.d("bytes[0]",Integer.toBinaryString(bytes[0]));
        // Log.d("bytes[1]",Integer.toBinaryString(bytes[1]));
        return bytes;
    }

//    public void sendMessage(int i, int val){
//        switch (i){
//            case P:
//            case I:
//            case D:
//                sendMessage(i,)
//
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        d = D.getInstance();
        Log.d(TAG, "onCreate()");
        intent = new Intent(BROADCAST_ACTION);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(this, "Connecting via bluetooth...", Toast.LENGTH_LONG).show();
        if (checkBTState()) {
            if (connectToDevice()) {
                listen = new Listen();
                listen.start();
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BTSENDMESSAGE);
        registerReceiver(receiver, filter);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        if (listen != null && listen.isAlive()) listen.end();
        try {
            if (outStream != null) outStream.close();
            if (inStream != null) inStream.close();
            if (btSocket != null) btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
        Toast.makeText(this, "Bluetooth Disconnected.", Toast.LENGTH_SHORT).show();
        unregisterReceiver(receiver);
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);
    }

    public void sendInfo(String data) {
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
        if (bluetooth == null) {
            Log.e(TAG, "Bluetooth NOT supported. Aborting.");
            return false;
        } else {
            if (bluetooth.isEnabled()) {
                Log.d(TAG, "Bluetooth is enabled...");
                bluetooth.startDiscovery();
                return true;
            } else {
                Log.e(TAG, "Bluetooth is not enabled...");
            }
        }
        return false;
    }

    private boolean connectToDevice() {
        bluetoothDevice = bluetooth.getRemoteDevice("98:D3:31:50:0C:FE");
        Log.d(TAG, "Conencting to " + bluetoothDevice.getName());

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
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get inStream");
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private void toastError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals("sampleratePref")) {
            String str = sharedPreferences.getString("sampleratePref", "666");
            int rate = Integer.valueOf(str);
            Log.d(TAG, "rate = " + rate);
            BluetoothService.sendValue(BluetoothService.RATEid, rate);
        } else if (key.equals("compPref")) {
            String str = sharedPreferences.getString("compPref", "666");
            int comp = Integer.valueOf(str);
            //int iComp = (int)(comp * 1000.0f);
            Log.d(TAG, "comp = " + comp);
            BluetoothService.sendValue(BluetoothService.COMPid, comp);
        } else if (key.equals("max_integral")) {
            String str = sharedPreferences.getString("max_integral", "666");
            int comp = Integer.valueOf(str);
            //int iComp = (int)(comp * 1000.0f);
            Log.d(TAG, "max_integral = " + comp);
            BluetoothService.sendValue(BluetoothService.MAXINTEGRALid, comp);
        } else {
            Log.e(TAG, "onSharedPreferenceChanged() missed " + key);
        }


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
            setCoeffs();
        }

        public void run() {
            Log.d(TAG, "Listen.start()");
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while (running) {
                try {
                    if ((line = in.readLine()) != null) {
                        if(line.contains("::")) setValue(line);
                        if (line.contains("Error")) addToConsole(line, true);
                        else if(line.length() > 20) sortPacket(line);
                        else addToConsole(line, true);
                    }
                    sendBroadcast(intent);

                    Thread.sleep(1);
                } catch (IOException e) {
                    //running = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void addToConsole(String line, boolean error) {
            D.appendConsole(line);
            if (error) Log.e("Data In", line);
            //else Log.d("Data In", line);
        }

        private void sortPacket(String line){
            String[] strings = line.split(" ");
            float[] packet = new float[strings.length];
            for (int i = 0; i < strings.length; i++) {

                if (strings[i].contains(".")) packet[i] = Float.parseFloat(strings[i]);
                //Log.d(i + " ", String.valueOf(packet[i]));
            }
            //Log.d("string.size()",strings.length + " ");
            long start = System.currentTimeMillis();
            d.addToPacketQueue(packet);
            long sortPacket = System.currentTimeMillis() - start;
        }

        private void setValue(String line){
            String[] strings = line.split("::");
            Log.d(TAG,"line = " + line);
            if(strings.length == 2){
                int id = Integer.parseInt(strings[0]);
                float value = Float.parseFloat(strings[1]);
                D.setValue(id,value);
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
