package com.clark.gldemo;

/**
 * @author Clark
 * 2022/1/22 18:35
 */
class Constant {
    public interface IMAGE {
        final int FORMAT_RGBA = 0x01;
        final int FORMAT_NV21 = 0x02;
        final int FORMAT_NV12 = 0x03;
        final int FORMAT_I420 = 0x04;
        final int FORMAT_YUYV = 0x05;
        final int FORMAT_GRAY = 0x06;
        final int FORMAT_I444 = 0x07;
        final int FORMAT_P010 = 0x08;
    }

    public interface SAMPLE_TYPE {
        final int SAMPLE_TYPE = 200;
        final int SAMPLE_TYPE_TRIANGLE = SAMPLE_TYPE;
        final int SAMPLE_TYPE_TEXTURE_MAP = SAMPLE_TYPE + 1;
        final int SAMPLE_TYPE_YUV_TEXTURE_MAP = SAMPLE_TYPE + 2;
        final int SAMPLE_TYPE_VAO = SAMPLE_TYPE + 3;
        final int SAMPLE_TYPE_FBO = SAMPLE_TYPE + 4;
        final int SAMPLE_TYPE_EGL = SAMPLE_TYPE + 5;
        final int SAMPLE_TYPE_FBO_LEG = SAMPLE_TYPE + 6;
        final int SAMPLE_TYPE_COORD_SYSTEM = SAMPLE_TYPE + 7;
        final int SAMPLE_TYPE_BASIC_LIGHTING = SAMPLE_TYPE + 8;
        final int SAMPLE_TYPE_TRANS_FEEDBACK = SAMPLE_TYPE + 9;
        final int SAMPLE_TYPE_MULTI_LIGHTS = SAMPLE_TYPE + 10;
        final int SAMPLE_TYPE_DEPTH_TESTING = SAMPLE_TYPE + 11;
        final int SAMPLE_TYPE_INSTANCING = SAMPLE_TYPE + 12;
        final int SAMPLE_TYPE_STENCIL_TESTING = SAMPLE_TYPE + 13;
        final int SAMPLE_TYPE_BLENDING = SAMPLE_TYPE + 14;
        final int SAMPLE_TYPE_PARTICLES = SAMPLE_TYPE + 15;
        final int SAMPLE_TYPE_SKYBOX = SAMPLE_TYPE + 16;
        final int SAMPLE_TYPE_3D_MODEL = SAMPLE_TYPE + 17;
        final int SAMPLE_TYPE_PBO = SAMPLE_TYPE + 18;
        final int SAMPLE_TYPE_BEATING_HEART = SAMPLE_TYPE + 19;
        final int SAMPLE_TYPE_CLOUD = SAMPLE_TYPE + 20;
        final int SAMPLE_TYPE_TIME_TUNNEL = SAMPLE_TYPE + 21;
        final int SAMPLE_TYPE_BEZIER_CURVE = SAMPLE_TYPE + 22;
        final int SAMPLE_TYPE_BIG_EYES = SAMPLE_TYPE + 23;
        final int SAMPLE_TYPE_FACE_SLENDER = SAMPLE_TYPE + 24;
        final int SAMPLE_TYPE_BIG_HEAD = SAMPLE_TYPE + 25;
        final int SAMPLE_TYPE_ROTARY_HEAD = SAMPLE_TYPE + 26;
        final int SAMPLE_TYPE_VISUALIZE_AUDIO = SAMPLE_TYPE + 27;
        final int SAMPLE_TYPE_SCRATCH_CARD = SAMPLE_TYPE + 28;
        final int SAMPLE_TYPE_AVATAR = SAMPLE_TYPE + 29;
        final int SAMPLE_TYPE_SHOCK_WAVE = SAMPLE_TYPE + 30;
        final int SAMPLE_TYPE_MRT = SAMPLE_TYPE + 31;
        final int SAMPLE_TYPE_FBO_BLIT = SAMPLE_TYPE + 32;
        final int SAMPLE_TYPE_TBO = SAMPLE_TYPE + 33;
        final int SAMPLE_TYPE_UBO = SAMPLE_TYPE + 34;
        final int SAMPLE_TYPE_RGB2YUYV = SAMPLE_TYPE + 35;
        final int SAMPLE_TYPE_MULTI_THREAD_RENDER = SAMPLE_TYPE + 36;
        final int SAMPLE_TYPE_TEXT_RENDER = SAMPLE_TYPE + 37;
        final int SAMPLE_TYPE_STAY_COLOR = SAMPLE_TYPE + 38;
        final int SAMPLE_TYPE_TRANSITIONS_1 = SAMPLE_TYPE + 39;
        final int SAMPLE_TYPE_TRANSITIONS_2 = SAMPLE_TYPE + 40;
        final int SAMPLE_TYPE_TRANSITIONS_3 = SAMPLE_TYPE + 41;
        final int SAMPLE_TYPE_TRANSITIONS_4 = SAMPLE_TYPE + 42;
        final int SAMPLE_TYPE_RGB2NV21 = SAMPLE_TYPE + 43;
        final int SAMPLE_TYPE_RGB2I420 = SAMPLE_TYPE + 44;
        final int SAMPLE_TYPE_RGB2I444 = SAMPLE_TYPE + 45;
        final int SAMPLE_TYPE_HWBuffer = SAMPLE_TYPE + 46;
        final int SAMPLE_TYPE_SET_TOUCH_LOC = SAMPLE_TYPE + 999;
        final int SAMPLE_TYPE_SET_GRAVITY_XY = SAMPLE_TYPE + 1000;
    }
}
