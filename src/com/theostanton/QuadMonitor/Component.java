package com.theostanton.QuadMonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by theo on 22/04/2014.
 */
public class Component extends View implements View.OnTouchListener{

    protected Paint textP = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint frameP = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected int bgColor = Color.RED;
    protected String title = "Component";
    protected final String TAG = "Component";
    protected Random r = new Random();

    protected RectF sqBounds;
    protected float top;
    protected float bot;
    protected float left;
    protected float right;
    protected float diameter;
    protected float height;
    protected float ctrX;
    protected float ctrY;
    protected float textY;

    public int id;

    public int acc = 0;

    protected long fps = 0L;
    protected long lastfFs = 0L;
    protected long lastMs = 0L;
    protected boolean focused = false;
    protected boolean scalable = true;
    private int[] i;


    public Component(Context context) {
        super(context);
        init();
    }

    public Component(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public Component(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {

        setOnTouchListener(this);
        textP.setTextAlign(Paint.Align.CENTER);
        textP.setTextSize(60.0f);
        textP.setStyle(Paint.Style.FILL);
        textP.setColor(Color.WHITE);
        textP.setAlpha(50);

        frameP.setColor(Color.WHITE);
        frameP.setStyle(Paint.Style.STROKE);
        frameP.setStrokeWidth(2.0f);

    }

    public int getId(){
        return id;
    }


    public void set(int i){
        Log.e("Component", "set(int i) should set in subclass");
        id = i;
    }

    public void set(int[] i){
        if(i.length == 1) set(i[0]);
        else Log.e("Component", "set(int[] i) should set in subclass");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // todo tidy and reconsider variables

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        ctrX = getPaddingLeft() + ww/2;
        ctrY = getPaddingTop() + hh/2;
        top = getPaddingTop();
        bot = hh;
        left = getPaddingLeft();
        right = ww;

        // Figure out how big we can make the pie.
        diameter = Math.min(ww, hh);
        height = hh - ypad;

        float xx = ww/2 - diameter/2;
        float yy = hh/2 - diameter/2;

        textY = yy/2 + getPaddingTop() + p.getTextSize()/2.0f;

        if(focused) {
            sqBounds = new RectF(0.0f, 0.0f, getWidth(), getHeight());
            //sqBounds.offsetTo(getPaddingLeft(), getPaddingTop());
            //sqBounds.offsetTo(xx, yy);
        }
        else {
            sqBounds = new RectF(0.0f, 0.0f, diameter, diameter);
            sqBounds.offsetTo(getPaddingLeft(), getPaddingTop());
            sqBounds.offsetTo(xx, yy);
        }

    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        //setBackgroundColor(Color.DKGRAY);
//
        //c.drawOval(sqBounds, frameP);
//
        fps += 1000L / ( System.currentTimeMillis() - lastMs );
        fps /= 2L;
        lastMs = System.currentTimeMillis();
        // c.drawText(D.getStringVal(id),ctrX,ctrY + textY,textP);

    }


    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG,"onTouch()");
        return true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    //    for fixed Aspect ratio
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int newWidth = getMeasuredWidth() ;
//        int newHeight = getMeasuredWidth();
//
//        Log.d("Component","newHeight  = " + newHeight);
//        Log.d("Component","newWidth  = " + newWidth);
//
//        setMeasuredDimension(newWidth, newHeight);
//    }


}
