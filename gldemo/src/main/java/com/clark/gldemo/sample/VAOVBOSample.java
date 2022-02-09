package com.clark.gldemo.sample;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import com.clark.gldemo.utils.OpenGL32Util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class VAOVBOSample extends GLSampleBase {

    private final int VERTEX_POS_SIZE = 3;// x, y and z
    private final int VERTEX_COLOR_SIZE = 4; // r, g, b, and a
    private final int VERTEX_POS_INDX = 0; //shader layout loc
    private final int VERTEX_COLOR_INDX = 1;//shader layout loc
    private final int VERTEX_STRIDE = 4 * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE);

    final String vShaderStr=
	       "attribute vec4 a_position;"
           +"attribute vec4 a_color;"
           +"varying vec4 v_color;"
           +"varying vec4 v_position;"
           +"void main()"
           +"{"
           +"v_color = a_color;"
           +"gl_Position = a_position;"
           +"v_position = a_position;"
           +"}";


	final String fShaderStr=
		    "precision mediump float;"
           +"varying vec4 v_color;"
           +"varying vec4 v_position;"
           +"void main()"
           +"{"
           +"float n = 10.0;"
           +"float span = 1.0 / n;"
           +"int i = int((v_position.x + 0.5)/span);"
           +"int j = int((v_position.y + 0.5)/span);"
           +"int grayColor = int(mod(float(i+j), 2.0));"
           +"if(grayColor == 1)"
           +"{"
           +"float luminance = v_color.r*0.299 + v_color.g*0.587 + v_color.b*0.114;"
           +"gl_FragColor = vec4(vec3(luminance), v_color.a);"
           +"}"
           +"else"
           +"{"
           +"gl_FragColor = v_color;"
           +"}"
           +"}";

    // 4 vertices, with(x,y,z) ,(r, g, b, a) per-vertex
    final float[] vertices=new float[]{
                -0.5f,  0.5f, 0.0f,       // v0
                1.0f,  0.0f, 0.0f, 1.0f,  // c0
                -0.5f, -0.5f, 0.0f,       // v1
                0.0f,  1.0f, 0.0f, 1.0f,  // c1
                0.5f, -0.5f, 0.0f,        // v2
                0.0f,  0.0f, 1.0f, 1.0f,  // c2
                0.5f,  0.5f, 0.0f,        // v3
                0.5f,  1.0f, 1.0f, 1.0f,  // c3
           };
    // Index buffer data
    final short[] indices= { 0, 1, 2, 0, 2, 3};

    private int mProgram=0;

    int[] vaoId ={0};
    int[] vboIds ={0,0};
    private int positionAtt;
    private int textureAtt;

    @Override
    public void init() {
        OpenGL32Util.checkGlError("createProgram0");
        mProgram= OpenGL32Util.createProgram(vShaderStr,fShaderStr);
        OpenGL32Util.checkGlError("createProgram");
        positionAtt= GLES32.glGetAttribLocation(mProgram,"a_position");
        textureAtt= GLES32.glGetAttribLocation(mProgram,"a_color");
        OpenGL32Util.checkGlError("GetAttrib");
        FloatBuffer verticesBuf= (FloatBuffer) createBuffer(vertices);
        ShortBuffer indicesBuf= (ShortBuffer) createBuffer(indices);

        GLES32.glGenBuffers(2, vboIds,0);
        Log.e(TAG, "init: vboId ="+vboIds[0]+","+vboIds[1]);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0]);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, 4*vertices.length,verticesBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("glBufferData0");

        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, 2*indices.length, indicesBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("glBufferData1");

        // Generate VAO Id
        GLES32.glGenVertexArrays(1, vaoId,0);
        Log.e(TAG, "init: vaoId ="+vaoId[0]);
        GLES32.glBindVertexArray(vaoId[0]);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0]);
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);

        OpenGL32Util.checkGlError("glBindBuffer");

        GLES32.glEnableVertexAttribArray(positionAtt);
        GLES32.glEnableVertexAttribArray(textureAtt);

        GLES32.glVertexAttribPointer(positionAtt, VERTEX_POS_SIZE, GLES32.GL_FLOAT, false, VERTEX_STRIDE,0);
        GLES32.glVertexAttribPointer(textureAtt, VERTEX_COLOR_SIZE, GLES32.GL_FLOAT, false, VERTEX_STRIDE, 4*VERTEX_POS_SIZE);

        OpenGL32Util.checkGlError("glVertexAttribPointer");

        GLES32.glBindVertexArray(GLES32.GL_NONE);
        isInited=true;
    }

    @Override
    public void draw(int screenW, int screenH) {
        if(mProgram == 0) return;

        GLES32.glUseProgram(mProgram);
        GLES32.glBindVertexArray(vaoId[0]);
        OpenGL32Util.checkGlError("glBindVertexArray");
        // Draw with the VAO settings
        GLES32.glDrawElements( GLES32.GL_TRIANGLES, 6,  GLES32.GL_UNSIGNED_SHORT, 0);

        // Return to the default VAO
        GLES32.glBindVertexArray( GLES32.GL_NONE);
    }

    @Override
    public void destroy() {
        if (mProgram!=0)
        {
            GLES32.glDeleteProgram(mProgram);
            GLES32.glDeleteBuffers(2, (IntBuffer) createBuffer(vboIds));
            GLES32.glDeleteVertexArrays(1, (IntBuffer) createBuffer(vaoId));
            mProgram = GLES32.GL_NONE;
        }
    }
}
