package com.clark.gldemo;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES20;
import android.util.Log;

import com.clark.gldemo.sample.FBOSample;
import com.clark.gldemo.sample.GLSampleBase;
import com.clark.gldemo.sample.NV21TextureMapSample;
import com.clark.gldemo.sample.TextureMapSample;
import com.clark.gldemo.sample.TriangleSample;
import com.clark.gldemo.sample.VAOVBOSample;
import com.clark.gldemo.utils.OpenGLUtil;

import java.util.Arrays;

/**
 * @author Clark
 * 2022/1/22 17:33
 */
public class MyGLRenderContext {
    private static final String TAG = "MyGLRenderContext";

    public static MyGLRenderContext getInstanse() {
        //返回静态内部类中的静态变量实例
        return SingletonHolder.manager;
    }

    private static class SingletonHolder {
        private static MyGLRenderContext manager = new MyGLRenderContext();
    }

    GLSampleBase m_pBeforeSample;
    GLSampleBase m_pCurSample;
    int m_ScreenW;
    int m_ScreenH;

    public MyGLRenderContext() {
        m_pCurSample = new TriangleSample();
        m_pBeforeSample = null;
    }

    public void setImageData(Bitmap bitmap) {
        if (m_pCurSample != null) {
            m_pCurSample.loadImage(bitmap);
        }
    }

    public void setImageData(int format, int width, int height, byte[] pData) {
        NativeImage nativeImage = new NativeImage();
        nativeImage.format = format;
        nativeImage.width = width;
        nativeImage.height = height;

        Log.e(TAG, "setImageData: format="+format+",width=" +width+",height="+height);
        switch (format) {
            case Constant.IMAGE.FORMAT_NV12:
            case Constant.IMAGE.FORMAT_NV21:
                nativeImage.ppPlane[0] = OpenGLUtil.createByteBuffer(pData, 0, width * height);
                nativeImage.ppPlane[1] = OpenGLUtil.createByteBuffer(pData, width * height, width * height / 2);//Arrays.copyOfRange(nativeImage.ppPlane[0], width * height, nativeImage.ppPlane[0].length);
                break;
            case Constant.IMAGE.FORMAT_I420:
                nativeImage.ppPlane[0] = OpenGLUtil.createByteBuffer(pData, 0, width * height);
                nativeImage.ppPlane[1] = OpenGLUtil.createByteBuffer(pData, width * height, width * height / 4);
                nativeImage.ppPlane[2] = OpenGLUtil.createByteBuffer(pData, width * height * 5 / 4, width * height / 4);
                break;
            default:
                nativeImage.ppPlane[0] = OpenGLUtil.createByteBuffer(pData, 0, pData.length);
                break;
        }

        if (m_pCurSample != null) {
            m_pCurSample.loadImage(nativeImage);
        }

    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] pData) {

        NativeImage nativeImage = new NativeImage();
        nativeImage.format = format;
        nativeImage.width = width;
        nativeImage.height = height;
        nativeImage.ppPlane[0] = OpenGLUtil.createByteBuffer(pData, 0, width * height);

        switch (format) {
            case Constant.IMAGE.FORMAT_NV12:
            case Constant.IMAGE.FORMAT_NV21:
                nativeImage.ppPlane[1] = OpenGLUtil.createByteBuffer(pData, width * height, pData.length);//Arrays.copyOfRange(nativeImage.ppPlane[0], width * height, nativeImage.ppPlane[0].length);
                break;
            case Constant.IMAGE.FORMAT_I420:
                nativeImage.ppPlane[1] = OpenGLUtil.createByteBuffer(pData, width * height, width * height / 4);
                nativeImage.ppPlane[2] = OpenGLUtil.createByteBuffer(pData, width * height * 5 / 4, width * height / 4);
                break;
            default:
                break;
        }

        if (m_pCurSample != null) {
            m_pCurSample.loadMultiImageWithIndex(index, nativeImage);
        }

    }

    public void setParamsInit(int paramType, int value0, int value1) {
        Log.e(TAG, "setParamsInit: paramType=" + paramType + ",value0=" + value0);
        if (paramType == Constant.SAMPLE_TYPE.SAMPLE_TYPE) {
            m_pBeforeSample = m_pCurSample;
            switch (value0) {
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRIANGLE:
                    Log.e(TAG, "setParamsInit:SAMPLE_TYPE_TEXTURE_MAP");
                    m_pCurSample = new TriangleSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXTURE_MAP:
                    Log.e(TAG, "setParamsInit:SAMPLE_TYPE_TEXTURE_MAP");
                    m_pCurSample = new TextureMapSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_YUV_TEXTURE_MAP:
                    m_pCurSample = new NV21TextureMapSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VAO:
                    m_pCurSample = new VAOVBOSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO:
                    m_pCurSample = new FBOSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO_LEG:
                    //m_pCurSample = new FBOLegLengthenSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_COORD_SYSTEM:
                    //m_pCurSample = new CoordSystemSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BASIC_LIGHTING:
                    //m_pCurSample = new BasicLightingSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANS_FEEDBACK:
                    //m_pCurSample = new TransformFeedbackSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MULTI_LIGHTS:
                    // m_pCurSample = new MultiLightsSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_DEPTH_TESTING:
                    // m_pCurSample = new DepthTestingSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_INSTANCING:
                    //m_pCurSample = new Instancing3DSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_STENCIL_TESTING:
                    //m_pCurSample = new StencilTestingSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BLENDING:
                    //m_pCurSample = new BlendingSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PARTICLES:
                    //m_pCurSample = new ParticlesSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SKYBOX:
                    //m_pCurSample = new SkyBoxSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_3D_MODEL:
                    //m_pCurSample = new Model3DSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PBO:
                    //m_pCurSample = new PBOSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BEATING_HEART:
                    //m_pCurSample = new BeatingHeartSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_CLOUD:
                    //m_pCurSample = new CloudSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TIME_TUNNEL:
                    //m_pCurSample = new TimeTunnelSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BEZIER_CURVE:
                    //m_pCurSample = new BezierCurveSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BIG_EYES:
                    //m_pCurSample = new BigEyesSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FACE_SLENDER:
                    //m_pCurSample = new FaceSlenderSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BIG_HEAD:
                    //m_pCurSample = new BigHeadSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_ROTARY_HEAD:
                    //m_pCurSample = new RotaryHeadSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VISUALIZE_AUDIO:
                    // m_pCurSample = new VisualizeAudioSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SCRATCH_CARD:
                    // m_pCurSample = new ScratchCardSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_AVATAR:
                    //m_pCurSample = new AvatarSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SHOCK_WAVE:
                    //m_pCurSample = new ShockWaveSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MRT:
                    //m_pCurSample = new MRTSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO_BLIT:
                    //m_pCurSample = new FBOBlitSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TBO:
                    // m_pCurSample = new TextureBufferSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_UBO:
                    // m_pCurSample = new UniformBufferSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2YUYV:
                    //m_pCurSample = new RGB2YUYVSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MULTI_THREAD_RENDER:
                    // m_pCurSample = new SharedEGLContextSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXT_RENDER:
                    // m_pCurSample = new TextRenderSample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_STAY_COLOR:
                    // m_pCurSample = new PortraitStayColorExample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_1:
                    // m_pCurSample = new GLTransitionExample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_2:
                    //m_pCurSample = new GLTransitionExample_2();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_3:
                    // m_pCurSample = new GLTransitionExample_3();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANSITIONS_4:
                    //m_pCurSample = new GLTransitionExample_4();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2NV21:
                    //m_pCurSample = new RGB2NV21Sample();
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_RGB2I420:
                    //m_pCurSample = new RGB2I420Sample();
                    break;
                default:
                    m_pCurSample = null;
                    break;
            }
        }
    }

    public void setParamsFloat(int paramType, float value0, float value1) {

        if (m_pCurSample != null) {
            switch (paramType) {
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SET_TOUCH_LOC:
                    m_pCurSample.setTouchLocation(value0, value1);
                    break;
                case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SET_GRAVITY_XY:
                    m_pCurSample.setGravityXY(value0, value1);
                    break;
                default:
                    break;

            }
        }
    }

    public void setParamsShortArr(short[] pShortArr) {
        if (m_pCurSample != null) {
            m_pCurSample.loadShortArrData(pShortArr);
        }

    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
        if (m_pCurSample != null) {
            m_pCurSample.updateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
        }
    }

    public void onSurfaceCreated() {
        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        m_ScreenW = width;
        m_ScreenH = height;
    }

    public void onDrawFrame() {
        Log.e(TAG, "onDrawFrame: ");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        if (m_pBeforeSample != null) {
            m_pBeforeSample.destroy();
            m_pBeforeSample = null;
        }
        if (m_pCurSample != null) {
            if (!m_pCurSample.isInited) m_pCurSample.init();
            m_pCurSample.draw(m_ScreenW, m_ScreenH);
        }
    }

    public void destroyInstance() {
        Log.e("MyGLRenderContext", " destroyInstance");
    }
}
