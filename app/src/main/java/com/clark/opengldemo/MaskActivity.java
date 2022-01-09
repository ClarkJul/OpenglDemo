package com.clark.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

public class MaskActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    private MaskRender mGLRender;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 实例化GLSurfaceView
        mGLSurfaceView = new GLSurfaceView(this);

        //实例化渲染器
        mGLRender = new MaskRender(this);

        // 设置渲染器
        mGLSurfaceView.setRenderer(mGLRender);

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGLSurfaceView.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_SPACE:
                mGLRender.ChangeScreen();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mGLRender.UseMasking();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}