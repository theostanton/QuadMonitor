package com.theostanton.QuadMonitor.dials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.theostanton.QuadMonitor.Component;
import com.theostanton.QuadMonitor.statics.D;
import com.theostanton.QuadMonitor.statics.G;

public class Dial extends Component {

    protected final String TAG = "Dial";

    private D d;
    //protected int[] IDs;
    private int[] lineColor;
    private int ids = -1;

    private float radius;
    private String title2;

    public Dial(Context context, AttributeSet attrs) {
        super(context, attrs);
        d = D.getInstance();
        title = "Dial";
        lineColor = new int[3];
        lineColor[0] = G.ERRCOLOUR;
        lineColor[1] = G.MESCOLOUR;
        lineColor[2] = G.DESCOLOUR;
    }

    public Dial(Context context, AttributeSet attrs, boolean f) {
        super(context, attrs);
        focused = true;
        title = "Dial";
        lineColor = new int[3];
        lineColor[0] = G.ERRCOLOUR;
        lineColor[1] = G.MESCOLOUR;
        lineColor[2] = G.DESCOLOUR;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        if (IDs != null) {
            for (int i = 0; i < IDs.length; i++) {
                drawLine(Math.toRadians(D.getVal(IDs[i])), lineColor[i], c);
            }
        }

        c.drawText(title, ctrX, ctrY - textP.getTextSize(), textP);

        c.drawOval(sqBounds, frameP);
        c.drawLine(sqBounds.left, ctrY, sqBounds.right, ctrY, frameP);

        for (int a = 0; a < 360; a += 20) drawNib(Math.toRadians(a), c);
    }

    public void set(int[] i) {
        Log.d(TAG, i.length + " ids being set");
        for (int ii : i) {
            Log.d(TAG, "IDs : " + ii + " set");
        }
        IDs = i;

        switch (IDs[0]) {
            case D.ERRROLL:
                title = "Roll Commands";
                break;
            case D.GYROROLL:
                title = "Roll Sensors";
                break;
            case D.ERRPITCH:
                title = "Pitch Commands";
                break;
            case D.GYROPITCH:
                title = "Pitch Sensors";
                break;
        }
    }

    private void drawLine(double ang, int color, Canvas c) {
        p.setColor(color);
        p.setStrokeWidth(2.0f);
        c.drawLine(ctrX, ctrY,
                ctrX - radius * (float) Math.cos(ang),
                ctrY - radius * (float) Math.sin(ang), p);
        c.drawLine(ctrX, ctrY,
                ctrX + radius * (float) Math.cos(ang),
                ctrY + radius * (float) Math.sin(ang), p);
    }

    private void drawNib(double ang, Canvas c) {
        c.drawLine(
                ctrX - radius * (float) Math.cos(ang),
                ctrY - radius * (float) Math.sin(ang),
                ctrX - radius * 0.98f * (float) Math.cos(ang),
                ctrY - radius * 0.98f * (float) Math.sin(ang),
                frameP);
    }

//    public void addId(int i){
//        if(i == -1) Log.e(title,"ID == -1");
//        IDs[++ids] = i;
//        title = D.getName(i);
//        invalidate();
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = diameter / 2.0f;

        float xx = w / 2.0f - diameter / 2.0f;
        float yy = h / 2.0f - diameter / 2.0f;


        sqBounds = new RectF(0.0f, 0.0f, diameter, diameter);
        sqBounds.offsetTo(getPaddingLeft(), getPaddingTop());
        sqBounds.offsetTo(xx, yy);
    }
}
