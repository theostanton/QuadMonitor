package com.theostanton.QuadMonitor;

import android.content.Intent;
import android.graphics.*;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by theo on 18/03/2014.
 */
public class D{ // Singleton. make thread safer

    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    public final static int ROLL = 0;
    public final static int PITCH = 1;
    public final static int YAW = 2;
    public final static int DES = 0;
    public final static int MES = 1;
    public final static int ERR = 2;
    public final static int DESROLL = 0;
    public final static int MESROLL = 1;
    public final static int ERRROLL = 2;
    public final static int GYROROLL = 3;

    //Graphs
    public final static int ACCROLL = 4;
    public final static int DESPITCH = 5;
    public final static int MESPITCH = 6;
    public final static int ERRPITCH = 7;
    public final static int GYROPITCH = 8;
    public final static int ACCPITCH = 9;
    public final static int DESYAW = 10;
    public final static int MESYAW = 11;
    public final static int ERRYAW = 12;
    public final static int GYROYAW = 13;
    public final static int ACCYAW = 14;
    public final static int pA = 15;
    public final static int iA = 16;
    public final static int dA = 17;
    public final static int pB = 18;
    public final static int iB = 19;
    public final static int dB = 20;
    public final static int pC = 21;
    public final static int iC = 22;
    public final static int dC = 23;
    public final static int pD = 24;
    public final static int iD = 25;
    public final static int dD = 26;
    public final static int ERRROLLINTEGRAL = 27;
    public final static int ERRPITCHINTEGRAL = 28;
    public final static int RXTHROTTLE = 29;
    public final static int RXYAW = 30;
    public final static int RXPITCH = 31;
    public final static int RXROLL = 32;
    private static final String TAG = "D";
    private static final int LISTLIMIT = 2048;
    public static boolean showControls = true;
    public static int VALUES = 55;
    public static SparseArray<String> names;
    public static int graphWidth = 2000;
    public static boolean fresh = true;
    public static Random r = new Random();
    public static float textSize = 60.0f;
    public static boolean freshConsole = false;
    private static D instance;
    private static boolean updating = false;
    private static SparseArray<Value> values;
    private static Bitmap bitMap;
    ;
    private static SparseArray<LinkedList<Float>> lists;
    private static Canvas[] canvases;
    private static Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static Bitmap  bmp;
    private static int graphW;
    private static int graphH;
    private static int focusGraphW;
    private static int focusGraphH;
    private static boolean focusGraph = false;
    private static int[] graphColours;
    private static float xScale = 2.0f;
    private static int pVal = -1;
    private static int iVal = -1;
    private static int dVal = -1;
    private static HashMap<Integer,Path> paths;
    private static int latest = -1;
    private static float decimalPlaces = 10.0f;
    private static int pathX;
    private static int raw;
    private static int rawLabels;
    private static int rawValues;
    private static String[] axisTitle;
    private static float sampleRate = 20.0f;
    private static int sampleRateOffset = 0;
    private static Updater updater;
    private static String console;
    private Intent intent;
    private Bitmap[] bitMaps;
    private float[] orientation;
    private float[] orientationOffset;

    private D() {
        super();
        Log.e(TAG, "init()");
        intent = new Intent(BROADCAST_ACTION);

        values = new SparseArray<Value>();
        for (int i = 0; i <= 28; i++) {
            values.put(i, new Value());
        }

        orientationOffset = new float[]{0.0f, 0.0f, 0.0f};

        values.get(pA).enableMovingAverager();
        values.get(iA).enableMovingAverager();
        values.get(dA).enableMovingAverager();
        values.get(pB).enableMovingAverager();
        values.get(iB).enableMovingAverager();
        values.get(dB).enableMovingAverager();
        values.get(pC).enableMovingAverager();
        values.get(iC).enableMovingAverager();
        values.get(dC).enableMovingAverager();
        values.get(pD).enableMovingAverager();
        values.get(iD).enableMovingAverager();
        values.get(dD).enableMovingAverager();

        lists = new SparseArray<LinkedList<Float>>(9);
        lists.put(DESROLL, new LinkedList<Float>());
        lists.put(MESROLL, new LinkedList<Float>());
        lists.put(ERRROLL, new LinkedList<Float>());
        lists.put(DESPITCH, new LinkedList<Float>());
        lists.put(MESPITCH, new LinkedList<Float>());
        lists.put(ERRPITCH, new LinkedList<Float>());
        lists.put(DESYAW, new LinkedList<Float>());
        lists.put(MESYAW, new LinkedList<Float>());
        lists.put(ERRYAW, new LinkedList<Float>());
        lists.put(ERRROLLINTEGRAL, new LinkedList<Float>());
        lists.put(ERRPITCHINTEGRAL, new LinkedList<Float>());

        canvases = new Canvas[3];

        bitMaps = new Bitmap[3];
        bitMaps[ROLL] = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        bitMaps[PITCH] = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        bitMaps[YAW] = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

        graphColours = new int[4];
        graphColours[0] = Color.BLUE;
        graphColours[1] = Color.GREEN;
        graphColours[2] = Color.RED;
        graphColours[3] = Color.LTGRAY;

        setNames();
    }

    public static void setCoeffsFromDefaultPreference(int kp, int ki, int kd) {
        if (pVal == -1) pVal = kp;
        if (iVal == -1) iVal = ki;
        if (dVal == -1) dVal = kd;
        Log.d(TAG, "pVal = " + pVal);
    }

    private static void addToList(int ID, float val) {
        lists.get(ID).addLast(val);
        if (lists.get(ID).size() > LISTLIMIT) lists.get(ID).removeFirst();
    }

    public synchronized static void updateLists() {
//        for(int i : lists.keySet()){
//            addToList(i,val(i));
//        }

        for (int i = 0; i < lists.size(); i++) {
            int key = lists.keyAt(i);
            addToList(key, val(key));
        }

        updateBitmaps();

//        bitMaps[ROLL] = createBitMap(new int[]{DESROLL, MESROLL, ERRROLL});
//        bitMaps[PITCH] = createBitMap(new int[]{DESPITCH, MESPITCH, ERRPITCH});
//        bitMaps[YAW] = createBitMap(new int[]{DESYAW, MESYAW, ERRYAW});

        if (sampleRateOffset < sampleRate) sampleRateOffset++;
        else sampleRateOffset = 0;

    }

    public static void updateBitmaps(){
        createBitMap(ROLL, new int[]{DESROLL, MESROLL, ERRROLL, ERRROLLINTEGRAL});
        createBitMap(PITCH, new int[]{DESPITCH, MESPITCH, ERRPITCH, ERRPITCHINTEGRAL});
        createBitMap(YAW, new int[]{DESYAW, MESYAW, ERRYAW});
    }

    private static synchronized void createBitMap(int axisID, int[] IDs) {

        //Canvas c = canvases[axisID];
        float w, h;

        if (focusGraph) {
            w = focusGraphW;
            h = focusGraphH;
        } else {
            w = graphW;
            h = graphH;
        }
        if (w == 0) w = 1.0f;
        if (h == 0) h = 1.0f;
        float ctrY = h / 2.0f;
        float yScale = ctrY / 60.0f;

//        if(bmp != null){
//            //bmp.recycle();
//            bmp = null;
//        }


        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        // xScale : x gap between points
        // values : points on bitmap
        // index : start value in list
        LinkedList<Float> list = lists.get(IDs[0]);
        int size = list.size();
        int values = (int) (w / xScale);
        float y = ctrY;

        ListIterator<Float> it;
        int cIndex = 0;

        //canvases[axisID].drawColor(Color.BLACK);
        Path[] paths = new Path[4];

        int j = 0;
        for (int id : IDs) {
            float x = 0.0f;
            float prev;
            float prev2;
            list = lists.get(id);
            if (values > size) {
                x = xScale * (values - size);
                it = list.listIterator();
                prev = ctrY;
                prev2 = ctrY;
            } else {
                int index = size - values;
                it = list.listIterator(index);
                prev = list.peekFirst();
                prev *= yScale;
                prev += ctrY;
                prev2 = prev;
            }
            paths[j] = new Path();
            paths[j].moveTo(x, prev);
            x += xScale;
            try {
                while (it.hasNext()) {
                    y = it.next();
                    y *= yScale;
                    y += ctrY;
                    //c.drawLine(x, prev, x + xScale, y, p);
                    //path.cubicTo(x - xScale, prev2, x,prev, x + xScale,y);
                    paths[j].quadTo(x, prev, x + xScale, y);
                    x += xScale;
                    //prev2 = prev;
                    prev = y;
                }
                paths[j].lineTo(w, y);
            } catch (Exception e) {
                Log.e(TAG, "plot error");
                e.printStackTrace();
            }
            j++;
        }

        p.setStrokeWidth(2.0f);
        canvases[axisID].drawColor(Color.BLACK);
        for (int i = 0; i < IDs.length; i++) {
            p.setColor(graphColours[i]);
            canvases[axisID].drawPath(paths[i], p);
        }

        p.setColor(Color.WHITE);
        float xAcc = xScale * sampleRate;
        for (float xx = w - sampleRateOffset * xScale; xx > 0.0f; xx -= xAcc) {
            canvases[axisID].drawLine(xx, ctrY - 4.0f, xx, ctrY + 4.0f, p);
        }


        //return Bitmap.createBitmap(bmp);
        //return bmp;

    }

    private static Bitmap createBitMapDRAWLINES(int[] IDs) {


        float w,h;

        if(focusGraph) {
            w = focusGraphW;
            h = focusGraphH;
        }
        else {
            w = graphW;
            h = graphH;
        }
        float ctrY = h / 2.0f;
        float yScale = ctrY / 60.0f;

//        if(bmp != null){
//            bmp.recycle();
//            bmp = null;
//        }
        bmp = Bitmap.createBitmap((int) w, (int) h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        p.setColor(Color.RED);
        // xScale : x gap between points
        // values : points on bitmap
        // index : start value in list
        LinkedList<Float> list = lists.get(IDs[0]);
        int size = list.size();
        int values = (int) ( w / xScale );
        float y = 0.0f;

        ListIterator<Float> it;
        int cIndex = 0;
        for(int id : IDs) {
            p.setColor(graphColours[cIndex++]);
            float x = 0.0f;
            float prev;
            list = lists.get(id);
            if (values > size) {
                x = xScale * (values - size);
                it = list.listIterator();
                prev  = ctrY;
            } else {
                int index = size - values;
                it = list.listIterator(index);
                prev  = list.peekFirst();
                prev *= yScale;
                prev += ctrY;
            }
            while (it.hasNext()) {
                y = it.next();
                y *= yScale;
                y += ctrY;
                c.drawLine(x, prev, x + xScale, y, p);
                x += xScale;
                prev = y;
            }
            c.drawLine(x, y, w, y, p);
        }

        return bmp;

    }

    private static void setNames() {

        axisTitle = new String[3];
        axisTitle[ROLL] = "Roll";
        axisTitle[PITCH] = "Pitch";
        axisTitle[YAW] = "Yaw";

        values.get(GYROROLL).setName("Gyro Roll");
        values.get(GYROPITCH).setName("Gyro Pitch");
        values.get(GYROYAW).setName("Gyro Yaw");
        values.get(ACCROLL).setName("Accel Roll");
        values.get(ACCPITCH).setName("Accel Pitch");
        values.get(ACCYAW).setName("Accel Yaw");
        values.get(MESROLL).setName("Measured Roll");
        values.get(MESPITCH).setName("Measured Pitch");
        values.get(MESYAW).setName("Measured Yaw");
        values.get(ERRROLL).setName("Error Roll");
        values.get(ERRPITCH).setName("Error Pitch");
        values.get(ERRYAW).setName("Error Yaw");
        values.get(ERRROLLINTEGRAL).setName("Roll Error Integral");
        values.get(ERRPITCHINTEGRAL).setName("Pitch Error Integral");

        values.get(pA).setName("P A");
        values.get(iA).setName("I A");
        values.get(dA).setName("D A");
        values.get(pB).setName("P B");
        values.get(iB).setName("I B");
        values.get(dB).setName("D B");
        values.get(pC).setName("P C");
        values.get(iC).setName("I C");
        values.get(dC).setName("D C");
        values.get(pD).setName("P D");
        values.get(iD).setName("I D");
        values.get(dD).setName("D D");


    }

    public static void updated() {
        latest++;
    }

    public static int getpVal() {
        return pVal;
    }

    public static int getiVal() {
        return iVal;
    }

//    public static void updateLists() {
//        addToList(DESROLL, val(DESROLL));
//        addToList(MESROLL, val(MESROLL));
//        addToList(ERRROLL, val(DESROLL) - val(MESROLL));
//        addToList(DESPITCH, 2*val(DESPITCH));
//        addToList(MESPITCH, 2*val(MESPITCH));
//        addToList(ERRPITCH, 2* ( val(DESPITCH) - val(MESPITCH) ));
//        addToList(DESYAW, val(DESYAW));
//        addToList(MESYAW, val(MESYAW));
//        addToList(ERRYAW, val(ERRYAW) - val(MESYAW));
//
//        updateErrors();
//    }
//
//    public static void updateListsSoftly() {
//        addSoftlyToList(DESROLL, val(21));
//        addSoftlyToList(MESROLL, val(22));
//        addSoftlyToList(ERRROLL, val(12) - val(22));
//        addSoftlyToList(DESPITCH, 2 * val(26));
//        addSoftlyToList(MESPITCH, 2 * val(27));
//        addSoftlyToList(ERRPITCH, 2 * (val(26) - val(27)));
//        addSoftlyToList(DESYAW, val(31));
//        addSoftlyToList(MESYAW, val(32));
//        addSoftlyToList(ERRYAW, val(31) - val(32));
//        updateErrors();
//    }

    public static int getdVal() {
        return dVal;
    }

//    public static void setpVal(int pVal) {
//        this.pVal = pVal;
//    }

    public static float val(int i) {
        if (values == null) return 0.0f;
        return values.get(i).getVal();
    }

//    public static void setiVal(int iVal) {
//        D.iVal = iVal;
//    }

    public static float max(int i) {
        return values.get(i).max;
    }

//    public static void setdVal(int dVal) {
//        D.dVal = dVal;
//    }

    public static float min(int i) {
        return values.get(i).min;
    }

    public static void setAllRandom() {
        for (int i = 0; i < values.size(); i++) {
            values.valueAt(i).tickRandom();
        }
        updateLists();
        updated();
    }

    public static String getName(int i) {
        return values.get(i).getName();
    }


//    public static void addSoftlyToList(int list, float val) {
//
//        //TODO: This isn't random the way it should be.
//        //TODO: Implement perlin noise
//        //Log.d(TAG,String.valueOf(val));
//        //System.out.println(val);
//        int dampening = 10;
//        float v = (int) val;
//        //v = Math.max(0, v);
//        //v = Math.min(90, v);
//        v *= 4;
//
//        ArrayList<Float> l = graphLists.get(list);
//
//        for (int i = 1; i < dampening; i++) {
//            v += l.get(graphWidth - i) + 45.0f;
//        }
//
//        v /= dampening + 3;
//        v -= 45.0f;
//        //Log.d(TAG,String.valueOf(v));
//        //v *= 3;
//        //v /= 2;
//        //v *= 2;
//        //System.out.println(v);
//
//        l.remove(0);
//        l.add(v);
//    }

//    public static ArrayList<Float> getList(int id) {
//        if(graphLists != null) {
//            if(id == -1){
//                Log.d("getList(id)","id == -1");
//                return new ArrayList<Float>(graphLists.get(0));
//            }
//            return new ArrayList<Float>(graphLists.get(id));
//        }
//        else return null;
//    }

//    public static void addToList(int list, float val) {
//        // ArrayList<Float> l = graphLists.get(list);
//        // int v = (int) val + l.get(l.size() - 1);
//        // v /= 2;
//        ArrayList<Float> l = graphLists.get(list);
//        if(l.size() > 0) {
//            l.remove(0);
//            l.add(val);
//        }
//    }

    public static float getVal(int i) {
        return val(i);
    }

    public static String getStringVal(int i) {
        float rounded = Math.round(val(i) * decimalPlaces) / decimalPlaces;
        return String.valueOf(rounded);
    }

    public synchronized static void sortPacket(float[] packet) {
        int i = 0;
        int a = 0;
        if (packet.length == 26) {
            switch (a) {
                case 0:
                    values.get(GYROROLL).setVal(packet[i++]);
                    values.get(GYROPITCH).setVal(packet[i++]);
                    values.get(GYROYAW).setVal(packet[i++]);
                    values.get(ACCROLL).setVal(packet[i++]);
                    values.get(ACCPITCH).setVal(packet[i++]);
                    values.get(MESYAW).setVal(packet[i++] / 2.0f); // TODO heading vs angle
                    values.get(MESROLL).setVal(packet[i++]);
                    values.get(MESPITCH).setVal(packet[i++]);
                    values.get(ERRROLL).setVal(packet[i++]);
                    values.get(ERRPITCH).setVal(packet[i++]);
                    values.get(ERRYAW).setVal(packet[i++]);
                    values.get(ERRROLLINTEGRAL).setVal(packet[i++]);
                    values.get(ERRPITCHINTEGRAL).setVal(packet[i++]);
                    i++; //values.get(MESYAW).setVal(packet[i++]);


                    values.get(pA).setVal(packet[i++]);
                    values.get(iA).setVal(packet[i++]);
                    values.get(dA).setVal(packet[i++]);
                    values.get(pB).setVal(packet[i++]);
                    values.get(iB).setVal(packet[i++]);
                    values.get(dB).setVal(packet[i++]);
                    values.get(pC).setVal(packet[i++]);
                    values.get(iC).setVal(packet[i++]);
                    values.get(dC).setVal(packet[i++]);
                    values.get(pD).setVal(packet[i++]);
                    values.get(iD).setVal(packet[i++]);
                    values.get(dD).setVal(packet[i++]);
            }
            //updateLists();
        } else {
            Log.e("Packet Size", "" + packet.length);
        }
    }

    public static String getRaw() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 26; i++) {
            sb.append(i + " " + values.get(i).getName() + " " + getStringVal(i) + "\n");
        }
        return sb.toString();
    }

    public static String getRawLabels() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i).getName() + " \n");
        }
        return sb.toString();
    }

    public static String getRawValues() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(" " + getStringVal(i) + " \n");
        }
        return sb.toString();
    }

    public static String getAxisTitle(int dID) {
        return axisTitle[dID];
    }

    public static void xScaleFactor(float scaleFactor) {
        xScale *= scaleFactor;
        updateLists();
        Log.d("Scale Factor", String.valueOf(xScale));
    }

    public static float getXScale() {
        return xScale;
    }

    public static float getSampleRate() {
        return sampleRate;
    }

    public static int getSampleRateOffset() {
        return (int) ((float) sampleRateOffset * xScale);
    }

    public static boolean updating() {
        return updating;
    }

    public static void setValue(int id, float value) { // from bluetooth
        switch (id) {
            case BluetoothService.KPid:
                pVal = (int) (value * 1000.0f);
                Log.d(TAG, "pVal set to : " + pVal);
                break;
            case BluetoothService.KIid:
                iVal = (int) (value * 1000.0f);
                Log.d(TAG, "iVal set to : " + iVal);
                break;
            case BluetoothService.KDid:
                dVal = (int) (value * 1000.0f);
                Log.d(TAG, "dVal set to : " + dVal);
                break;
            default:
                Log.e(TAG, "setValue id error " + id);
        }
    }

    public static D getInstance() {
        if (instance == null) instance = new D();
        return instance;
    }

    public static String getConsole() {
        freshConsole = false;
        return console;
    }

    public static void appendConsole(String toAppend) {
        console += toAppend;
        console += "\n";
        freshConsole = true;
    }

    public static void setControl(int controlID, int val) {
        switch (controlID) {
            case RXPITCH:
                values.get(DESPITCH).setVal(val);
                updateError(PITCH);
                break;
            case RXROLL:
                values.get(DESROLL).setVal(val);
                updateError(ROLL);
                break;
//            case RXTHROTTLE :
//                values.get(DESTHROTTLE).setVal(val);
//                break;
            case RXYAW:
                values.get(DESYAW).setVal(val);
                updateError(YAW);
                break;
        }
    }

    private static void updateError(int axisID) {
        switch (axisID) {
            case ROLL:
                values.get(ERRROLL).setVal(
                        -val(MESROLL) - val(DESROLL));
                break;
            case PITCH:
                values.get(ERRPITCH).setVal(
                        -val(MESPITCH) - val(DESPITCH));
                break;
            case YAW:
                values.get(ERRYAW).setVal(
                        -val(MESYAW) - val(DESYAW));
                break;
        }
    }

    public void setGraphBounds(int w, int h) {
        if (focusGraph) {
            focusGraphW = w;
            focusGraphH = h;
        } else {
            graphW = w;
            graphH = h;
        }


        for (int i = 0; i < 3; i++) {
            bitMaps[i] = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            canvases[i] = new Canvas(bitMaps[i]);
        }

        updateBitmaps();

    }

    public void setFocusGraph(boolean state) {
        focusGraph = state;
    }

    public synchronized Bitmap getBmp(int axisID) {
        if (axisID == -1) return Bitmap.createBitmap(graphW, graphH, Bitmap.Config.RGB_565);
        while (updating) {
            try {
                wait();
            } catch (Exception e) {
                Log.e(TAG, "Wait on updating in getBmp error");
            }
        }
        return bitMaps[axisID];
    }

    public int addToPacketQueue(float[] packet) {
        if (updater == null) {
            ///Log.d(TAG,"updater == null");
            updater = new Updater(packet);
            updater.start();
        } else if (updater.isAlive()) {
            //Log.d(TAG,"updater.isAlive()");
            updater.add(packet);
        } else {
            //Log.d(TAG,"updater != null && notAlive()");
            updater = new Updater(packet);
            updater.start();
        }
        return updater.queueSize();
    }

    public synchronized void notifyFinishedUpdating() {
        updating = false;
        notifyAll();
    }

    public void setOrientationOffset(float[] orientation) {
        orientationOffset = orientation;
    }

    public void setOrientation(float[] orientation) {
        values.get(MESROLL).setVal(orientation[2]); //- orientationOffset[2]);
        values.get(MESPITCH).setVal(orientation[1]); // - orientationOffset[1]);
        values.get(MESYAW).setVal(orientation[0]); // orientationOffset[0]);
        updateErrors();
        updateLists();
    }

    private void updateErrors() {
        int i = 0;
        while (i < 3) updateError(i++);
    }

    class Updater extends Thread {

        private static final String TAG = "Updater";

        ArrayBlockingQueue<float[]> packetQueue;
        private boolean running = true;

        public Updater(float[] packet) {
            //Log.d(TAG,"constructor");
            packetQueue = new ArrayBlockingQueue<float[]>(10);
            packetQueue.add(packet);
        }

        @Override
        public void run() {
            int count = 0;
            //Log.d(TAG,"run");
            while (running) {
                if (!packetQueue.isEmpty()) {
                    updating = true;
                    while (!packetQueue.isEmpty()) {
                        //Log.d(TAG,"while");
                        count++;
                        try {
                            sortPacket(packetQueue.take());
                        } catch (InterruptedException e) {
                            Log.e(TAG, "packetQueue.take() error");
                            e.printStackTrace();
                        }
                        if (count > 1) Log.d(String.valueOf(count), "in a row");
                        if (!packetQueue.isEmpty()) Log.e(TAG, packetQueue.size() + " in packetQueue");
                    }
                    updateLists();
                    count = 0;
                }
                notifyFinishedUpdating();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Log.d(TAG, "end while");


        }

        public void add(float[] packet) {
            if (packetQueue.size() < 10) {
                packetQueue.add(packet);
                //Log.e(TAG, "Added to queue. " + packetQueue.size() + " in packetQueue");
            }
            //else Log.e(TAG, "packetQeue full");
        }

        public int queueSize() {
            return packetQueue.size();
        }
    }
}