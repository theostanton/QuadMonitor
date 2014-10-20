package com.theostanton.QuadMonitor.statics;

/**
 * Created by theo on 02/10/2014.
 */
public class Packet {

    public char id = '0';
    public float[] data;

    public Packet(char id, float[] data){
        this.id = id;
        this.data = data;
    }

}
