package com.theostanton.QuadMonitor.graphs;//package com.example.NavigationEmulation.graphs;
//
//import android.content.Context;
//import android.graphics.*;
//import android.util.AttributeSet;
//import android.util.Log;
//import com.example.NavigationEmulation.Component;
//import com.example.NavigationEmulation.D;
//
//import java.util.LinkedList;
//
///**
// * Created by theo on 23/04/2014.
// */
//public class OLDGraph extends Component{
//
//    private static final String TAG = "Graph";
//
//    private static Paint lineP;
//    private Bitmap capture = null;
//
//    private static final int ROLL = 0;
//    private static final int PITCH = 1;
//    private static final int YAW = 2;
//    private int dID = -1;
//
//    private float scale;
//
//    private RectF graphBounds;
//    private Rect bitMapBounds;
//    private Rect shift;
//    private Canvas cc;
//    private Bitmap blank;
//
//    public OLDGraph(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        title = "Graph";
//        lineP = new Paint(Paint.ANTI_ALIAS_FLAG);
//        lineP.setStyle(Paint.Style.STROKE);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        float topPad = height / 6.0f;
//        height -= topPad;
//        graphBounds = new RectF(left,top+topPad,right,bot);
//        ctrY+=topPad/2.0f;
//        bitMapBounds = new Rect(0,0,D.getList(dID).size() - 1,360);
//        shift = new Rect(-1,0,D.getList(dID).size() - 2,360);
//        //scale /= 55.0f;
//        scale = 2.0f;
//        Log.d(TAG, String.valueOf(scale));
//
//
//        //if(cc == null) {
//        blank = Bitmap.createBitmap(D.getList(dID).size() - 1,360, Bitmap.Config.ARGB_8888);
//        capture = blank;
//        //capture = Bitmap.createBitmap(D.getList(dID).size() - 1,360, Bitmap.Config.ARGB_8888);
//        cc = new Canvas(capture);
//        //}
//    }
//
//
//
//
//    @Override
//    protected void onDraw(Canvas c) {
//        super.onDraw(c);
//
//        c.drawBitmap(drawBitMapOpt(), bitMapBounds, graphBounds, p);
//        c.drawRect(graphBounds, frameP);
//        c.drawLine(left, ctrY, right, ctrY, frameP);
//        c.drawText(title,ctrX,textY,textP);
//
//
//
//    }
//
//    public Bitmap drawBitMapOpt(){
//        Bitmap old = capture;
//        capture = Bitmap.createBitmap(D.getList(dID).size() - 1,360, Bitmap.Config.ARGB_8888);;
//        Canvas cc = new Canvas(capture);
//        if(old != null) cc.drawBitmap(old,-1,0,p);
//
//        lineP.setColor(Color.RED);
//        plotLast(dID, cc);
//
//        lineP.setColor(Color.GREEN);
//        plotLast(dID + 1, cc);
//
//        lineP.setColor(Color.BLUE);
//        plotLast(dID + 2, cc);
//        return capture;
//    }
//
//    public void drawBitMap() {
//
//        capture = Bitmap.createBitmap(D.getList(dID).size() - 1,360, Bitmap.Config.ARGB_8888);
//        Canvas cc = new Canvas(capture);
//
//        lineP.setColor(Color.RED);
//        plotLine(dID, cc);
//
//        lineP.setColor(Color.GREEN);
//        plotLine(dID + 1, cc);
//
//        lineP.setColor(Color.BLUE);
//        plotLine(dID + 2, cc);
////
////        cc.drawRect(graphBounds, frameP);
////        cc.drawLine(left, ctrY, right, ctrY, frameP);
////
////        p.setStyle(Paint.Style.FILL);
////        p.setTextSize(30);
//    }
//
//    public void plotLast(int id, Canvas c){
//        try {
//            LinkedList<Integer> list = D.getList(id);
//            c.drawLine(list.size() - 2, 180.0f - list.get(list.size() - 2) * scale, list.size() - 1, 180.0f - list.get(list.size() - 1) * scale, lineP);
//        }
//        catch (Exception e){
//            Log.e(TAG, "plotline dot error", e);
//        }
//    }
//
//    public void plotLine(int id, Canvas c) {
//        try {
//            LinkedList<Integer> list = D.getList(id);
//
//            for (int i = 0; i < list.size()-1; i++) {
//
//                //c.drawLine(i, ctrY - list.get(i)*scale, i + 1, ctrY - list.get(i + 1)*scale, lineP);
//                //c.drawLine(i, 10, i+1, cY + 10, p);
//            }
//        } catch (Exception e){
//            Log.e(TAG, "plotLine error", e);
//        }
//
//
//        //System.out.println(list.get(D.graphWidth-10));
//    }
//
//    public void set(int i) {
//        if (i == -1) Log.e(title, "ID == -1");
//        id = i;
//        dID = i;
//        switch (id) {
//            case ROLL:
//                title = "Roll";
//                //dID = 0;
//                break;
//            case PITCH:
//                title = "Pitch";
//                //dID = 3;
//                break;
//            case YAW:
//                title = "Yaw";
//                //dID = 6;
//                break;
//            default:
//                Log.e(TAG, "Unassigned ID");
//        }
//    }
//
//}
