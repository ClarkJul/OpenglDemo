package com.clark.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer, ViewTreeObserver.OnGlobalLayoutListener{
    final float[] vertexCoord = {
            -0.5f, 0.5f, 0.0f,  // Position 0
            -0.5f, -0.5f, 0.0f,  // Position 1
            0.5f, -0.5f, 0.0f,   // Position 2
            0.5f, 0.5f, 0.0f,   // Position 3
    };

    final float[] textureCoord = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    final short[] mdrawCoord = {0, 1, 2, 0, 2, 3};

    final String sVertexShader =
            "attribute vec4 a_position;"
                    + "attribute vec2 a_textureCoord;"
                    + "varying vec2 v_textureCoord;"
                    + "void main()"
                    + "{"
                    + "gl_Position = a_position;"
                    + "v_textureCoord = a_textureCoord;"
                    + "}";

    final String sFragmentShader =
            "precision mediump float;"
                    + "varying vec2 v_textureCoord;"
                    + "uniform sampler2D u_samplerTexture;"
                    + "void main()"
                    + "{"
                    + "gl_FragColor = texture2D(u_samplerTexture,v_textureCoord);"
                    + "}";

    GLSurfaceView mSurfaceView;
    public int mprogramHandle;
    public int mAPositionIndex;
    public int mATextureCoordIndex;
    public int mUniformTextureIndex;

    public FloatBuffer fb_position;
    public FloatBuffer fb_texturecoord;
    public ShortBuffer fb_drawCoord;

    private ImageView imageView;

    public int textureId;
    Bitmap bitmap=null;
    private ByteBuffer imageBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);
        mSurfaceView = findViewById(R.id.glv_content);
        imageView = findViewById(R.id.iv_display);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(this);

    }

    @Override
    public void onGlobalLayout() {
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //激活2D纹理
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //创建缓存，存放数据
        fb_position = ByteBuffer.allocateDirect(vertexCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb_position.put(vertexCoord).position(0);
        fb_texturecoord = ByteBuffer.allocateDirect(textureCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb_texturecoord.put(textureCoord).position(0);

        fb_drawCoord = ByteBuffer.allocateDirect(mdrawCoord.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        fb_drawCoord.put(mdrawCoord).position(0);
        mprogramHandle= OpenGLUtil.createProgram(sVertexShader,sFragmentShader);
        //获取渲染器中变量的句柄
        mAPositionIndex = GLES20.glGetAttribLocation(mprogramHandle, "a_position");
        mATextureCoordIndex = GLES20.glGetAttribLocation(mprogramHandle, "a_textureCoord");
        mUniformTextureIndex = GLES20.glGetUniformLocation(mprogramHandle, "u_samplerTexture");

        textureId = OpenGLUtil.glGenTexture();
        bitmap = loadRGBAImage(R.drawable.texture);

    }

    private Bitmap loadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                imageBuffer = ByteBuffer.allocate(bytes).order(ByteOrder.nativeOrder());
                bitmap.copyPixelsToBuffer(imageBuffer);
                byte[] byteArray = imageBuffer.array();
                Bitmap test= Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                test.copyPixelsFromBuffer(ByteBuffer.wrap(byteArray));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(test);
                    }
                });
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

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(mprogramHandle);
        //启用顶点属性数组
        GLES20.glEnableVertexAttribArray(mAPositionIndex);
        GLES20.glVertexAttribPointer(mAPositionIndex, 3, GLES20.GL_FLOAT, false, 12, fb_position);
        GLES20.glEnableVertexAttribArray(mATextureCoordIndex);
        GLES20.glVertexAttribPointer(mATextureCoordIndex, 2, GLES20.GL_FLOAT, false, 8, fb_texturecoord);
        //设置纹理采样器的值
        GLES20.glUniform1i(mUniformTextureIndex, 0);
        //绑定纹理单元（为上面的采样器绑定）
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //OpenGLUtil.checkGlError("glBindTexture");
        //GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, imageBuffer);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        OpenGLUtil.checkGlError("glTexImage2D");
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mdrawCoord.length, GLES20.GL_UNSIGNED_SHORT, fb_drawCoord);
        //bitmap.recycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }
}