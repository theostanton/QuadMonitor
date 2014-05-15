package com.theostanton.QuadMonitor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by theo on 07/05/2014.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "settings";

    SharedPreferences settings;
    // SharedPreferences.Editor editor = settings.edit();

    public static void refreshSummaries() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        settings = getPreferenceScreen().getSharedPreferences();
        settings.registerOnSharedPreferenceChangeListener(this);


        EditTextPreference pref = (EditTextPreference) findPreference("p_coeff");
        pref.setSummary(pref.getText());

        pref = (EditTextPreference) findPreference("i_coeff");
        pref.setSummary(pref.getText());

        pref = (EditTextPreference) findPreference("d_coeff");
        pref.setSummary(pref.getText());

        pref = (EditTextPreference) findPreference("controlratePref");
        pref.setSummary(pref.getText());

        pref = (EditTextPreference) findPreference("sampleratePref");
        pref.setSummary(pref.getText());


        pref = (EditTextPreference) findPreference("max_integral");
        pref.setSummary(pref.getText());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        //item.setIcon()
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        //updateRate();
    }

//    public void updateRate() {
//        EditTextPreference myPrefText = (EditTextPreference) findPreference("sampleratePref");
//        int rate = Integer.valueOf(myPrefText.getText());
//        Log.d(TAG, "rate = " + rate);
//        BluetoothService.sendValue(BluetoothService.RATEid, rate);
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (findPreference(key) instanceof EditTextPreference) {
            EditTextPreference pref = (EditTextPreference) findPreference(key);
            Log.d(TAG, "Preference change " + pref.toString());
            String str = pref.getText();
            Log.d(TAG, "Preference text :" + str);

            pref.setSummary(str);
        }
    }
}
