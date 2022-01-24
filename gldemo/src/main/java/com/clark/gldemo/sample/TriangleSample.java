package com.clark.gldemo.sample;

import android.opengl.GLES20;

import com.clark.gldemo.utils.OpenGLUtil;

import java.nio.FloatBuffer;

/**
 * @author Clark
 * 2022/1/23 14:47
 */
public class TriangleSample extends GLSampleBase {
    String vShaderStr =
			"attribute vec4 vPosition;"+
            "void main()"+
            "{"+
            "gl_Position = vPosition;"+
            "}";

    String fShaderStr =
			"precision mediump float;"+
            "void main()"+
            "{"+
            "gl_FragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );"+
            "}";

    float[] vVertices = {
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };
    private FloatBuffer verticesBuffer = null;
    public TriangleSample() {
        verticesBuffer = (FloatBuffer) createBuffer(vVertices);
    }

    @Override
    public void init() {
        glProgram = OpenGLUtil.createProgram(vShaderStr,fShaderStr);
    }

    @Override
    public void draw(int screenW, int screenH) {
        if(glProgram == 0) {
            return;
        }

        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Use the program object
        GLES20.glUseProgram (glProgram);

        // Load the vertex data
        GLES20.glVertexAttribPointer (0, 3, GLES20.GL_FLOAT,false, 0, verticesBuffer );
        GLES20.glEnableVertexAttribArray (0);

        GLES20.glDrawArrays (GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glUseProgram (GLES20.GL_NONE);
    }

    @Override
    public void destroy() {
        if (glProgram !=0) {
            GLES20.glDeleteProgram(glProgram);
            glProgram = GLES20.GL_NONE;
        }
    }
}
