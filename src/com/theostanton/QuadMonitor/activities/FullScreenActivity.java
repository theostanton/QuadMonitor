package com.theostanton.QuadMonitor.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import com.theostanton.QuadMonitor.R;
import com.theostanton.QuadMonitor.fragments.BaseFragment;

/**
 * Created by theo on 08/05/2014.
 */
public class FullScreenActivity extends Activity {

    private static final String TAG = "FullScreenActivity";
    private static final int CONTENT_VIEW_ID = 10101010;

    private BaseFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_full);
        getActionBar().hide();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            finish();
        }
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getActionBar().hide();
//        Intent intent = getIntent();
//        String tab = intent.getStringExtra("POSITION");
//
//        // need to assign IDs to views
//
//        if(tab.equals("Graphs")) setContentView(R.layout.graph_horizontal);
//        else if(tab.equals("Dials")) setContentView(R.layout.dial_grid);
//        else if(tab.equals("PID")) setContentView(R.layout.pid_grid);
//        else if(tab.equals("Raw")) setContentView(R.layout.raw_view);
//        else if(tab.equals("Coeffs")) setContentView(R.layout.coeffs);
//        else Log.e(TAG, "Position error : " + tab);
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FrameLayout frame = new FrameLayout(this);
//        frame.setId(CONTENT_VIEW_ID);
//        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        if (savedInstanceState == null) {
//            Fragment newFragment = new PidFragment();
//            FragmentManager ft = getFragmentManager().beginTransaction();
//            ft.add(CONTENT_VIEW_ID,  newFragment).commit();
//        }
//    }

}
