package com.theostanton.QuadMonitor;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.theostanton.QuadMonitor.dials.DialFragment;
import com.theostanton.QuadMonitor.fragments.BaseFragment;
import com.theostanton.QuadMonitor.fragments.ConsoleFragment;
import com.theostanton.QuadMonitor.fragments.RawFragment;
import com.theostanton.QuadMonitor.graphs.GraphFragment;
import com.theostanton.QuadMonitor.pid.PidFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{
    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "MainActivity";
    private static final String[] tabLabels = {"Graphs", "Dials", "PID", "Raw", "Coeffs", "Console"};
    private D d;
    private PagerAdapter pagerAdapter;

    private Intent intent;

    private ViewPager mViewPager;
    private BaseFragment currFragment;
    private Context mContext;
    //private Ticker ticker;
    private int position = 0;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(this, FullScreenActivity.class);
            startActivity(intent);
        }
    }

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int kp = Integer.valueOf(sharedPreferences.getString("p_coeff", "-1"));
        int ki = Integer.valueOf(sharedPreferences.getString("i_coeff", "-1"));
        int kd = Integer.valueOf(sharedPreferences.getString("d_coeff", "-1"));

        Log.d(TAG, "coeffs : " + kp + " " + ki + " " + kd);

        D.setCoeffsFromDefaultPreference(kp, ki, kd);

        showControls(D.showControls);
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

    private void showControls(boolean show) {
        if (show) {
            LinearLayout remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLgraph);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.VISIBLE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLdial);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.VISIBLE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLpid);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.VISIBLE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLraw);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.VISIBLE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLconsole);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            LinearLayout remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLgraph);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.GONE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLdial);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.GONE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLpid);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.GONE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLraw);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.GONE);
            remoteLayout = (LinearLayout) this.findViewById(R.id.remoteControlLLconsole);
            if (remoteLayout != null) remoteLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.remote_control_action:
                showControls(!item.isChecked());
                D.showControls = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.fullscreen_action:
                Intent fullscreenIntent = new Intent(this, FullScreenActivity.class);
                Log.d(TAG, "Position : " + position);
                fullscreenIntent.putExtra("POSITION", tabLabels[position]);
                startActivity(fullscreenIntent);
                break;
            case R.id.settings_action:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.update_coeff_action :
                //int val = BluetoothService.getValue(BluetoothService.KPid);
                BluetoothService.getCoeffs();
                break;
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
        return true;
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
            Log.d(TAG, position + "");
            switch (i) {
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
                case 5:
                    currFragment = new ConsoleFragment();
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




}
