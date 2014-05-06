package com.theostanton.QuadMonitor.dials;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.theostanton.QuadMonitor.D;
import com.theostanton.QuadMonitor.R;
import com.theostanton.QuadMonitor.fragments.BaseFragment;

import java.util.ArrayList;

/**
 * Created by theo on 23/04/2014.
 */
public class DialFragment extends BaseFragment implements View.OnTouchListener{

    private static final String TAG = "Dial Fragment";

    private Dial rollMesDial;
    private Dial rollErrDial;
    private Dial pitchMesDial;
    private Dial pitchErrDial;

    private D d;

    public DialFragment(){
       d = D.getInstance();
    }

    public DialFragment(int id){
    }


    public void init(){
        super.init();

        VIEW = "DIAL";

        rollErrDial = (Dial) layoutView.findViewById(R.id.dial1);
        rollErrDial.set(new int[]{D.ERRROLL,D.MESROLL});
        rollErrDial.setTitle("Roll Commands");
        rollErrDial.setOnTouchListener(this);

        rollMesDial = (Dial) layoutView.findViewById(R.id.dial2);
        rollMesDial.set(new int[]{D.GYROROLL,D.ACCROLL});
        rollMesDial.setTitle("Roll Sensors");
        rollMesDial.setOnTouchListener(this);

        pitchErrDial = (Dial) layoutView.findViewById(R.id.dial3);
        pitchErrDial.set(new int[]{D.ERRPITCH,D.MESPITCH});
        pitchErrDial.setTitle("Pitch Commands");
        pitchErrDial.setOnTouchListener(this);

        pitchMesDial = (Dial) layoutView.findViewById(R.id.dial4);
        pitchMesDial.set(new int[]{D.GYROPITCH,D.ACCPITCH});
        pitchMesDial.setTitle("Pitch Sensors");
        pitchMesDial.setOnTouchListener(this);

        views = new ArrayList<View>(4);
        views.add(rollErrDial);
        views.add(rollMesDial);

        views.add(pitchErrDial);
        views.add(pitchMesDial);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.dial_grid, container, false);
        init();

        return layoutView;
    }


}
