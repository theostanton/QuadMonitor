package com.theostanton.QuadMonitor;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.theostanton.QuadMonitor.dials.DialFragment;
import com.theostanton.QuadMonitor.fragments.BaseFragment;
import com.theostanton.QuadMonitor.fragments.RawFragment;
import com.theostanton.QuadMonitor.graphs.GraphFragment;
import com.theostanton.QuadMonitor.pid.PidFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{
    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "MainActivity";
    private D d;


    private static final String[] tabLabels = {"Graphs", "Dials", "PID","Raw","Coeffs"};

    private PagerAdapter pagerAdapter;

    private Intent intent;

    private ViewPager mViewPager;
    private BaseFragment currFragment;
    private Context mContext;
    //private Ticker ticker;
    private int position = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mContext = getBaseContext();
        d = D.getInstance();
        setContentView(R.layout.main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Specify that we will be displaying tabs in the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < tabLabels.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tabLabels[i])
                            .setTabListener(this)
            );
        }

        intent = new Intent(this,BluetoothService.class);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("MyActivity","Gonna Create Ticker");
        //ticker = new Ticker();
        //ticker.start();
    }

    public void update(){

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.bluetooth_action:
                if(item.isChecked()){
                    stopService(intent);
                    item.setChecked(false);
                    Log.d(TAG,"Disconnect Bluteooth");
                }
                else {

                    startService(intent);
                    item.setChecked(true);
                    Log.d(TAG,"Connect Bluteooth");
                }
                return true;
            case R.id.automate_action:
                if(item.isChecked()){
                    G.automate = false;
                    item.setChecked(false);
                    currFragment.stopTicker();
                }
                else {
                    G.automate = true;
                    item.setChecked(true);
                    currFragment.startTicker();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            position = i;
            Log.d(TAG,position + "");
            switch(i){
                case 0:
                    currFragment = new GraphFragment();
                    break;
                case 1:
                    currFragment = new DialFragment();
                    break;
                case 2:
                    currFragment = new PidFragment();
                    break;
                case 3:
                    currFragment = new RawFragment();
                    break;
                case 4:
                    currFragment = new CoeffFragment();
                    break;
                default:
                    currFragment = new DialFragment();
            }

            // GridView gridview = (GridView) findViewById(R.id.gridView);
            // gridview.setAdapter(new DummyGridAdapter(null));

            return currFragment;
        }

        @Override
        public int getCount() {
            return tabLabels.length;
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }




}
