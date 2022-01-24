package com.clark.gldemo.utils;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Clark
 * 2022/1/23 13:09
 */
public class OpenGLUtil {
    private static final String TAG="OpenGLUtil::";

    public  static int createProgram(String vertShaderStr,String fragShaderStr) {
        int mvertexShaderHandle=createShapeHandle(vertShaderStr,GLES20.GL_VERTEX_SHADER);
        int mfragmentShaderHandle=createShapeHandle(fragShaderStr,GLES20.GL_FRAGMENT_SHADER);
        if (mvertexShaderHandle == 0 || mfragmentShaderHandle == 0) {
            throw new RuntimeException("Fail to create shader!");
        }
        //创建程序
        int mprogramHandle = GLES20.glCreateProgram();
        if (mprogramHandle != 0) {
            //将渲染器容器绑定到程序中
            GLES20.glAttachShader(mprogramHandle, mvertexShaderHandle);
            GLES20.glAttachShader(mprogramHandle, mfragmentShaderHandle);
            //链接程序
            GLES20.glLinkProgram(mprogramHandle);

            int status[] = new int[1];
            //获取链接结果
            GLES20.glGetProgramiv(mprogramHandle, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == 0) {
                GLES20.glDeleteProgram(mprogramHandle);
                mprogramHandle = 0;
            }
        }

        if (mprogramHandle == 0) {
            throw new RuntimeException("Fail to create program!");
        } else {
            Log.e(TAG, "Create program succussful!");
        }

        return mprogramHandle;
    }

    private static int createShapeHandle(String shaderStr, int type) {
        //创建渲染器容器
        int handle = GLES20.glCreateShader(type);
        if (handle != 0) {
            //将渲染器代码放入容器中
            GLES20.glShaderSource(handle, shaderStr);
            //编译
            GLES20.glCompileShader(handle);

            int status[] = new int[1];
            //获取编译结果
            GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                String ss = GLES20.glGetShaderInfoLog(handle);
                Log.e(TAG, ss);
                GLES20.glDeleteShader(handle);
                handle = 0;
            }
        }

        if (handle == 0) {
            if (type == GLES20.GL_VERTEX_SHADER) {
                throw new RuntimeException("Fail to create Vertex Shader!");
            } else if (type == GLES20.GL_FRAGMENT_SHADER) {
                throw new RuntimeException("Fail to create Fragement Shader!");
            }
        } else {
            if (type == GLES20.GL_VERTEX_SHADER) {
                Log.e(TAG, "Create vertex shader successful!");
            } else if (type == GLES20.GL_FRAGMENT_SHADER) {
                Log.e(TAG, "Create fragment shader successful!");
            }
        }

        return handle;
    }

    public static String readRawShaderFile(Context context, int shareId) {
        InputStream is = context.getResources().openRawResource(shareId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        StringBuffer sb = new StringBuffer();
        try {

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void glGenTextures(int[] textures) {
        GLES20.glGenTextures(textures.length, textures, 0);

        for (int i = 0; i < textures.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
        }
    }

    public static int glGenTexture() {
        int[] textures=new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
        return textures[0];
    }

    public static int genOESTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        while(error!=0) {
            Log.e("OpenGlUtils", op+"   checkGlError: error="+error);
            error =GLES20.glGetError();
        }
    }

    public static <T> Buffer createBuffer(T data) {
        Buffer res=null;
        //Log.e(TAG, "createBuffer: "+(data instanceof float[]) );
        if (data instanceof float[]){
            float[] input= (float[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            ((FloatBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof short[]){
            short[] input= (short[]) data;
            res = ByteBuffer.allocateDirect(input.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            ((ShortBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof int[]){
            int[] input= (int[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer();
            ((IntBuffer)res).put(input, 0, input.length).position(0);
        }else if (data instanceof byte[]){
            byte[] input= (byte[]) data;
            res = ByteBuffer.allocateDirect(input.length )
                    .order(ByteOrder.nativeOrder());
            ((ByteBuffer)res).put(input, 0, input.length).position(0);
        }
        return res;
    }
}
