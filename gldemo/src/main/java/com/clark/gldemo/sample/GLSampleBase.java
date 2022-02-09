package com.clark.gldemo.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.clark.gldemo.NativeImage;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Clark
 * 2022/1/22 17:33
 */
public abstract class GLSampleBase {
    protected static final String TAG="GLSample";
    protected int glProgram;
    public boolean isInited=false;

    public void loadImage(NativeImage pImage) {

    }
    public void loadImage(Bitmap bitmap) {

    }

    public void loadMultiImageWithIndex(int index, NativeImage pImage) {
    }

    public void loadShortArrData(short[] pShortArr) {
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
    }

    public void setTouchLocation(float x, float y) {
    }

    public void setGravityXY(float x, float y) {
    }

    public abstract void init();

    public abstract void draw(int screenW, int screenH);

    public abstract void destroy();

    public final void copyNativeImage(NativeImage imageOrigin,NativeImage imageTarget){
        imageTarget.ppPlane[0]=imageOrigin.ppPlane[0];
        if (imageOrigin.ppPlane[1]!=null){
            imageTarget.ppPlane[1]=imageOrigin.ppPlane[1];
        }
        if (imageOrigin.ppPlane[2]!=null){
            imageTarget.ppPlane[2]=imageOrigin.ppPlane[2];
        }

        /*Log.e(TAG, "copyNativeImage: " +imageTarget.ppPlane[0].length);
        imageTarget.bitmap= Bitmap.createBitmap(404, 336, Bitmap.Config.ARGB_8888);
        imageTarget.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(imageTarget.ppPlane[0]));*/
    }
    public final void freeNativeImage(NativeImage imageTarget){
        imageTarget.ppPlane=new ByteBuffer[3];
    }

    protected  <T> Buffer createBuffer(T data) {
        Buffer res=null;
        //Log.e(TAG, "createBuffer: "+(data instanceof float[]) );
        if (data instanceof float[]){
            float[] input= (float[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            ((FloatBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof short[]){
            short[] input= (short[]) data;
            res = ByteBuffer.allocateDirect(input.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            ((ShortBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof int[]){
            int[] input= (int[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer();
            ((IntBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof byte[]){
            byte[] input= (byte[]) data;
            res = ByteBuffer.allocateDirect(input.length )
                    .order(ByteOrder.nativeOrder());
            ((ByteBuffer)res).put(input, 0, input.length).position(0);
        }
        return res;
    }
}
