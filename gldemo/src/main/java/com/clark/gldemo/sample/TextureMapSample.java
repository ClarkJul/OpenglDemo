package com.clark.gldemo.sample;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.clark.gldemo.utils.OpenGLUtil;

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

    private int textureId;
    private int samplerLoc;
    private int positionAtt;
    private int textureAtt;

    private FloatBuffer verticesBuffer ;
    private FloatBuffer textureBuffer ;
    private ShortBuffer indicesBuffer ;

    private Bitmap image;

    public TextureMapSample() {
        verticesBuffer = (FloatBuffer) createBuffer(verticesCoords);
        textureBuffer = (FloatBuffer) createBuffer(textureCoords);
        indicesBuffer = (ShortBuffer) createBuffer(indices);
    }

    @Override
    public void init() {
        Log.e(TAG, "TextureMapSample init()" );
        glProgram = OpenGLUtil.createProgram(sVertexShader,sFragmentShader);
        if (glProgram != 0) {
            samplerLoc = GLES20.glGetUniformLocation(glProgram, "u_samplerTexture");
            positionAtt = GLES20.glGetAttribLocation(glProgram, "a_position");
            textureAtt = GLES20.glGetAttribLocation(glProgram, "a_texCoord");
        } else {
            Log.e(TAG, "TextureMapSample Init create program fail");
        }
        textureId =OpenGLUtil.glGenTexture();
    }

    @Override
    public void loadImage(Bitmap bitmap) {
        Log.e(TAG, "TextureMapSample loadImage() " );
       image=bitmap;//Bitmap.createBitmap(bitmap);
    }


    @Override
    public void draw(int screenW, int screenH) {
        Log.e(TAG, "TextureMapSample Draw()");
        if (glProgram == GLES20.GL_NONE || textureId == GLES20.GL_NONE) {
            return;
        }

        GLES20.glUseProgram(glProgram);
        // Load the vertex position
        GLES20.glEnableVertexAttribArray(positionAtt);
        GLES20.glVertexAttribPointer(positionAtt, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        // Load the texture coordinate

        GLES20.glEnableVertexAttribArray(textureAtt);
        GLES20.glVertexAttribPointer(textureAtt, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        // Set the RGBA map sampler to texture unit to 0
        GLES20.glUniform1i(samplerLoc, 0);

        // Bind the RGBA map
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
    }

    @Override
    public void destroy() {
        Log.e(TAG, "TextureMapSample destroy()" );
        if (glProgram != 0) {
            GLES20.glDeleteProgram(glProgram);
            GLES20.glDeleteTextures(1, (IntBuffer) createBuffer(new int[]{textureId}));
            glProgram = GLES20.GL_NONE;
        }
    }
}
