package com.theostanton.QuadMonitor.graphs;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import com.theostanton.QuadMonitor.Component;
import com.theostanton.QuadMonitor.D;

/**
 * Created by theo on 23/04/2014.
 */
public class Graph extends Component {

    private static final String TAG = "Graph";
    private static Paint lineP;
    private D d;
    // private static final int ROLL = 0;
    // private static final int PITCH = 1;
    // private static final int YAW = 2;
    private int dID = 0;
    private int axisID = -1;
    private float yScale = 0.0f;

    private int acc = 0;
    // private float scale;
    private RectF graphBounds;
    private Rect bitMapBounds;
    private Path path;
    private int x =0;


    // private Rect shift;
    // private Canvas cc;
    // private Bitmap blank;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        title = "Graph";
        d = D.getInstance();
        lineP = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineP.setStyle(Paint.Style.STROKE);
        path = new Path();
        path.moveTo(0,ctrY - 50);
    }

    public Graph(Context context, AttributeSet attrs, boolean f) {
        super(context, attrs);
        d = D.getInstance();
        focused = f;
        title = "Graph";
        lineP = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineP.setStyle(Paint.Style.STROKE);
        path = new Path();
        path.moveTo(0,ctrY - 50);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //float topPad = height / 12.0f; // sets padding at top
        float topPad = 0.0f;
        height -= topPad;
        graphBounds = new RectF(left,top,right,bot);
        //ctrY+=topPad/2.0f;
        ctrY = graphBounds.centerY();
        //bitMapBounds = new Rect(0,0,D.getList(dID).size() - 1,360);
        yScale = ctrY / 90.0f;
        Log.d(TAG,"yScale = " + yScale);
        d.setGraphBounds(getWidth()-2,(int)graphBounds.height());
        textP.setTextSize(100.0f);
        textY = graphBounds.top + textP.getTextSize()/2.0f + graphBounds.height() / 10.0f;
    }




    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawBitmap(d.getBmp(dID),1,graphBounds.top,p);
        if(!focused) c.drawRect(graphBounds, frameP);
        c.drawLine(left, ctrY, right, ctrY, frameP);
//        for(float x = graphBounds.width() - D.getSampleRateOffset(); x > 0.0f; x -= D.getXScale() * D.getSampleRate()){
//            c.drawLine(x,ctrY - 4.0f,x,ctrY + 4.0f,frameP);
//        }
        c.drawText(title, ctrX, textY, textP);
    }

    public void set(int i){
        if (i == -1) Log.e(title, "ID[0] == -1");
        id = i;
        dID = i;
        title = D.getAxisTitle(dID);
    }
}