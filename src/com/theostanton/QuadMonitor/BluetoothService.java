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
import com.theostanton.QuadMonitor.statics.D;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by theo on 26/04/2014.
 */
public class BluetoothService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String BROADCAST_ACTION = "FRESHBTDATA";
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String BTSENDMESSAGE = "BTSENDMESSAGE";
    public static final String GETCOEFFS = "GETCOEFFS";


    public static final int KPid = 1;
    public static final int KIid = 2;
    public static final int KDid = 3;
    public static final int RATEid = 4;
    public static final int COMPid = 5;
    public static final int MAXINTEGRALid = 6;
    public static final int PINGid = 7;
    public static final int RXPINGid = 8;
    public static final int THROTTLEid = D.RXTHROTTLE;
    public static final int YAWid = D.RXYAW;
    public static final int PITCHLid = D.RXPITCH;
    public static final int ROLLid = D.RXROLL;

    //private Sender sender;



    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static InputStream inStream;
    private static OutputStream outStream;
    private final Handler handler = new Handler();
    private Receiver receiver;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
    private D d;
    private Intent intent;
    private BluetoothAdapter bluetooth;
    private Listen listen;
    private BluetoothSocket btSocket;
    private BluetoothDevice bluetoothDevice;

    private static byte[] getInt16(int val) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) val; // LSB
        bytes[1] = (byte) (val >> 8); // MSB
        // Log.d("bytes[0]",Integer.toBinaryString(bytes[0]));
        // Log.d("bytes[1]",Integer.toBinaryString(bytes[1]));
        return bytes;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        d = D.getInstance();
        Log.d(TAG, "onCreate()");
        intent = new Intent(BROADCAST_ACTION);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        receiver = new Receiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BTSENDMESSAGE);
        filter.addAction(CONNECT);
        filter.addAction(DISCONNECT);
        filter.addAction(GETCOEFFS);
        registerReceiver(receiver, filter);

//        sender = new Sender();
//        sender.start();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public void getCoeffs() { // requests coeff values
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

    public void setCoeffs() { // requests coeff values
        // Log.d(TAG, "setCoeffs()");
        // sendMessage(KPid, getInt16(D.getpVal()));
        // sendMessage(KIid, getInt16(D.getiVal()));
        // sendMessage(KDid, getInt16(D.getdVal()));
    }

    public void sendValue(int id, int value) {
        sendMessage(id, getInt16(value));
        //sender.put(id,getInt16(value));
    }

    public void connect() {
        Log.d(TAG, "Connect Bluteooth");
        Toast.makeText(this, "Connecting via bluetooth...", Toast.LENGTH_LONG).show();
        if (checkBTState()) {
            if (connectToDevice()) {
                listen = new Listen();
                listen.start();

                setCoeffs();
            }
        }
    }

    public void disConnect() {
        Log.d(TAG, "Disconnect Bluteooth");
        if (listen != null && listen.isAlive()) listen.end();
        try {
            if (outStream != null) outStream.close();
            if (inStream != null) inStream.close();
            if (btSocket != null) btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Bluetooth Disconnected.", Toast.LENGTH_SHORT).show();
    }

    private synchronized void sendMessageOLD(int id, byte[] bytes) {
        if (btSocket != null) {
            if (btSocket.isConnected()) {
                //Log.d(TAG, "Sending id : " + id);
                byte[] data = new byte[bytes.length + 1];
                data[0] = (byte) id;
                data[1] = bytes[1]; // MSB
                data[2] = bytes[0]; // LSB

                try {
                    outStream.write(data);
                } catch (IOException e) {
                    Log.d(TAG, "outStream.write() error");
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "btSocket not connected");
            }
        }

    }

    private synchronized void sendMessage(int id, byte[] bytes) {
        if (btSocket != null) {
            if (btSocket.isConnected()) {
                Log.d(TAG, "Sending id : " + id);
                byte[] data = new byte[bytes.length + 1];
                data[0] = (byte) id;
                data[1] = bytes[1]; // MSB
                data[2] = bytes[0]; // LSB

                try {
                    outStream.write(data);
                } catch (IOException e) {
                    Log.d(TAG, "outStream.write() error");
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "btSocket not connected");
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disConnect();
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
                Log.d(TAG, "Starting discovery");
                return true;
            } else {
                Log.e(TAG, "Bluetooth is not enabled...");
            }
        }
        return false;
    }

    private boolean connectToDevice() {

        //bluetoothDevice = bluetooth.getRemoteDevice("98:D3:31:50:0C:FE");
        bluetoothDevice = bluetooth.getRemoteDevice("20:14:08:08:25:10"); // Quad


//        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
//
//        for(BluetoothDevice bd:pairedDevices){
//            Log.d("Name",bd.getName());
//            Log.d("Address",bd.getAddress());
//        }
//
//        if(true) return false;

        Log.d(TAG, "Connecting to " + bluetoothDevice.getName());

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
        Log.e(TAG, "onSharedPreferenceChanged()");

        if (key.equals("sampleratePref")) {
            String str = sharedPreferences.getString("sampleratePref", "666");
            int rate = Integer.valueOf(str);
            Log.d(TAG, "rate = " + rate);
            sendValue(BluetoothService.RATEid, rate);
        } else if (key.equals("compPref")) {
            String str = sharedPreferences.getString("compPref", "666");
            int comp = Integer.valueOf(str);
            Log.d(TAG, "comp = " + comp);
            sendValue(BluetoothService.COMPid, comp);
        } else if (key.equals("max_integral")) {
            String str = sharedPreferences.getString("max_integral", "666");
            int comp = Integer.valueOf(str);
            Log.d(TAG, "max_integral = " + comp);
            sendValue(BluetoothService.MAXINTEGRALid, comp);
        } else if (key.equals("ping_checkbox")) {
            boolean state = sharedPreferences.getBoolean("ping_checkbox", true);
            Log.d(TAG, "ping_checkbox = " + state);
            sendValue(BluetoothService.PINGid, state ? 1 : 0);
        } else if (key.equals("rx_ping_checkbox")) {
            boolean state = sharedPreferences.getBoolean("rx_ping_checkbox", true);
            Log.d(TAG, "rx_ping_checkbox = " + state);
            sendValue(BluetoothService.RXPINGid, state ? 1 : 0);
        } else {
            Log.e(TAG, "onSharedPreferenceChanged() missed " + key);
        }


    }
//    class Sender extends Thread {
//
//        private boolean running = true;
//        private SparseArray<byte[]> sparseArray;
//
//        public Sender(){
//            sparseArray = new SparseArray<byte[]>();
//        }
//
//        @Override
//        public void run() {
//            while(running){
//                if(sparseArray.size() > 0){
//                    int key = sparseArray.keyAt(0);
//                    Log.d(TAG,"Key = " + key);
//                    sendMessage(key,sparseArray.get(key));
//                    sparseArray.remove(key);
//                }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void put(int id, byte[] value){
//            sparseArray.put(id,value);
//        }
//
//        private synchronized void sendMessage(int id, byte[] bytes) {
//            if(btSocket != null) {
//                if (btSocket.isConnected()) {
//                    //Log.d(TAG, "Sending id : " + id);
//                    byte[] data = new byte[bytes.length + 1];
//                    data[0] = (byte) id;
//                    data[1] = bytes[1]; // MSB
//                    data[2] = bytes[0]; // LSB
//
//                    try {
//                        outStream.write(data);
//                    } catch (IOException e) {
//                        Log.d(TAG, "outStream.write() error");
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d(TAG, "btSocket not connected");
//                }
//            }
//
//        }
//    }


    class Listen extends Thread {


        /*

        Packets:

        Desired

            id = D
            D0: desired Roll
            D1: desired Pitch
            D2: desired Yaw

        Measured

            id = M
            D0: measured Roll
            D1: measured Pitch
            D2: measured Yaw

        Error

            id = E
            D0: error Roll
            D1: error Pitch
            D2: error Yaw

        Accel

            id = A
            D0: accel Roll
            D1: accel Pitch
            D2: accel Yaw

        Gyro

            id = G
            D0: gyro Roll
            D1: gyro Pitch
            D2: gyro Yaw

        RX

            id = T
            D0: rawvalue CH1
            D1: rawvalue CH2
            D2: rawvalue CH3
            D3: rawvalue CH4
            D4: rawvalue CH5
            D5: rawvalue CH6

        PIDs

           id = a, b, c or d
           D0: p * 1000
           D1: i * 1000
           D2: d * 1000
           D3: correction

        TODO: Timer packet

         */

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
                        //Log.d(TAG,line);
                        //if(line.contains("::")) setCoeffValue(line);
                        if (line.contains("Error")) addToConsole(line, true);
                        else sortPacket(line);
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
            String[] strings = line.split(",");
            float[] packetData = new float[strings.length-1];

            switch( line.charAt(0) ){
                case '!':
                    D.updateLists();

                case 'a':
                case 'b':
                case 'c':
                case 'd':
                    for (int i = 0;  i < packetData.length-1; i++) {
                        packetData[i] = (float)Integer.parseInt(strings[i+1]) / 1000.0f;
                    }
                    break;
                case 'R':
                case 'M':
                case 'A':
                case 'G':
                case 'D':
                case 'E':
                    for (int i = 0;  i < packetData.length-1; i++) {
                        packetData[i] = (float)Integer.parseInt(strings[i+1])  ;
                    }
                    break;
                default:
                    Log.e(TAG, "Packet id error : " + line);
                    addToConsole(line, true);
            }

            D.sortPacket(line.charAt(0), packetData);

        }

//        private void sortPacketSGL(String line){
//            String[] strings = line.split(",");
//            int[] packet = new int[strings.length-1];
//            //Log.d(TAG,"us " + strings[strings.length-1]);
//            if("P".equals(strings[0]) ){
//                //Log.d(TAG,"receiving packet " + strings.length);
//                for (int i = 0;  i < packet.length-1; i++) {
//                    packet[i] = Integer.parseInt(strings[i+1]);
//                }
////                for(int i : packet){
////                    Log.d(TAG,"i" + i);
////                }
//                convertAndAddPacketQueue(packet);
//            }
//
//        }



//        private void convertAndAddPacketQueueSGL(int[] packet) {
//            float floatPacket[] = new float[packet.length];
//
//            for(int i=0; i<packet.length; i++){
//                if(i<17){
//                    floatPacket[i] = (float)packet[i];
//                }
//                else {
//                    floatPacket[i] = (float)packet[i] / 1000.0f;
//                }
//            }
//            d.addToPacketQueue(floatPacket);
//        }
//
//        private void sortPacketOLDUSINGFLOATS(String line){
//            String[] strings = line.split(",");
//            float[] packet = new float[strings.length];
//            for (int i = 0; i < strings.length; i++) {
//
//                if (strings[i].contains(".")) packet[i] = Float.parseFloat(strings[i]);
//                //Log.d(i + " ", String.valueOf(packet[i]));
//            }
//            //Log.d("string.size()",strings.length + " ");
//            long start = System.currentTimeMillis();
//            d.addToPacketQueue(packet);
//            long sortPacket = System.currentTimeMillis() - start;
//        }

        private void setValue(String line){
            String[] strings = line.split("::");
            Log.d(TAG,"line = " + line);
            if(strings.length == 2){
                int id = Integer.parseInt(strings[0]);
                float value = Float.parseFloat(strings[1]);
                D.setCoeffValue(id, value);
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

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BTSENDMESSAGE)) {
                int id = intent.getIntExtra("id", 0);
                int value = intent.getIntExtra("Value", 0);
                //Log.d(TAG, "Message I = " + id);
                //Log.d(TAG, "Message Value = " + value);
                sendMessage(id, getInt16(value));
                //sender.put(id, getInt16(value));
            } else if (action.equals(CONNECT)) {
                connect();
            } else if (action.equals(DISCONNECT)) {
                disConnect();
            } else if (action.equals(GETCOEFFS)) {
                getCoeffs();
            } else {
                Log.d(TAG, action);
            }
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
