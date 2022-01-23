package com.clark.gldemo;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * @author Clark
 * 2022/1/22 17:37
 */
public class NativeImage {
   public int width;
   public int height;
   public int format;
   public byte[][] ppPlane;
   public Bitmap bitmap;

    public NativeImage() {
        width = 0;
        height = 0;
        format = 0;
        ppPlane=new byte[3][];
        ppPlane[0] = null;
        ppPlane[1] = null;
        ppPlane[2] = null;
        bitmap=null;
    }

    @Override
    public String toString() {
        return "NativeImage{" +
                "width=" + width +
                ", height=" + height +
                ", format=" + format +
                ", ppPlane=" + Arrays.toString(ppPlane) +
                '}';
    }
}
