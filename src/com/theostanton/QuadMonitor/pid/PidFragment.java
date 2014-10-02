package com.theostanton.QuadMonitor.pid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.theostanton.QuadMonitor.statics.D;
import com.theostanton.QuadMonitor.R;
import com.theostanton.QuadMonitor.fragments.BaseFragment;

import java.util.ArrayList;

/**
 * Created by theo on 23/04/2014.
 */
public class PidFragment extends BaseFragment implements View.OnTouchListener{

    private static final String TAG = "PID Fragment";


    private Motor motorA;
    private Motor motorB;
    private Motor motorC;
    private Motor motorD;

    public PidFragment(){

    }

    //@Override
    //public void update(){
    //    Log.d(TAG, "update()");
    //    motorA.postInvalidate();
    //    motorB.postInvalidate();
    //    motorC.postInvalidate();
    //    motorD.postInvalidate();
    //}

    public void init(){
        super.init();
        VIEW = "MOTOR";

        motorA = (Motor) layoutView.findViewById(R.id.motor1);
        motorA.set(D.pA);
        motorA.setTitle("Motor A");
        motorA.setOnTouchListener(this);

        motorB = (Motor) layoutView.findViewById(R.id.motor2);
        motorB.set(D.pB);
        motorB.setTitle("Motor B");
        motorB.setOnTouchListener(this);

        motorC = (Motor) layoutView.findViewById(R.id.motor3);
        motorC.set(D.pC);
        motorC.setTitle("Motor C");
        motorC.setOnTouchListener(this);

        motorD = (Motor) layoutView.findViewById(R.id.motor4);
        motorD.set(D.pD);
        motorD.setTitle("Motor D");
        motorD.setOnTouchListener(this);

        views = new ArrayList<View>(4);
        views.add(motorA);
        views.add(motorB);
        views.add(motorC);
        views.add(motorD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.pid_grid, container, false);
        init();

        return layoutView;
    }



}
