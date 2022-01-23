package com.clark.gldemo.sample;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.clark.gldemo.NativeImage;
import com.clark.gldemo.utils.OpenGLUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Clark
 * 2022/1/22 17:29
 */
public class TextureMapSample extends GLSampleBase {
    private final float[] verticesCoords = {
            -1.0f, 0.5f, 0.0f,  // Position 0
            -1.0f, -0.5f, 0.0f,  // Position 1
            1.0f, -0.5f, 0.0f,   // Position 2
            1.0f, 0.5f, 0.0f,   // Position 3
    };

    private final float[] textureCoords = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    private final short[] indices = {0, 1, 2, 0, 2, 3};

    final String sVertexShader =
            "attribute vec4 a_position;"
                    + "attribute vec2 a_texCoord;"
                    + "varying vec2 v_texCoord;"
                    + "void main()"
                    + "{"
                    + "gl_Position = a_position;"
                    + "v_texCoord = a_texCoord;"
                    + "}";

    final String sFragmentShader =
            "precision mediump float;"
                    + "varying vec2 v_texCoord;"
                    + "uniform sampler2D u_samplerTexture;"
                    + "void main()"
                    + "{"
                    + "gl_FragColor = texture2D(u_samplerTexture,v_texCoord);"
                    + "}";

    private int m_TextureId;
    private int m_SamplerLoc;
    private int aPosition;
    private int aTexture;

    private FloatBuffer verticesBuffer = null;
    private FloatBuffer textureBuffer = null;
    private ShortBuffer indicesBuffer = null;

    private Bitmap image;


    public TextureMapSample() {
        verticesBuffer = (FloatBuffer) createBuffer(verticesCoords);
        textureBuffer = (FloatBuffer) createBuffer(textureCoords);
        indicesBuffer = (ShortBuffer) createBuffer(indices);
    }

    @Override
    public void init() {
        Log.e(TAG, "TextureMapSample init()" );
        m_TextureId=OpenGLUtil.glGenTexture();
        //String vShaderStr = OpenGLUtil.readRawShaderFile(OpenglApp.getApp(), R.raw.texture_map_vert);
        //String fShaderStr = OpenGLUtil.readRawShaderFile(OpenglApp.getApp(), R.raw.texture_map_frag);
        m_ProgramObj = OpenGLUtil.createProgram(sVertexShader,sFragmentShader);
        if (m_ProgramObj != 0) {
            m_SamplerLoc = GLES20.glGetUniformLocation(m_ProgramObj, "u_samplerTexture");
            aPosition = GLES20.glGetUniformLocation(m_ProgramObj, "a_position");
            aTexture = GLES20.glGetUniformLocation(m_ProgramObj, "a_texCoord");

            // Use the program object
            GLES20.glUseProgram(m_ProgramObj);
        } else {
            Log.e(TAG, "TextureMapSample Init create program fail");
        }
    }

    @Override
    public void loadImage(Bitmap bitmap) {
        Log.e(TAG, "TextureMapSample loadImage() " );
       image=bitmap;//Bitmap.createBitmap(bitmap);
    }


    @Override
    public void draw(int screenW, int screenH) {
        Log.e(TAG, "TextureMapSample Draw()");
        if (m_ProgramObj == GLES20.GL_NONE || m_TextureId == GLES20.GL_NONE) {
            return;
        }
        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Load the vertex position
        verticesBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        // Load the texture coordinate
        textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTexture);
        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        // Set the RGBA map sampler to texture unit to 0
        GLES20.glUniform1i(m_SamplerLoc, 0);

        // Bind the RGBA map
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_TextureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);

        indicesBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
    }

    @Override
    public void destroy() {
        Log.e(TAG, "TextureMapSample destroy()" );
        if (m_ProgramObj != 0) {
            GLES20.glDeleteProgram(m_ProgramObj);
            GLES20.glDeleteTextures(1, (IntBuffer) createBuffer(new int[]{m_TextureId}));
            m_ProgramObj = GLES20.GL_NONE;
        }
    }
}
