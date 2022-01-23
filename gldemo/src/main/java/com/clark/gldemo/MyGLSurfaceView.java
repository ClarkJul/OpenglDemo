package com.clark.gldemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class MyGLSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "MyGLSurfaceView";

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private float mPreviousY;
    private float mPreviousX;
    private int mXAngle;
    private int mYAngle;

    private MyGLRender mGLRender;

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mPreScale = 1.0f;
    private float mCurScale = 1.0f;
    private long mLastMultiTouchTime;

//    public MyGLSurfaceView(Context context) {
//        this(context, null);
//    }
//
//    public MyGLSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.setEGLContextClientVersion(2);
//        mGLRender = new MyGLRender();
//        /*If no setEGLConfigChooser method is called,
//        then by default the view will choose an RGB_888 surface with a depth buffer depth of at least 16 bits.*/
//        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
//        setRenderer(mGLRender);
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
//        mScaleGestureDetector = new ScaleGestureDetector(context, this);
//
//    }

    public MyGLSurfaceView(Context context, MyGLRender glRender) {
        this(context, glRender, null);
    }

    public MyGLSurfaceView(Context context, MyGLRender glRender, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2);
        mGLRender = glRender;
        /*If no setEGLConfigChooser method is called,
        then by default the view will choose an RGB_888 surface with a depth buffer depth of at least 16 bits.*/
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(mGLRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getPointerCount() == 1) {
            consumeTouchEvent(e);
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - mLastMultiTouchTime > 200)
            {
                float y = e.getY();
                float x = e.getX();
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float dy = y - mPreviousY;
                        float dx = x - mPreviousX;
                        mYAngle += dx * TOUCH_SCALE_FACTOR;
                        mXAngle += dy * TOUCH_SCALE_FACTOR;
                }
                mPreviousY = y;
                mPreviousX = x;

                switch (mGLRender.getSampleType()) {
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_FBO_LEG:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_COORD_SYSTEM:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BASIC_LIGHTING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TRANS_FEEDBACK:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_MULTI_LIGHTS:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_DEPTH_TESTING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_INSTANCING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_STENCIL_TESTING:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PARTICLES:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SKYBOX:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_3D_MODEL:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_PBO:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VISUALIZE_AUDIO:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_UBO:
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXT_RENDER:
                        mGLRender.updateTransformMatrix(mXAngle, mYAngle, mCurScale, mCurScale);
                        requestRender();
                        break;
                    default:
                        break;
                }
            }

        } else {
            mScaleGestureDetector.onTouchEvent(e);
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    public void setAspectRatio(int width, int height) {
        Log.d(TAG, "setAspectRatio() called with: width = [" + width + "], height = [" + height + "]");
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    public MyGLRender getGLRender() {
        return mGLRender;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        switch (mGLRender.getSampleType()) {
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_COORD_SYSTEM:
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_BASIC_LIGHTING:
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_INSTANCING:
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_3D_MODEL:
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_VISUALIZE_AUDIO:
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_TEXT_RENDER:
            {
                float preSpan = detector.getPreviousSpan();
                float curSpan = detector.getCurrentSpan();
                if (curSpan < preSpan) {
                    mCurScale = mPreScale - (preSpan - curSpan) / 200;
                } else {
                    mCurScale = mPreScale + (curSpan - preSpan) / 200;
                }
                mCurScale = Math.max(0.05f, Math.min(mCurScale, 80.0f));
                mGLRender.updateTransformMatrix(mXAngle, mYAngle, mCurScale, mCurScale);
                requestRender();
            }
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        mPreScale = mCurScale;
        mLastMultiTouchTime = System.currentTimeMillis();

    }

    public void consumeTouchEvent(MotionEvent e) {
        dealClickEvent(e);
        float touchX = -1, touchY = -1;
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                touchX = e.getX();
                touchY = e.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchX = -1;
                touchY = -1;
                break;
            default:
                break;
        }

        //滑动、触摸
        switch (mGLRender.getSampleType()) {
            case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SCRATCH_CARD:
                mGLRender.setTouchLoc(touchX, touchY);
                requestRender();
                break;
            default:
                break;
        }

        //点击
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //touchX = e.getX();
                //touchY = e.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

    }

    public void dealClickEvent(MotionEvent e) {
        float touchX = -1, touchY = -1;
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                touchX = e.getX();
                touchY = e.getY();
            {
                //点击
                switch (mGLRender.getSampleType()) {
                    case Constant.SAMPLE_TYPE.SAMPLE_TYPE_SHOCK_WAVE:
                        mGLRender.setTouchLoc(touchX, touchY);
                        break;
                    default:
                        break;
                }
            }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MyGLRenderContext.getInstanse().destroyInstance();
    }
}