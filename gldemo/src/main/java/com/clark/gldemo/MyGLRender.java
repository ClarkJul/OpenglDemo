package com.clark.gldemo;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRender implements GLSurfaceView.Renderer {
    private static final String TAG = "MyGLRender";
    private MyNativeRender mNativeRender;
    private MyGLRenderContext mGLRenderContext;
    private int mSampleType;

    MyGLRender() {
        //mNativeRender = new MyNativeRender();
        mGLRenderContext = MyGLRenderContext.getInstanse();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //mNativeRender.native_OnSurfaceCreated();
        mGLRenderContext.onSurfaceCreated();
        Log.e(TAG, "onSurfaceCreated() called with: GL_VERSION = [" + gl.glGetString(GL10.GL_VERSION) + "]");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //mNativeRender.native_OnSurfaceChanged(width, height);
        mGLRenderContext.onSurfaceChanged(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //mNativeRender.native_OnDrawFrame();
        mGLRenderContext.onDrawFrame();
    }


    public void init() {
        //mNativeRender.native_Init();
    }

    public void unInit() {
        //mNativeRender.native_UnInit();
    }

    public void setParamsInt(int paramType, int value0, int value1) {
        if (paramType == Constant.SAMPLE_TYPE.SAMPLE_TYPE) {
            mSampleType = value0;
        }
        //mNativeRender.native_SetParamsInt(paramType, value0, value1);
        mGLRenderContext.setParamsInit(paramType, value0, value1);
    }

    public void setTouchLoc(float x, float y) {
        //mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_TOUCH_LOC, x, y);
        mGLRenderContext.setParamsFloat(Constant.SAMPLE_TYPE.SAMPLE_TYPE_SET_TOUCH_LOC, x, y);
    }

    public void setGravityXY(float x, float y) {
        //mNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_GRAVITY_XY, x, y);
        mGLRenderContext.setParamsFloat(Constant.SAMPLE_TYPE.SAMPLE_TYPE_SET_GRAVITY_XY, x, y);
    }

    public void setImageData(int format, int width, int height, byte[] bytes) {
       // mNativeRender.native_SetImageData(format, width, height, bytes);
        mGLRenderContext.setImageData(format,width,height,bytes);
    }
    public void setImageData(Bitmap bitmap) {
        // mNativeRender.native_SetImageData(format, width, height, bytes);
        mGLRenderContext.setImageData(bitmap);
    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        //mNativeRender.native_SetImageDataWithIndex(index, format, width, height, bytes);
        mGLRenderContext.setImageDataWithIndex(index,format,width,height,bytes);
    }

    public void setAudioData(short[] audioData) {
        //mNativeRender.native_SetAudioData(audioData);
        mGLRenderContext.setParamsShortArr(audioData);
    }

    public int getSampleType() {
        return mSampleType;
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
        //mNativeRender.native_UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
        mGLRenderContext.updateTransformMatrix(rotateX,rotateY,scaleX,scaleY);
    }

}
