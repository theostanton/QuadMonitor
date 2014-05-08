package com.theostanton.QuadMonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by theo on 02/05/2014.
 */
public class Coeff extends Component{

    // Copid from first attempt

    private static final String TAG = "Coeff";
    // public static final int P = 1;
    // public static final int I = 2;
    // public static final int D = 3;

    private static Paint titleP;
    private static Intent intent;
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
        titleP = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleP.setTextAlign(Paint.Align.CENTER);
        titleP.setColor(Color.BLACK);

        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.LTGRAY);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawRect(rect, p);
        c.drawText(tit, getWidth() / 2, yText, titleP);
        c.drawText(String.valueOf(val), getWidth() / 2, yText * 3 / 4 + val + getWidth(), p);
    }

    @Override
    public void set(int i) {
        id = i;
        switch(i){
            case BluetoothService.KPid:
                tit = "P";
                val = com.theostanton.QuadMonitor.D.getpVal();
                break;
            case BluetoothService.KIid:
                tit = "I";
                val = com.theostanton.QuadMonitor.D.getiVal();
                break;
            case BluetoothService.KDid:
                tit = "D";
                val = com.theostanton.QuadMonitor.D.getdVal();
                break;
            default:
                Log.e(TAG, "ID error " + i);
        }

    }

    public void drag(float dy) {
        val = Math.min(999, (int) (val + dy));
        fVal = (float) val / (float) h;
        if (val > 0) {
            rect = new Rect(0, 0, getWidth(), val + getWidth());
            //Log.d(TAG,rect.toString());
        } else {
            rect = new Rect(0, 0, getWidth(), getWidth());
            val = 0;
        }

        // BluetoothService.sendMessage("message to send");

    }

    public void release(){
        intent = new Intent(BluetoothService.BTSENDMESSAGE);
        intent.putExtra("id", id);
        intent.putExtra("Value", val);
        getContext().sendBroadcast(intent);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        x = 0;
        y = 0;
        w = getWidth();
        h = getHeight();
        Rect bounds = new Rect();
        int margins = 75;


        titleP.setTextSize((w - 2 * margins) * 3 / 2);
        p.setTextSize(150.0f); // TODO Propotional

        yText = w - margins; // TODO Propotional
        rect = new Rect(0, 0, 0 + w, 0 + val + w);
    }
}
