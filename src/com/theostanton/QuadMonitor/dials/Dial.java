package com.theostanton.QuadMonitor.dials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.theostanton.QuadMonitor.Component;
import com.theostanton.QuadMonitor.D;

/**
 * Created by theo on 23/04/2014.
 */
public class Dial extends Component{

//    private static final int ROLLMES = 0;
//    private static final int ROLLERR = 1;
//    private static final int PITCHMES = 2;
//    private static final int PITCHERR = 3;
//    private static final int YAWMES = 4;
//    private static final int YAWERR = 5;

    private D d;
    private int[] id;
    private int ids = -1;

    private float radius;
    private String title2;

    public Dial(Context context, AttributeSet attrs) {
        super(context, attrs);
        d = D.getInstance();
        id = new int[8];
        title = "Dial";
    }

    public Dial(Context context, AttributeSet attrs, boolean f) {
        super(context, attrs);
        focused = true;
        id = new int[8];
        title = "Dial";
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawOval(sqBounds, frameP);
        c.drawLine(sqBounds.left,ctrY,sqBounds.right,ctrY,frameP);


        int idDec = ids;
        switch(idDec){
            case 1:
                drawLine(Math.toRadians(D.getVal(id[idDec--])), Color.BLUE, c);
            case 0:
                drawLine(Math.toRadians(D.getVal(id[idDec])), Color.RED, c);
                break;
            default:
                Log.e("Dial", "No ids set");
        }


        c.drawText(title, ctrX, top + textY, textP);
        //c.drawText(D.getStringVal(id), ctrX, textY, textP);
    }

    private void drawLine(double ang, int color, Canvas c){
        p.setColor(color);
        c.drawLine(ctrX, ctrY,
                ctrX + radius * (float) Math.cos(ang),
                ctrY + radius * (float) Math.sin(ang), p);
        c.drawLine(ctrX,ctrY,
                ctrX-radius*(float)Math.cos(ang),
                ctrY-radius*(float)Math.sin(ang),p);
    }

    public void addId(int i){
        if(i == -1) Log.e(title,"ID == -1");
        id[++ids] = i;
        title = d.getName(i);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        radius = diameter / 2.0f;

        float xx = w/2.0f - diameter/2.0f;
        float yy = h/2.0f - diameter/2.0f;


        sqBounds = new RectF(0.0f, 0.0f, diameter, diameter);
        sqBounds.offsetTo(getPaddingLeft(), getPaddingTop());
        sqBounds.offsetTo(xx, yy);
    }
}
