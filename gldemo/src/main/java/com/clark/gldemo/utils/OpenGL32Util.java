package com.clark.gldemo.utils;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES32;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
public class OpenGL32Util {
    private static final String TAG = "OpenGL32Util::";

    public static int createProgram(String vertShaderStr, String fragShaderStr) {
        int mvertexShaderHandle = createShapeHandle(vertShaderStr, GLES32.GL_VERTEX_SHADER);
        checkGlError("GL_VERTEX_SHADER");
        int mfragmentShaderHandle = createShapeHandle(fragShaderStr, GLES32.GL_FRAGMENT_SHADER);
        checkGlError("GL_FRAGMENT_SHADER");
        if (mvertexShaderHandle == 0 || mfragmentShaderHandle == 0) {
            throw new RuntimeException("Fail to create shader!");
        }
        //创建程序
        int mprogramHandle = GLES32.glCreateProgram();
        if (mprogramHandle != 0) {
            //将渲染器容器绑定到程序中
            GLES32.glAttachShader(mprogramHandle, mvertexShaderHandle);
            GLES32.glAttachShader(mprogramHandle, mfragmentShaderHandle);
            //链接程序
            GLES32.glLinkProgram(mprogramHandle);

            int status[] = new int[1];
            //获取链接结果
            GLES32.glGetProgramiv(mprogramHandle, GLES32.GL_LINK_STATUS, status, 0);
            if (status[0] == 0) {
                GLES32.glDeleteProgram(mprogramHandle);
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
        int handle = GLES32.glCreateShader(type);
        checkGlError("glCreateShader");
        if (handle != 0) {
            //将渲染器代码放入容器中
            GLES32.glShaderSource(handle, shaderStr);
            //编译
            GLES32.glCompileShader(handle);

            int status[] = new int[1];
            //获取编译结果
            GLES32.glGetShaderiv(handle, GLES32.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                String ss = GLES32.glGetShaderInfoLog(handle);
                Log.e(TAG, ss);
                GLES32.glDeleteShader(handle);
                handle = 0;
            }
        }

        if (handle == 0) {
            if (type == GLES32.GL_VERTEX_SHADER) {
                throw new RuntimeException("Fail to create Vertex Shader!");
            } else if (type == GLES32.GL_FRAGMENT_SHADER) {
                throw new RuntimeException("Fail to create Fragement Shader!");
            }
        } else {
            if (type == GLES32.GL_VERTEX_SHADER) {
                Log.e(TAG, "Create vertex shader successful!");
            } else if (type == GLES32.GL_FRAGMENT_SHADER) {
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
        GLES32.glGenTextures(textures.length, textures, 0);

        for (int i = 0; i < textures.length; i++) {
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[i]);

            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR);
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);

            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
        }
    }

    public static int glGenTexture() {
        int[] textures = new int[1];
        GLES32.glGenTextures(1, textures, 0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textures[0]);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
        return textures[0];
    }

    public static int genOESTexture() {
        int[] texture = new int[1];
        GLES32.glGenTextures(1, texture, 0);
        GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES32.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES32.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES32.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public static void checkGlError(String op) {
        int error = GLES32.glGetError();
        while (error != 0) {
            Log.e(TAG, op + "   checkGlError: error=" + error);
            error = GLES32.glGetError();
        }
    }

    public static <T> Buffer createBuffer(T data) {
        Buffer res = null;
        //Log.e(TAG, "createBuffer: "+(data instanceof float[]) );
        if (data instanceof float[]) {
            float[] input = (float[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            ((FloatBuffer) res).put(input, 0, input.length).position(0);
        } else if (data instanceof short[]) {
            short[] input = (short[]) data;
            res = ByteBuffer.allocateDirect(input.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            ((ShortBuffer) res).put(input, 0, input.length).position(0);
        } else if (data instanceof int[]) {
            int[] input = (int[]) data;
            res = ByteBuffer.allocateDirect(input.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asIntBuffer();
            ((IntBuffer) res).put(input, 0, input.length).position(0);
        } else if (data instanceof byte[]) {
            byte[] input = (byte[]) data;
            res = ByteBuffer.allocateDirect(input.length)
                    .order(ByteOrder.nativeOrder());
            ((ByteBuffer) res).put(input, 0, input.length).position(0);
        }
        return res;
    }

    public static ByteBuffer createByteBuffer(byte[] data, int offset,int length) {
        return ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder()).put(data, offset, length);
    }

    public static void writeByteToFile(String path,byte[] data){
        Log.e(TAG, "writeByteToFile: " );
        File mFile=new File("/storage/emulated/0/test",System.currentTimeMillis()+"_test");
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                    Log.e(TAG, "writeByteToFile: success!" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
