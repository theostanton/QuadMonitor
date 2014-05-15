package com.theostanton.QuadMonitor.graphs;

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
public class GraphFragment extends BaseFragment {

    private static final String TAG = "Graph Fragment";
    private static final int ROLLMES = 0;
    private static final int ROLLERR = 1;
    private static final int PITCHMES = 2;
    private static final int PITCHERR = 3;
    private static final int YAWMES = 4;
    private static final int YAWERR = 5;
    private String tag = "Graph Tag";
    private Graph graphRoll;
    private Graph graphPitch;
    private Graph graphYaw;

    public GraphFragment(){}

    public void init() {
        super.init();

        VIEW = "GRAPH";

        graphRoll = (Graph) layoutView.findViewById(R.id.graph1);
        graphRoll.set(D.ROLL);
        graphRoll.setOnTouchListener(this);

        graphPitch = (Graph) layoutView.findViewById(R.id.graph2);
        graphPitch.set(D.PITCH);
        graphPitch.setOnTouchListener(this);

        graphYaw = (Graph) layoutView.findViewById(R.id.graph3);
        graphYaw.set(D.YAW);
        graphYaw.setOnTouchListener(this);

        views = new ArrayList<View>(3);

        views.add(graphRoll);
        views.add(graphPitch);
        views.add(graphYaw);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.graph_horizontal, container, false);
        init();

        return layoutView;
    }

}
