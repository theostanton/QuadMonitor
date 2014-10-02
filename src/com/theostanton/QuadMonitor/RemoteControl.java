package com.theostanton.QuadMonitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.theostanton.QuadMonitor.statics.D;
import com.theostanton.QuadMonitor.statics.G;

/**
 * Created by theo on 08/05/2014.
 */
public class RemoteControl extends View implements View.OnTouchListener {


    protected static final String DNS = "http://theostanton.com";
    protected static final String TAG = "RemoteControl";

    protected Paint fillPaint;
    protected Paint linePaint;
    protected Paint joyPaint;
    protected int diameter;
    protected float joyDiameter;
    protected float joyRadius;
    protected RectF centerRect;
    protected RectF sqBounds;
    protected RectF joyStick;
    protected RectF joyStickNeck;
    protected int xID = -1;
    protected int yID = -1;
    protected boolean ySticky = true;
    protected boolean xSticky = false;
    protected Point point;
    protected Point ctrPoint;
    protected int xVal = 0;
    protected int yVal = 0;
    private int currentPointerId = -1;
    private int secondaryPointerId = -1;

    public RemoteControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        xID = sortId(attrs.getAttributeValue(DNS, "xAxisID"));
        yID = sortId(attrs.getAttributeValue(DNS, "yAxisID"));
        xSticky = attrs.getAttributeBooleanValue(DNS, "xSticky", true);
        ySticky = attrs.getAttributeBooleanValue(DNS, "ySticky", true);
    }

    protected void init() {


        setOnTouchListener(this);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.GRAY);
        fillPaint.setAlpha(100);

        joyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        joyPaint.setStyle(Paint.Style.FILL);
        joyPaint.setColor(Color.BLACK);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);

        xVal = 0;
        yVal = 0;
        point = new Point((int) sqBounds.centerX(), (int) sqBounds.centerX());
        ctrPoint = new Point((int) sqBounds.centerX(), (int) sqBounds.centerX());

        joyDiameter = diameter / 10.0f;
        Log.d(TAG, "joyDiameter = " + joyDiameter);
        Log.d(TAG, "diameter = " + diameter);
        joyRadius = joyDiameter / 2.0f;

        centerRect = new RectF(sqBounds.centerX() - joyRadius,
                sqBounds.centerY() - joyRadius,
                sqBounds.centerX() + joyRadius,
                sqBounds.centerY() + joyRadius);
        linePaint.setStrokeWidth(joyDiameter);
    }

    protected void updateVals(int xx, int yy) {

        // Constrain xx
        if (xx > sqBounds.right) xx = (int) sqBounds.right - (int) joyRadius;
        else if (xx < sqBounds.left) xx = (int) sqBounds.left + (int) joyRadius;

        // constrain yy
        if (yy > sqBounds.bottom) yy = (int) sqBounds.bottom - (int) joyRadius;
        else if (yy < sqBounds.top) yy = (int) sqBounds.top + (int) joyRadius;


        point.x = xx;
        point.y = yy;

        joyStick = new RectF(xx - joyRadius, yy - joyRadius, xx + joyRadius, yy + joyRadius);

        xx -= sqBounds.left;
        yy -= sqBounds.top;

        xVal = 90 * xx / (diameter + 1) - 45;
        yVal = 90 * yy / (diameter + 1) - 45;

    }

    private void sendVals(boolean released) {

        if (released) {
            if (ySticky) yVal = 0;
            if (xSticky) xVal = 0;
        }
        Intent intent = new Intent(BluetoothService.BTSENDMESSAGE);
        intent.putExtra("id", xID);
        intent.putExtra("Value", xVal);
        getContext().sendBroadcast(intent);
        if( G.automate) D.setControl(xID, xVal);

        intent = new Intent(BluetoothService.BTSENDMESSAGE);
        intent.putExtra("id", yID);
        intent.putExtra("Value", yVal);
        getContext().sendBroadcast(intent);
        if( G.automate) D.setControl(yID, yVal);

//        if (!G.automate) {
//            D.updateLists();
//            getContext().sendBroadcast(new Intent(BluetoothService.BROADCAST_ACTION));
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;


        diameter = (int) Math.min(ww, hh);

        float xx = ww / 2 - diameter / 2;
        float yy = hh / 2 - diameter / 2;

        sqBounds = new RectF(0.0f, 0.0f, diameter, diameter);
        sqBounds.offsetTo(getPaddingLeft(), getPaddingTop());
        sqBounds.offsetTo(xx, yy);

        Log.d(TAG, "sqBounds = " + sqBounds);

        init();

        updateVals((int) sqBounds.centerX(), (int) sqBounds.centerY());
    }

    protected int sortId(String str) {
        if (str.equals("ROLL")) return D.RXROLL;
        else if (str.equals("PITCH")) return D.RXPITCH;
        else if (str.equals("YAW")) return D.RXYAW;
        else if (str.equals("THROTTLE")) return D.RXTHROTTLE;
        else return -1;
    }

    @Override
    protected void onDraw(Canvas c) {
        c.drawOval(sqBounds, fillPaint); // main outline
        c.drawOval(joyStick, joyPaint); // tip circle
        c.drawOval(centerRect, joyPaint); // center circle
        c.drawLine(sqBounds.centerX(), sqBounds.centerY(), point.x, point.y, linePaint); // stick line
    }

    private void releaseTouch(int xx, int yy) {
        if (ySticky) yy = (int) sqBounds.centerY();
        if (xSticky) xx = (int) sqBounds.centerX();
        updateVals(xx, yy);
        sendVals(true);
    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateVals((int) ev.getX(), (int) ev.getY());
                sendVals(false);
                break;
            case MotionEvent.ACTION_UP:
                releaseTouch((int) ev.getX(), (int) ev.getY());
                break;
        }

        invalidate();
        return true;
    }


}
