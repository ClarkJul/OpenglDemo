package com.clark.gldemo.sample;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.clark.gldemo.NativeImage;
import com.clark.gldemo.utils.OpenGLUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class NV21TextureMapSample extends GLSampleBase {

    final String sVertexShader=
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
           +"varying vec2 v_texCoord;"
           +"uniform sampler2D y_texture;"
           +"uniform sampler2D uv_texture;"
           +"void main()"
           +"{"
           /*+"vec3 yuv;"*/
           +"float y = texture2D(y_texture, v_texCoord).r;"
           +"float u = texture2D(uv_texture, v_texCoord).a-0.5;"
           +"float v = texture2D(uv_texture, v_texCoord).r-0.5;"
           /*+"highp vec3 rgb = mat3(1,1,1,0,-0.344,1.770,1.403,-0.714,0) * yuv;"*/
           +"float r = y + 1.403*v;"
           +"float g = y - 0.344*u - 0.714*v;"
           +"float b = y + 1.770*u;"
           +"gl_FragColor = vec4(r, g, b, 1.0);"
           +"}";

    private final float[] verticesCoords = {
            -1.0f,  0.78f, 0.0f,  // Position 0
            -1.0f, -0.78f, 0.0f,  // Position 1
            1.0f,  -0.78f, 0.0f,  // Position 2
            1.0f,   0.78f, 0.0f,  // Position 3
    };

    private final float[] textureCoords = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    private final short[] indices = {0, 1, 2, 0, 2, 3};

    private int ytextureId;
    private int uvtextureId;
    private int ySamplerLoc;
    private int uvSamplerLoc;
    private int positionAtt;
    private int textureAtt;

    private FloatBuffer verticesBuffer ;
    private FloatBuffer textureBuffer ;
    private ShortBuffer indicesBuffer ;

    private NativeImage renderImage;


    public NV21TextureMapSample() {
        verticesBuffer = (FloatBuffer) createBuffer(verticesCoords);
        textureBuffer = (FloatBuffer) createBuffer(textureCoords);
        indicesBuffer = (ShortBuffer) createBuffer(indices);
    }

    @Override
    public void loadImage(NativeImage pImage) {
        renderImage=new NativeImage();
        renderImage.format=pImage.format;
        renderImage.width=pImage.width;
        renderImage.height=pImage.height;
        copyNativeImage(pImage,renderImage);
    }

    @Override
    public void init() {
        glProgram = OpenGLUtil.createProgram(sVertexShader,sFragmentShader);
        if (glProgram != 0) {
            ySamplerLoc = GLES20.glGetUniformLocation(glProgram, "y_texture");
            uvSamplerLoc = GLES20.glGetUniformLocation(glProgram, "uv_texture");
            positionAtt = GLES20.glGetAttribLocation(glProgram, "a_position");
            textureAtt = GLES20.glGetAttribLocation(glProgram, "a_texCoord");
        } else {
            Log.e(TAG, "TextureMapSample Init create program fail");
        }
        int[] textures=new int[2];
        OpenGLUtil.glGenTextures(textures);
        ytextureId =textures[0];
        uvtextureId=textures[1];
    }

    @Override
    public void draw(int screenW, int screenH) {
        GLES20.glUseProgram(glProgram);
        // Load the vertex position
        GLES20.glEnableVertexAttribArray(positionAtt);
        GLES20.glVertexAttribPointer(positionAtt, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        // Load the texture coordinate
        GLES20.glEnableVertexAttribArray(textureAtt);
        GLES20.glVertexAttribPointer(textureAtt, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glUniform1i(ySamplerLoc, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ytextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,GLES20. GL_LUMINANCE, renderImage.width, renderImage.height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, renderImage.ppPlane[0].position(0));


        GLES20.glUniform1i(uvSamplerLoc, 1);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvtextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,GLES20. GL_LUMINANCE_ALPHA, renderImage.width>>1, renderImage.height>>1, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, renderImage.ppPlane[1].position(0));


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
    }

    @Override
    public void destroy() {

    }
}
