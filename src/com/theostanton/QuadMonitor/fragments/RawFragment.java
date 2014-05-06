package com.theostanton.QuadMonitor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.theostanton.QuadMonitor.D;
import com.theostanton.QuadMonitor.R;

import java.util.ArrayList;

/**
 * Created by theo on 02/05/2014.
 */
public class RawFragment extends BaseFragment {

    private static final String TAG = "Raw Fragment";

    private TextView rawTextViewValue;
    private TextView rawTextViewLabel;

    public RawFragment(){}

    public void init(){
        super.init();
        VIEW = "RAW";

        rawTextViewValue = (TextView) layoutView.findViewById(R.id.rawTextViewValue);
        rawTextViewLabel = (TextView) layoutView.findViewById(R.id.rawTexViewtLabel);

        rawTextViewLabel.setText("No D");
        rawTextViewValue.setText("ata!");

        views = new ArrayList<View>();
        views.add(rawTextViewLabel);
        views.add(rawTextViewValue);
    }

    @Override
    public void update() {
        rawTextViewLabel.setText(D.getRawLabels());
        rawTextViewValue.setText(D.getRawValues());
        super.update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.raw_view, container, false);
        init();

        return layoutView;
    }

}
