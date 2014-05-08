package com.theostanton.QuadMonitor.fragments;

import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.theostanton.QuadMonitor.D;
import com.theostanton.QuadMonitor.R;

import java.util.ArrayList;

/**
 * Created by theo on 08/05/2014.
 */
public class ConsoleFragment extends BaseFragment {

    private static final String TAG = "Console Fragment";

    private static Scroller scroller;

    private TextView consoleTextView;

    public void init() {
        super.init();
        VIEW = "CONSOLE";

        consoleTextView = (TextView) layoutView.findViewById(R.id.consoleTextView);

        consoleTextView.setMovementMethod(new ScrollingMovementMethod());
        consoleTextView.setText("No data!");
        scroller = new Scroller(0);

        if (D.freshConsole) {
            consoleTextView.setText(D.getConsole());

            final Layout layout = consoleTextView.getLayout();
            if (layout != null) {
                int scrollDelta = layout.getLineBottom(consoleTextView.getLineCount() - 1)
                        - consoleTextView.getScrollY() - consoleTextView.getHeight();
                if (scrollDelta > 0)
                    consoleTextView.scrollBy(0, scrollDelta);
            }
        }

        views = new ArrayList<View>();
        views.add(consoleTextView);
    }

    @Override
    public void update() {
        super.update();


        if (D.freshConsole) {
            consoleTextView.setText(D.getConsole());

            final Layout layout = consoleTextView.getLayout();
            if (layout != null) {
                int scrollDelta = layout.getLineBottom(consoleTextView.getLineCount() - 1)
                        - consoleTextView.getScrollY() - consoleTextView.getHeight();
                if (scrollDelta > 0)
                    if (scroller.isAlive()) scroller.addDelta(scrollDelta);
                    else {
                        scroller = new Scroller(scrollDelta);
                        scroller.start();
                    }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.console_view, container, false);
        init();

        return layoutView;
    }

    class Scroller extends Thread {

        private float scrollDelta = 0.0f;

        public Scroller(int delta) {
            scrollDelta = (float) delta;
        }

        @Override
        public void run() {
            while (scrollDelta > 0.0f) {
                float scrollBy = scrollDelta / 20.0f + 1.0f;
                consoleTextView.scrollBy(0, (int) scrollBy);
                scrollDelta -= (float) (int) scrollBy;
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void addDelta(int delta) {
            scrollDelta = (float) delta;
        }
    }

}
