package com.clark.opengldemo;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class ChapterMain extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	private Handler mHandler=null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mGLSurfaceView = new GLSurfaceView(this);

		//mGLSurfaceView.setRenderer(new MyGlRender(this));
		//mGLSurfaceView.setRenderer(new TraceRender(this));
		mGLSurfaceView.setRenderer(new FogRender(this));
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		setContentView(mGLSurfaceView);
		mHandler=new Handler(Looper.getMainLooper());
		startRender();
	}

	private void startRender(){
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mGLSurfaceView.requestRender();
				startRender();
			}
		},100);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
    protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		mGLSurfaceView.onPause();
	}
}