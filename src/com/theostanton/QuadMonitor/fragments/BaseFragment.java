package com.theostanton.QuadMonitor.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import com.theostanton.QuadMonitor.BluetoothService;
import com.theostanton.QuadMonitor.D;
import com.theostanton.QuadMonitor.FocusActivity;
import com.theostanton.QuadMonitor.G;

import java.util.ArrayList;

import static java.lang.Math.max;

/**
 * Created by theo on 23/04/2014.
 */
public class BaseFragment extends Fragment implements View.OnTouchListener{

    protected String TAG = "BaseFragmet";
    protected String VIEW = "UNASSIGNED";

    protected D d;

    protected View layoutView;
    private ScaleGestureDetector mScaleDetector;

    protected ArrayList<View> views;

    protected Ticker ticker;
    protected float dx = 0.0f;
    protected float dy = 0.0f;
    protected float lastx = 0.0f;
    protected float lasty = 0.0f;
    private boolean scaleEvent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"super.onCreateView shouldn't be called");


        return new View(null);
    }

    public void init(){
        d = D.getInstance();
        mScaleDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());
        if(G.automate) {
            ticker = new Ticker();
            ticker.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public void update(){
        //Log.d(TAG,"update()");
        if(G.bluetooth) {
            while (D.updating()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(views != null) for(View v: views) v.postInvalidate();
        else Log.e(TAG,"views == null");// TODO this is happening
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //view.postInvalidate();
        scaleEvent = false;
        mScaleDetector.onTouchEvent(motionEvent);
        if(motionEvent.getPointerCount() > 1) return true;
        else Log.d(TAG,"no Scale Event");
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN :
                lastx = motionEvent.getX();
                lasty = motionEvent.getY();
                dx = 0.0f;
                dy = 0.0f;
                break;
            case MotionEvent.ACTION_MOVE :
                dx += motionEvent.getX() - lastx;
                dy += motionEvent.getY() - lasty;
                lastx = motionEvent.getX();
                lasty = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP :
                //Log.d(TAG, view.toString());
                //Log.d(TAG, "dx = " + dx);
                //Log.d(TAG, "dy = " + dy);

                if(max(dx,dy) < 1.0f) {
                    Intent intent = new Intent(getActivity(), FocusActivity.class);

                    intent.putExtra("ID", new int[]{view.getId()});
                    intent.putExtra("VIEW", VIEW);
                    //Log.d(TAG, "ID = " + view.getId());
                    startActivity(intent);
                }
                break;


        }
        return true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG,"onReceive()");
            //D.setAllRandom();
            update();
        }
    };

    public void startTicker() {
        if(ticker == null) {
            ticker = new Ticker();
        }
        if(!ticker.isAlive()) ticker.start();
    }

    public void stopTicker() {
        if(ticker == null) ticker = new Ticker();
        ticker.end();
    }


    class Ticker extends Thread {

        private int period = 100;
        private boolean running = true;

        public Ticker() {
            Log.d("Ticker", "Create");
        }

        @Override
        public void run() {
            Log.d("Ticker", "Start");
            while (running) {
                try {
                    //if (Global.automate) {
                    if (G.automate) {
                        D.setAllRandom();
                        update();
                        //sleep(Global.updatePeriod);
                        sleep(G.interval);
                    } else {
                        sleep(500);
                    }
                } catch (Exception e) {
                    Log.e("Ticker", "Catch error");
                    e.printStackTrace();
                }
            }

        }

        public void end(){
            running = false;
        }

    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleEvent = true;
            Log.d(TAG,detector.getCurrentSpanY() + " SpanY");
            Log.d(TAG,detector.getScaleFactor() + " scale Factor");
            D.xScaleFactor(detector.getScaleFactor());
            //mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            //mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            update();
            return true;
        }
    }

}