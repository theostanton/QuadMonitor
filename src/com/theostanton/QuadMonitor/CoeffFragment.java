package com.theostanton.QuadMonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.theostanton.QuadMonitor.fragments.BaseFragment;

import java.util.ArrayList;

/**
 * Created by theo on 02/05/2014.
 */
public class CoeffFragment extends BaseFragment implements View.OnTouchListener {

    private Coeff pCoeff;
    private Coeff iCoeff;
    private Coeff dCoeff;

    public void init() {
        super.init();
        VIEW = "COEFFS";

        pCoeff = (Coeff) layoutView.findViewById(R.id.coeffViewP);
        pCoeff.set(BluetoothService.KPid);
        pCoeff.setOnTouchListener(this);

        iCoeff = (Coeff) layoutView.findViewById(R.id.coeffViewI);
        iCoeff.set(BluetoothService.KIid);
        iCoeff.setOnTouchListener(this);

        dCoeff = (Coeff) layoutView.findViewById(R.id.coeffViewD);
        dCoeff.set(BluetoothService.KDid);
        dCoeff.setOnTouchListener(this);


        views = new ArrayList<View>(3);
        views.add(pCoeff);
        views.add(iCoeff);
        views.add(dCoeff);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.coeffs, container, false);
        init();
        return layoutView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Coeff coeffView = (Coeff) view;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lasty = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                coeffView.drag(motionEvent.getY() - lasty);
                lasty = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                coeffView.release();
                break;


        }
        view.postInvalidate();
        return true;
    }
}
