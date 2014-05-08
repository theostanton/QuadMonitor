package com.theostanton.QuadMonitor.pid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import com.theostanton.QuadMonitor.Component;
import com.theostanton.QuadMonitor.D;

import java.util.Random;

/**
 * Created by theo on 22/04/2014.
 */
public class Motor extends Component {

    private static final String TAG = "Motor";
    private static final String[] labels = {"P", "I", "D", "T"};
    private Random r = new Random(); // TODO: temp

//    private static final int A = 0;
//    private static final int B = 1;
//    private static final int C = 2;
//    private static final int D = 3;
private Paint pBar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] barX = new float[4];
    private float[] barTextX = new float[4];
    private float   barTextY;
    private float   barW;
    private float   yScale;
    private float   maxVal = 30.0f;
    private float   maxY;
    private int dID = -1;

    public Motor(Context context) {
        super(context);
        title = "Motor";
    }

    public Motor(Context context, AttributeSet attrs, boolean f) {
        super(context, attrs);
        focused = true;
        title = "Dial";
    }

    public Motor(Context context, AttributeSet attrs) {
        super(context,attrs);
        title = "Motor";
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        if(!focused) c.drawRect(sqBounds, frameP);
        c.drawLine(sqBounds.left,ctrY,sqBounds.right,ctrY,frameP);

        c.drawText(title, ctrX, getBottom() - textP.getTextSize() * 2.0f, textP);
        //if(focused) c.drawText(title, ctrX, getBottom() - textP.getTextSize()*2.0f, textP);
        //else        c.drawText(title, ctrX, textY, textP);
        float tot = 0.0f;
        for(int i = 0; i<3; i++) {
            float val = Math.min( D.getVal(dID + i) * yScale, maxY);
            val = Math.max(val,-maxY);
            c.drawRect(
                    barX[i],
                    ctrY + ( val < 0.0f ? val : 0.0f ),
                    barX[i]+barW,
                    ctrY + ( val < 0.0f ? 0.0f : val ),
                    pBar);
            c.drawText(labels[i], barTextX[i],barTextY,pBar);
            tot += val;
        }

        tot = Math.min( tot, maxY);
        tot = Math.max(tot,-maxY);


        c.drawRect(barX[3],
                ctrY + ( tot < 0.0f ? tot : 0.0f ),
                barX[3]+barW,
                ctrY + ( tot < 0.0f ? 0.0f : tot ),
                pBar);
        c.drawText(labels[3], barTextX[3],barTextY,pBar);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        barW = sqBounds.width() / 6.5f;
        float xx = barW/2 + sqBounds.left;
        barTextY = sqBounds.top + diameter/4.0f;


        for (int i = 0; i < 4; i++, xx += barW*3/2) {
            barX[i] = xx;
            barTextX[i] = xx + barW/2;
        }
        yScale = sqBounds.height() / ( maxVal * 2.2f );
        maxY = maxVal * yScale;

                pBar.setColor(Color.WHITE);;
        pBar.setStyle(Paint.Style.FILL);
        pBar.setTextAlign(Paint.Align.CENTER);
        pBar.setTextSize(barW * 2.0f / 3.0f);

    }

    public void set(int i) {
        if (i == -1) Log.e(title, "ID == -1");
        id = i;
        dID = i;
        switch (id){
            case D.pA:
                title = "Motor A";
                break;
            case D.pB:
                title = "Motor B";
                break;
            case D.pC:
                title = "Motor C";
                break;
            case D.pD:
                title = "Motor D";
                break;
            default :
                Log.e(TAG,"dID not set " + id);
                dID = 3;
        }
        invalidate();
    }
}
