/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Slightly modified by Alex Burka
 *   - support dependent preference widgets
 *   - correctly track the seekbar value internally
 */
package com.theostanton.QuadMonitor;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String androidns = "http://schemas.android.com/apk/res/android";
    private static final String TAG = "SeekBarPreference";

    private SeekBar mSeekBar;
    private TextView mSplashText, mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private int mDefault, mMax, mValue = 0;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
        if (mDialogMessage != null && mDialogMessage.length() > 1 && mDialogMessage.charAt(0) == '@') {
            int id = Integer.parseInt(mDialogMessage.substring(1));
            mDialogMessage = context.getString(id);
        }
        mSuffix = attrs.getAttributeValue(androidns, "text");
        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 100);

    }

    @Override
    protected View onCreateDialogView() {
        setPersistent(false);
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(10, 10, 10, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);


        return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (shouldPersist()) {
                mValue = mSeekBar.getProgress();
                persistInt(mValue);
            }
            notifyDependencyChange(shouldDisableDependents());
        }
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            //mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
            mValue = 0;
        else
            mValue = (Integer) defaultValue;
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        String t = String.valueOf(value);
        //mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
        mValueText.setText(t);
        if (shouldPersist())
            persistInt(value);
        callChangeListener(Integer.valueOf(value));
    }

    public void onStartTrackingTouch(SeekBar seek) {
    }

    public void onStopTrackingTouch(SeekBar seek) {
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getProgress() {
        return mValue;
    }

    public void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null)
            mSeekBar.setProgress(progress);
    }

    @Override
    public boolean shouldDisableDependents() {
        Log.d("SKP", "SDD called, mValue = " + Integer.toString(mValue) + ", super = " + Boolean.toString(super.shouldDisableDependents()));
        return mValue == 0 || super.shouldDisableDependents();
    }
}