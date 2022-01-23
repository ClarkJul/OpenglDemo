/**
 * Created by 公众号：字节流动 on 2021/3/12.
 * https://github.com/githubhaohao/NDK_OpenGLES_3_0
 * 最新文章首发于公众号：字节流动，有疑问或者技术交流可以添加微信 Byte-Flow ,领取视频教程, 拉你进技术交流群
 */

package com.clark.gldemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.clark.gldemo.adapter.MyRecyclerViewAdapter;
import com.clark.gldemo.audio.AudioCollector;
import com.clark.gldemo.egl.EGLActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class MainActivity extends AppCompatActivity implements AudioCollector.Callback, ViewTreeObserver.OnGlobalLayoutListener, SensorEventListener {
    private static final String TAG = "MainActivity";
    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] SAMPLE_TITLES = {
            "DrawTriangle",
            "TextureMap",
            "YUV Rendering",
            "VAO&VBO",
            "FBO Offscreen Rendering",
            "EGL Background Rendering",
            "FBO Stretching",
            "Coordinate System",
            "Basic Lighting",
            "Transform Feedback",
            "Complex Lighting",
            "Depth Testing",
            "Instancing",
            "Stencil Testing",
            "Blending",
            "Particles",
            "SkyBox",
            "Assimp Load 3D Model",
            "PBO",
            "Beating Heart",
            "Cloud",
            "Time Tunnel",
            "Bezier Curve",
            "Big Eyes",
            "Face Slender",
            "Big Head",
            "Rotary Head",
            "Visualize Audio",
            "Scratch Card",
            "3D Avatar",
            "Shock Wave",
            "MRT",
            "FBO Blit",
            "Texture Buffer",
            "Uniform Buffer",
            "RGB to YUYV",
            "Multi-Thread Render",
            "Text Render",
            "Portrait stay color",
            "GL Transitions_1",
            "GL Transitions_2",
            "GL Transitions_3",
            "GL Transitions_4",
            "RGB to NV21",
            "RGB to I420",
    };

    private MyGLSurfaceView mGLSurfaceView;
    private ViewGroup mRootView;
    //private int mSampleSelectedIndex = Constant.SAMPLE_TYPE.SAMPLE_TYPE_BEATING_HEART - Constant.SAMPLE_TYPE.SAMPLE_TYPE;
    private int mSampleSelectedIndex = Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRIANGLE - Constant.SAMPLE_TYPE.SAMPLE_TYPE;
    private AudioCollector mAudioCollector;
    private MyGLRender mGLRender = new MyGLRender();
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGLRender.init();

    }

    @Override
    public void onGlobalLayout() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGLSurfaceView = new MyGLSurfaceView(this, mGLRender);
        mRootView.addView(mGLSurfaceView, lp);
        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
        ///sdcard/Android/data/com.byteflow.app/files/Download
        String fileDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        CommonUtils.copyAssetsDirToSDCard(MainActivity.this, "poly", fileDir + "/model");
        CommonUtils.copyAssetsDirToSDCard(MainActivity.this, "fonts", fileDir);
        CommonUtils.copyAssetsDirToSDCard(MainActivity.this, "yuv", fileDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(this, "We need the permission: WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (mAudioCollector != null) {
            mAudioCollector.unInit();
            mAudioCollector = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRender.unInit();
        /*
         * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
         * so this is unnecessary.
         * */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_sample) {
            showGLSampleDialog();
        }
        return true;
    }

    @Override
    public void onAudioBufferCallback(short[] buffer) {
        Log.e(TAG, "onAudioBufferCallback() called with: buffer[0] = [" + buffer[0] + "]");
        mGLRender.setAudioData(buffer);
        //mGLSurfaceView.requestRender();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                Log.d(TAG, "onSensorChanged() called with TYPE_GRAVITY: [x,y,z] = [" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + "]");
                if (mSampleSelectedIndex + Constant.SAMPLE_TYPE.SAMPLE_TYPE == Constant.SAMPLE_TYPE.SAMPLE_TYPE_AVATAR) {
                    mGLRender.setGravityXY(event.values[0], event.values[1]);
                }
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showGLSampleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View rootView = inflater.inflate(R.layout.sample_selected_layout, null);

        final AlertDialog dialog = builder.create();

        Button confirmBtn = rootView.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final RecyclerView resolutionsListView = rootView.findViewById(R.id.resolution_list_view);

        final MyRecyclerViewAdapter myPreviewSizeViewAdapter = new MyRecyclerViewAdapter(this, Arrays.asList(SAMPLE_TITLES));
        myPreviewSizeViewAdapter.setSelectIndex(mSampleSelectedIndex);
        myPreviewSizeViewAdapter.addOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mRootView.removeView(mGLSurfaceView);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                mGLSurfaceView = new MyGLSurfaceView(MainActivity.this, mGLRender);
                mRootView.addView(mGLSurfaceView, lp);

                int selectIndex = myPreviewSizeViewAdapter.getSelectIndex();
                myPreviewSizeViewAdapter.setSelectIndex(position);
                myPreviewSizeViewAdapter.notifyItemChanged(selectIndex);
                myPreviewSizeViewAdapter.notifyItemChanged(position);
                mSampleSelectedIndex = position;
                mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

                if (mRootView.getWidth() != mGLSurfaceView.getWidth()
                        || mRootView.getHeight() != mGLSurfaceView.getHeight()) {
                    mGLSurfaceView.setAspectRatio(mRootView.getWidth(), mRootView.getHeight());
                }

                mGLRender.setParamsInt(Constant.SAMPLE_TYPE.SAMPLE_TYPE, position + Constant.SAMPLE_TYPE.SAMPLE_TYPE, 0);

                int sampleType = position + Constant.SAMPLE_TYPE.SAMPLE_TYPE;
                Bitmap tmp;
                switch (sampleType) {
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRIANGLE:
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXTURE_MAP:
                        loadRGBAImage(R.drawable.dzzz);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_YUV_TEXTURE_MAP:
                        //loadNV21Image();
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VAO:
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO:
                        //loadRGBAImage(R.drawable.java);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO_LEG:
                        //loadRGBAImage(R.drawable.leg);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_EGL:
                        //startActivity(new Intent(MainActivity.this, EGLActivity.class));
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_COORD_SYSTEM:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BASIC_LIGHTING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANS_FEEDBACK:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MULTI_LIGHTS:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_DEPTH_TESTING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_INSTANCING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_STENCIL_TESTING:
                        //loadRGBAImage(R.drawable.board_texture);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BLENDING:
                        //loadRGBAImage(R.drawable.board_texture,0);
                        //loadRGBAImage(R.drawable.floor,1);
                        //loadRGBAImage(R.drawable.window,2);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PARTICLES:
                        //loadRGBAImage(R.drawable.board_texture);
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SKYBOX:
                        //loadRGBAImage(R.drawable.right,0);
                        //loadRGBAImage(R.drawable.left,1);
                        //loadRGBAImage(R.drawable.top,2);
                        //loadRGBAImage(R.drawable.bottom,3);
                        //loadRGBAImage(R.drawable.back,4);
                        //loadRGBAImage(R.drawable.front,5);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PBO:
                        //loadRGBAImage(R.drawable.front);
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BEATING_HEART:
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_CLOUD:
                        //loadRGBAImage(R.drawable.noise);
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TIME_TUNNEL:
                        //loadRGBAImage(R.drawable.front);
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BEZIER_CURVE:
                        //loadRGBAImage(R.drawable.board_texture);
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BIG_EYES:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FACE_SLENDER:
                        //Bitmap bitmap = loadRGBAImage(R.drawable.yifei);
                        //mGLSurfaceView.setAspectRatio(bitmap.getWidth(), bitmap.getHeight());
                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BIG_HEAD:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_ROTARY_HEAD:
//                        Bitmap b = loadRGBAImage(R.drawable.huge);
//                        mGLSurfaceView.setAspectRatio(b.getWidth(), b.getHeight());
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VISUALIZE_AUDIO:
//                        if(mAudioCollector == null) {
//                            mAudioCollector = new AudioCollector();
//                            mAudioCollector.addCallback(MainActivity.this);
//                            mAudioCollector.init();
//                        }
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SCRATCH_CARD:
//                        Bitmap b1 = loadRGBAImage(R.drawable.yifei);
//                        mGLSurfaceView.setAspectRatio(b1.getWidth(), b1.getHeight());
//                        //mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_AVATAR:
//                        Bitmap b2 = loadRGBAImage(R.drawable.avatar_a, 0);
//                        mGLSurfaceView.setAspectRatio(b2.getWidth(), b2.getHeight());
//                        loadRGBAImage(R.drawable.avatar_b, 1);
//                        loadRGBAImage(R.drawable.avatar_c, 2);
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SHOCK_WAVE:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MULTI_THREAD_RENDER:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXT_RENDER:
//                        Bitmap b3 = loadRGBAImage(R.drawable.lye);
//                        mGLSurfaceView.setAspectRatio(b3.getWidth(), b3.getHeight());
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MRT:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO_BLIT:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TBO:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_UBO:
//                        Bitmap b4 = loadRGBAImage(R.drawable.lye);
//                        mGLSurfaceView.setAspectRatio(b4.getWidth(), b4.getHeight());
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2YUYV:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2NV21:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2I420:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2I444:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_HWBuffer:
//                        tmp = loadRGBAImage(R.drawable.sk);
//                        mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_STAY_COLOR:
//                        loadGrayImage();
//                        Bitmap b5 = loadRGBAImage(R.drawable.lye2);
//                        loadRGBAImage(R.drawable.ascii_mapping, 1);
//                        mGLSurfaceView.setAspectRatio(b5.getWidth(), b5.getHeight());
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_1:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_2:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_3:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_4:
//                        loadRGBAImage(R.drawable.lye, 0);
//                        loadRGBAImage(R.drawable.lye4, 1);
//                        loadRGBAImage(R.drawable.lye5, 2);
//                        loadRGBAImage(R.drawable.lye6, 3);
//                        loadRGBAImage(R.drawable.lye7, 4);
//                        tmp = loadRGBAImage(R.drawable.lye8, 5);
//                        mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                        break;
//                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_CONVEYOR_BELT:
//                        tmp = loadRGBAImage(R.drawable.lye4);
//                        mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
//                        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
//                        break;
                    default:
                        break;
                }

                mGLSurfaceView.requestRender();

                if (sampleType != Constant.SAMPLE_TYPE.SAMPLE_TYPE_VISUALIZE_AUDIO && mAudioCollector != null) {
                    mAudioCollector.unInit();
                    mAudioCollector = null;
                }

                dialog.cancel();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        resolutionsListView.setLayoutManager(manager);

        resolutionsListView.setAdapter(myPreviewSizeViewAdapter);
        resolutionsListView.scrollToPosition(mSampleSelectedIndex);

        dialog.show();
        dialog.getWindow().setContentView(rootView);

    }

    private Bitmap loadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                mGLRender.setImageData(bitmap);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private Bitmap loadRGBAImage(int resId, int index) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mGLRender.setImageDataWithIndex(index, Constant.IMAGE.FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private void loadNV21Image() {
        InputStream is = null;
        try {
            is = getAssets().open("YUV_Image_840x1074.NV21");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int lenght = 0;
        try {
            lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            mGLRender.setImageData(Constant.IMAGE.FORMAT_NV21, 840, 1074, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadGrayImage() {
        InputStream is = null;
        try {
            is = getAssets().open("lye_1280x800.Gray");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int lenght = 0;
        try {
            lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            mGLRender.setImageDataWithIndex(0, Constant.IMAGE.FORMAT_GRAY, 1280, 800, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}