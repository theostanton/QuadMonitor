package com.theostanton.QuadMonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.theostanton.QuadMonitor.dials.Dial;
import com.theostanton.QuadMonitor.graphs.Graph;
import com.theostanton.QuadMonitor.pid.Motor;

/**
 * Created by theo on 23/04/2014.
 */
public class FocusActivity extends Activity implements View.OnTouchListener{

    private static final String TAG = "Focus Activity";
    private D d;
    private Component view;
    private Ticker ticker;
    private ScaleGestureDetector mScaleDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        d = D.getInstance();
        getActionBar().hide();

        Intent intent = getIntent();
        int[] id = intent.getIntArrayExtra("ID");
        String VIEW = intent.getStringExtra("VIEW");

        if(VIEW.equals("DIAL")) {
            view = new Dial(this, null,true);
        }
        else if(VIEW.equals("MOTOR")) {
            view = new Motor(this, null,true);
        }
        else if(VIEW.equals("GRAPH")) {
            d.setFocusGraph(true);
            view = new Graph(this, null,true);
        }
        else Log.e(TAG,"Get VIEw error : " + VIEW);

        view.setOnTouchListener(this);
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        view.setFocused(true);
        view.set(id);


        Log.d(TAG,"id = " + id);


        setContentView(view);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(G.automate) {
            ticker = new Ticker();
            ticker.start();
        }
        else if(G.bluetooth){
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothService.BROADCAST_ACTION));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        d.setFocusGraph(false);
        try{
            if(broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e){

        }
    }

    public void update(){
        view.postInvalidate();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG,"onReceive()");
            //D.setAllRandom();
            update();
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view instanceof Graph)  mScaleDetector.onTouchEvent(motionEvent);
        Log.d(TAG,"pointerCount " + motionEvent.getPointerCount());
        if(motionEvent.getPointerCount() > 1) return true;
        //finish();
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG,detector.getCurrentSpanY() + " SpanY");
            Log.d(TAG,detector.getScaleFactor() + " scale Factor");
            d.xScaleFactor(detector.getScaleFactor());
            //mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            //mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            update();
            return true;
        }
    }

    class Ticker extends Thread {

        private int period = 100;

        public Ticker() {
            Log.d("Ticker", "Create");
        }

        @Override
        public void run() {
            Log.d("Ticker", "Start");
            while (true) {
                try {

                    //if (Global.automate) {
                    if (G.automate) {
                        //Log.d("Ticker", "Tick");
                        new Thread() {
                            public void run() {
                                D.setAllRandom();
                                update();
                            }
                        }.start();
                        //sleep(Global.updatePeriod);
                        sleep( G.interval );
                    } else {
                        sleep(500);
                    }
                } catch (Exception e) {
                    Log.e("Ticker", "Catch error");
                    e.printStackTrace();
                }
            }

        }

    }
}
