package com.theostanton.QuadMonitor;

import java.util.Random;

/**
 * Created by theo on 23/04/2014.
 */
public class Value {

    private static Random r = new Random();
    public float val = 0.1f;
    public float max = 55.0f;
    public float min = -55.0f;
    private String name = "Label";
    private boolean movingAverage = false;

    public void setMax(Float m) {
        max = m;
    }

    public void setMin(Float m) {
        min = m;
    }

    public void setVal(float v) {
        if (v > max) val = max;
        else if (v < min) val = min;
        else val = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String str) {
        name = str;
    }

    public float getVal() {
        return val;
    }

    public void setVal(byte byt) {
        float v = (float) byt;
        if (movingAverage) {
            v += val;
            v /= 2.0f;
        }
        if (v > max) val = max;
        else if (v < min) val = min;
        else val = v;
    }

    public void tickRandom() {
        val = (float) (r.nextGaussian()) * max + val;
        val /= 2.0f;
        val = Math.min(val, 45.0f);
        val = Math.max(val, -45.0f);
    }

    public void enableMovingAverager() {
        movingAverage = true;
    }

    public void disableMovingAverager() {
        movingAverage = false;
    }

    @Override
    public String toString() {
        return name + ":" + String.valueOf(val);
    }

}
