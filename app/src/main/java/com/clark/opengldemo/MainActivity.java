package com.clark.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private GLSurfaceView.Renderer mGLRender;
    private Handler mHandler=null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
//        mGLRender = new ParticleRender(this);
        //mGLRender = new ChangeRender(this);
        mGLRender = new MultiRender(this);
        mGLSurfaceView.setRenderer(mGLRender);
        //mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLSurfaceView);
        mHandler=new Handler();
        //startRender();
    }

    private void startRender(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGLSurfaceView.requestRender();
                startRender();
            }
        },42);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mGLRender instanceof ParticleRender){
            ((ParticleRender)mGLRender).onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mGLRender instanceof ChangeRender){
            ((ChangeRender)mGLRender).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }
}