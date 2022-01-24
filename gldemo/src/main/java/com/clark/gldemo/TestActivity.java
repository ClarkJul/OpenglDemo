package com.clark.gldemo;

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

import com.clark.gldemo.utils.OpenGLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class TestActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    GLSurfaceView glSurfaceView;
    GLRenderer glRenderer=null;
    ImageView ivDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        glSurfaceView=findViewById(R.id.gsv_display);
        ivDisplay=findViewById(R.id.iv_display);

        glRenderer=new GLRenderer();
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(glRenderer);

    }

    @Override
    public void onGlobalLayout() {
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glSurfaceView.requestRender();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (glRenderer!=null){
            glRenderer.destory();
            glRenderer=null;
        }

    }

    private Bitmap loadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        private float[] verticesCoords = {
                -1.0f, 0.5f, 0.0f,  // Position 0
                -1.0f, -0.5f, 0.0f,  // Position 1
                1.0f, -0.5f, 0.0f,   // Position 2
                1.0f, 0.5f, 0.0f,   // Position 3
        };

        private float[] textureCoords = {
                0.0f, 0.0f,        // TexCoord 0
                0.0f, 1.0f,        // TexCoord 1
                1.0f, 1.0f,        // TexCoord 2
                1.0f, 0.0f         // TexCoord 3
        };

        private final short[] indices = {0, 1, 2, 0, 2, 3};


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

        private int aPosition;
        private int aTexture;
        private int samplerTexture;

        private FloatBuffer verticesBuffer;
        private FloatBuffer textureBuffer;
        private ShortBuffer indicesBuffer;

        private int glProgram;
        private int textureId;
        private Bitmap image=null;

        public GLRenderer() {
            verticesBuffer = (FloatBuffer) OpenGLUtil.createBuffer(verticesCoords);
            textureBuffer = (FloatBuffer) OpenGLUtil.createBuffer(textureCoords);
            indicesBuffer = (ShortBuffer) OpenGLUtil.createBuffer(indices);
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.e("GLRenderer", "onSurfaceCreated: " );
            glProgram=OpenGLUtil.createProgram(sVertexShader,sFragmentShader);
            aPosition = GLES20.glGetAttribLocation(glProgram, "a_position");
            aTexture = GLES20.glGetAttribLocation(glProgram, "a_textureCoord");
            samplerTexture = GLES20.glGetUniformLocation(glProgram, "u_samplerTexture");

            textureId = OpenGLUtil.glGenTexture();
            image=loadRGBAImage(R.drawable.dzzz);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.e("GLRenderer", "onSurfaceChanged:");
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUseProgram(glProgram);
            //启用顶点属性数组
            GLES20.glEnableVertexAttribArray(aPosition);
            GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 12, verticesBuffer);
            GLES20.glEnableVertexAttribArray(aTexture);
            GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
            //设置纹理采样器的值
            GLES20.glUniform1i(samplerTexture, 0);
            //绑定纹理单元（为上面的采样器绑定）
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            //OpenGLUtil.checkGlError("glBindTexture");
            //GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, imageBuffer);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
            OpenGLUtil.checkGlError("glTexImage2D");
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
        }

        public void destory(){
            if (glProgram != 0) {
                GLES20.glDeleteProgram(glProgram);
                GLES20.glDeleteTextures(1, new int[]{textureId},0);
                glProgram = GLES20.GL_NONE;
            }
        }
    }
}
