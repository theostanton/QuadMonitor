package com.theostanton.QuadMonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by theo on 02/05/2014.
 */
public class Coeff extends Component{

    // Copid from first attempt

    private static final String TAG = "Coeff";
    private static final int P = 0;
    private static final int I = 1;
    private static final int D = 2;

    private int x;
    private int y;
    private int w;
    private int h;
    private Rect rect;

    private String tit = "P";
    private int xText;
    private int yText;
    private int size = 150;

    private int val = 0;
    private float fVal = 0.5f;


    public Coeff(Context context, AttributeSet attrs) {
        super(context, attrs);
        title = "Coeff";
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        p.setColor(Color.LTGRAY);
        c.drawRect(rect, p);
        p.setColor(Color.BLACK);
        p.setTextSize(size);
        c.drawText(tit, xText, yText, p);
        p.setColor(Color.LTGRAY);
        c.drawText(String.valueOf(val), x + 20, yText + val + getWidth(), p);
    }

    @Override
    public void set(int[] i) {
        super.set(i);
        switch(i[0]){
            case P:
                tit = "P";
                val = com.theostanton.QuadMonitor.D.getpVal();
                break;
            case I:
                tit = "I";
                val = com.theostanton.QuadMonitor.D.getiVal();
                break;
            case D:
                tit = "D";
                val = com.theostanton.QuadMonitor.D.getdVal();
                break;
            default:
                Log.e(TAG, "ID error " + i);
        }

    }

    public void drag(float dy) {
        val = Math.min(1000,(int)( val + dy ));
        fVal = (float) val / (float) h;
        if (val > 0) {
            rect = new Rect(0, 0, getWidth(), val + getWidth());
            Log.d(TAG,rect.toString());
        } else {
            rect = new Rect(0, 0, getWidth(), getWidth());
            val = 0;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        x = 0;
        y = 0;
        w = getWidth();
        h = getHeight();
        textP.setTextSize(150.0f); // TODO Propotional
        Rect bounds = new Rect();
        textP.getTextBounds(tit, 0, tit.length(), bounds);
        xText = w / 2 - bounds.width() / 2;
        yText = 200; // TODO Propotional
        rect = new Rect(0, 0, 0 + w, 0 + val + w);
    }
}
