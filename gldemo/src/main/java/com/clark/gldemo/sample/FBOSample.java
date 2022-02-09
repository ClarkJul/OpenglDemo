package com.clark.gldemo.sample;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import com.clark.gldemo.NativeImage;
import com.clark.gldemo.utils.OpenGL32Util;
import com.clark.gldemo.utils.OpenGLUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class FBOSample extends GLSampleBase {

    String vShaderStr =
             "attribute vec4 a_position;"
           + "attribute vec2 a_texCoord;"
           + "varying vec2 v_texCoord;"
           + "void main()"
           + "{"
           + "gl_Position = a_position;"
           + "v_texCoord = a_texCoord;"
           + "}";

    // 用于普通渲染的片段着色器脚本，简单纹理映射
    String fShaderStr =
            "precision mediump float;"
            + "varying vec2 v_texCoord;"
            + "uniform sampler2D s_TextureMap;"
            + "void main()"
            + "{"
            + "gl_FragColor = texture2D(s_TextureMap, v_texCoord);"
            + "}";

    // 用于离屏渲染的片段着色器脚本，取每个像素的灰度值
    String fFboShaderStr =
            "precision mediump float;"
            + "varying vec2 v_texCoord;"
            + "uniform sampler2D s_TextureMap;"
            + "void main()"
            + "{"
            + "vec4 tempColor = texture2D(s_TextureMap, v_texCoord);"
            + "float luminance = tempColor.r * 0.299 + tempColor.g * 0.587 + tempColor.b * 0.114;"
            + "gl_FragColor = vec4(vec3(luminance), tempColor.a);"
            + "}"; // 输出灰度图


    //顶点坐标
    final float[] vVertices = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
    };

    //正常纹理坐标
    final float[] vTexCoors = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    //fbo 纹理坐标与正常纹理方向不同，原点位于左下角
    final float[] vFboTexCoors = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    final short[] indices = {0, 1, 2, 1, 3, 2};

    int m_ImageTextureId;
    int m_FboTextureId;
    int m_FboId;
    int[] vaoIds = {0, 0};
    int[] vboIds = {0, 0, 0, 0};
    int m_SamplerLoc;
    int m_FboProgramObj;
    int m_FboSamplerLoc;


    private int positionAtt;
    private int textureAtt;

    private int positionAttFbo;
    private int textureAttFbo;

    private NativeImage renderImage;

    @Override
    public void init() {
        glProgram = OpenGL32Util.createProgram(vShaderStr, fShaderStr);
        m_FboProgramObj = OpenGL32Util.createProgram(vShaderStr, fFboShaderStr);

        if (glProgram == GLES20.GL_NONE || m_FboProgramObj == GLES20.GL_NONE) {
            Log.e(TAG, "init: glProgram or m_FboProgramObj is none");
            return;
        }

        positionAtt= GLES32.glGetAttribLocation(glProgram,"a_position");
        textureAtt= GLES32.glGetAttribLocation(glProgram,"a_texCoord");

        positionAttFbo= GLES32.glGetAttribLocation(m_FboProgramObj,"a_position");
        textureAttFbo= GLES32.glGetAttribLocation(m_FboProgramObj,"a_texCoord");

        m_SamplerLoc = GLES32.glGetUniformLocation(glProgram, "s_TextureMap");
        m_FboSamplerLoc = GLES32.glGetUniformLocation(m_FboProgramObj, "s_TextureMap");

        FloatBuffer verticesBuf = (FloatBuffer) createBuffer(vVertices);
        FloatBuffer texCoorsBuf = (FloatBuffer) createBuffer(vTexCoors);
        FloatBuffer fboTexCoorsBuf = (FloatBuffer) createBuffer(vFboTexCoors);
        ShortBuffer indicesBuf = (ShortBuffer) createBuffer(indices);

        GLES32.glGenBuffers(4, vboIds,0);
        Log.e(TAG, "init: vboIds="+vboIds[0]+","+vboIds[1]+","+vboIds[2]+","+vboIds[3]);
        OpenGL32Util.checkGlError("vboIds init");
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0]);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, 4 * vVertices.length, verticesBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("vboIds init0");
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[1]);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, 4 * vTexCoors.length, texCoorsBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("vboIds init1");
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[2]);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, 4 * vFboTexCoors.length, fboTexCoorsBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("vboIds init2");
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, vboIds[3]);
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, 2 * indices.length, indicesBuf, GLES32.GL_STATIC_DRAW);
        OpenGL32Util.checkGlError("vboIds init3");

        GLES32.glGenVertexArrays(2, vaoIds,0);

        // 初始化用于普通渲染的 VAO
        // Normal rendering VAO
        GLES32.glBindVertexArray(vaoIds[0]);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0]);
        GLES32.glEnableVertexAttribArray(positionAtt);
        GLES32.glVertexAttribPointer(positionAtt, 3, GLES32.GL_FLOAT, false, 3 * 4, 0);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_NONE);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[1]);
        GLES32.glEnableVertexAttribArray(textureAtt);
        GLES32.glVertexAttribPointer(textureAtt, 2, GLES32.GL_FLOAT, false, 2 * 4, 0);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_NONE);

        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, vboIds[3]);
        OpenGL32Util.checkGlError("Normal rendering VAO");
        GLES32.glBindVertexArray(GLES32.GL_NONE);


        // 初始化用于离屏渲染的 VAO
        // FBO off screen rendering VAO
        GLES32.glBindVertexArray(vaoIds[1]);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0]);
        GLES32.glEnableVertexAttribArray(positionAttFbo);
        GLES32.glVertexAttribPointer(positionAttFbo, 3, GLES32.GL_FLOAT, false, 3 * 4, 0);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_NONE);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[2]);
        GLES32.glEnableVertexAttribArray(textureAttFbo);
        GLES32.glVertexAttribPointer(textureAttFbo, 2, GLES32.GL_FLOAT, false, 2 * 4, 0);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, GLES32.GL_NONE);

        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, vboIds[3]);

        GLES32.glBindVertexArray(GLES32.GL_NONE);


        // 创建并初始化 FBO
        int[] fboId = new int[1];
        GLES32.glGenFramebuffers(1, fboId, 0);
        m_FboId = fboId[0];
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, m_FboId);

        // 创建并初始化 FBO 纹理
        m_FboTextureId =OpenGL32Util.glGenTexture();
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, m_FboTextureId);

        //指定FBO纹理的输出图像的格式 RGBA
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, renderImage.width, renderImage.height, 0, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, null);

        //将fbo绑定到2d的纹理上
        GLES32.glFramebufferTexture2D(GLES32.GL_FRAMEBUFFER, GLES32.GL_COLOR_ATTACHMENT0, GLES32.GL_TEXTURE_2D, m_FboTextureId, 0);

        if (GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER) != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "init: frame buffer create fail");
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, GLES32.GL_NONE);


        // 创建并初始化图像纹理
        m_ImageTextureId=OpenGL32Util.glGenTexture();
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, m_ImageTextureId);
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, renderImage.width, renderImage.height, 0, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, renderImage.ppPlane[0].position(0));
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);

        isInited=true;
    }

    @Override
    public void draw(int screenW, int screenH) {
        // 离屏渲染
        GLES32.glPixelStorei(GLES32.GL_UNPACK_ALIGNMENT, 1);
        GLES32.glViewport(0, 0, renderImage.width, renderImage.height);

        // Do FBO off screen rendering
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, m_FboId);
        GLES32.glUseProgram(m_FboProgramObj);
        GLES32.glUniform1i(m_FboSamplerLoc, 0);
        GLES32.glBindVertexArray(vaoIds[1]);

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, m_ImageTextureId);
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_SHORT, 0);

        GLES32.glBindVertexArray(GLES32.GL_NONE);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, GLES32.GL_NONE);

        // 普通渲染
        // Do normal rendering
        GLES32.glViewport(0, 0, screenW, screenH);
        GLES32.glUseProgram(glProgram);
        GLES32.glUniform1i(m_SamplerLoc, 0);
        GLES32.glBindVertexArray(vaoIds[0]);

        /*GLES32.glUseProgram(m_FboProgramObj);
        GLES32.glUniform1i(m_FboSamplerLoc, 0);
        GLES32.glBindVertexArray(vaoIds[1]);*/

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, m_FboTextureId);
//        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, m_ImageTextureId);
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, 6, GLES32.GL_UNSIGNED_SHORT, 0);

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
        GLES32.glBindVertexArray(GLES32.GL_NONE);
    }

    @Override
    public void destroy() {
        if (glProgram != 0) {
            GLES32.glDeleteProgram(glProgram);
        }

        if (m_FboProgramObj != 0) {
            GLES32.glDeleteProgram(m_FboProgramObj);
        }

        if (m_ImageTextureId != 0) {
            GLES32.glDeleteTextures(1, new int[]{m_ImageTextureId}, 0);
        }

        if (m_FboTextureId != 0) {
            GLES32.glDeleteTextures(1, new int[]{m_FboTextureId}, 0);
        }

        if (vboIds[0] != 0) {
            GLES32.glDeleteBuffers(4, vboIds, 0);
        }

        if (vaoIds[0] != 0) {
            GLES32.glDeleteVertexArrays(2, vaoIds, 0);
        }

        if (m_FboId != 0) {
            GLES32.glDeleteFramebuffers(1, new int[]{m_FboId}, 0);
        }
    }

    @Override
    public void loadImage(NativeImage pImage) {
        renderImage = new NativeImage();
        renderImage.format = pImage.format;
        renderImage.width = pImage.width;
        renderImage.height = pImage.height;
        copyNativeImage(pImage, renderImage);
        Log.e(TAG, "loadImage: "+renderImage.toString() );
    }

    boolean createFrameBufferObj() {

        return true;
    }

}
