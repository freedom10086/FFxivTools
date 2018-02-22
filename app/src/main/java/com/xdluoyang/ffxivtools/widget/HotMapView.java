package com.xdluoyang.ffxivtools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.xdluoyang.ffxivtools.huntapi.HuntItem;

import java.util.List;

public class HotMapView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = HotMapView.class.getSimpleName();
    public static final float SCALE_MAX = 3.0f;

    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private float initScale = 1.0f;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix imageMatrix = new Matrix();
    /**
     * 用于双击检测
     */
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;

    private boolean isCheckTopAndBottom = true;
    private boolean isCheckLeftAndRight = true;

    private float stepX = 0, stepY = 0;
    private List<HuntItem> data;
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HotMapView(Context context) {
        this(context, null);
    }

    public HotMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);

        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(0xffff4900);

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();
                Log.e("DoubleTap", getScale() + " , " + initScale);
                if (getScale() < initScale * 1.5f) {
                    //postDelayed(); 16 ：多久实现一次的定时器操作
                    HotMapView.this.postDelayed(new AutoScaleRunnable(initScale * 1.5f, x, y), 16);
                    isAutoScale = true;
                } else {
                    HotMapView.this.postDelayed(new AutoScaleRunnable(initScale, x, y), 16);
                    isAutoScale = true;
                }

                return true;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    public void setData(List<HuntItem> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        Drawable d = getDrawable();
        if (d != null) {
            canvas.save();

            int width = d.getIntrinsicWidth();
            int height = d.getIntrinsicHeight();

            canvas.setMatrix(imageMatrix);

            d.draw(canvas);

            //super.onDraw(canvas);


            if (stepX == 0) {
                stepX = (float) (width * 1.0 / 42);
                stepY = (float) (height * 1.0 / 42);
            }

            if (data != null && data.size() > 0) {
                int max = 0;

                for (HuntItem item : data) {
                    if (item.Counts > max) {
                        max = item.Counts;
                    }
                }

                if (max > 0)
                    for (HuntItem item : data) {
                        float x = (item.X - 0.5f) * stepX;
                        float y = (item.Y - 0.5f) * stepY;

                        int alpha = (int) (item.Counts * 1.0f / max * 255 * 0.85); //* 0.9 不让最大值不透明
                        if (alpha < 26) alpha = 26;
                        rectPaint.setAlpha(alpha);

                        canvas.drawRect(x - 0.5f * stepX, y - 0.5f * stepY, x + 0.5f * stepX, y + 0.5f * stepY, rectPaint);
                    }
            }

            canvas.restore();
        }
    }

    /**
     * 自动缩放的任务
     *
     * @author zhy
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            imageMatrix.postScale(tmpScale, tmpScale, x, y);

            checkBorderAndCenterWhenScale();
            //setImageMatrix(imageMatrix);
            invalidate();

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale)) || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                HotMapView.this.postDelayed(this, 16);
            } else {
                // 设置为目标的缩放比例
                final float deltaScale = mTargetScale / currentScale;
                imageMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();

                invalidate();
                isAutoScale = false;
            }
        }
    }

    /**
     * 对图片进行缩放的控制，首先进行缩放范围的判断，然后设置mScaleMatrix的scale值
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Drawable d = getDrawable();
        if (d == null) return true;

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        /**
         * 缩放的范围控制
         */
        if (scaleFactor * scale < initScale) {
            scaleFactor = initScale / scale;
        }

        if (scaleFactor * scale > initScale * SCALE_MAX) {
            scaleFactor = initScale * SCALE_MAX / scale;
        }

        imageMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
        checkBorderAndCenterWhenScale();
        //setImageMatrix(imageMatrix);
        invalidate();

        return true;
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        final float left = getX();
        final float top = getY();
        final float right = getWidth() + left;
        final float bottom = getHeight() + top;


        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= getWidth()) {
            if (rect.left > left) {
                deltaX = left-rect.left;
            }
            if (rect.right < right) {
                deltaX = right - rect.right;
            }
        }

        if (rect.height() >= getHeight()) {
            if (rect.top > top) {
                deltaY =top -rect.top;
            }
            if (rect.bottom < bottom) {
                deltaY = bottom - rect.bottom;
            }
        }

        imageMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBounds() {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float left = getX();
        final float top = getY();
        final float right = getWidth() + left;
        final float bottom = getHeight() + top;
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > top && isCheckTopAndBottom) {
            deltaY = top - rect.top;
        }
        if (rect.bottom < bottom && isCheckTopAndBottom) {
            deltaY = bottom - rect.bottom;
        }
        if (rect.left > left && isCheckLeftAndRight) {
            deltaX = left - rect.left;
        }
        if (rect.right < right && isCheckLeftAndRight) {
            deltaX = right - rect.right;
        }
        imageMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF() {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            imageMatrix.mapRect(rect);
        } else {
            rect.set(0, 0, getWidth(), getHeight());
            imageMatrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    /**
     * 我们让OnTouchListener的MotionEvent交给ScaleGestureDetector进行处理
     * public boolean onTouch(View v, MotionEvent event){
     * return mScaleGestureDetector.onTouchEvent(event);
     * }
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                Log.e(TAG, "ACTION_MOVE");
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }

                if (isCanDrag) {
                    isCheckLeftAndRight = isCheckTopAndBottom = true;
                    // 如果宽度小于屏幕宽度，则禁止左右移动
                    if (rectF.width() < getWidth()) {
                        dx = 0;
                        isCheckLeftAndRight = false;
                    }
                    // 如果高度小雨屏幕高度，则禁止上下移动
                    if (rectF.height() < getHeight()) {
                        dy = 0;
                        isCheckTopAndBottom = false;
                    }

                    //设置偏移量
                    imageMatrix.postTranslate(dx, dy);
                    //再次校验
                    checkMatrixBounds();
                    //setImageMatrix(imageMatrix);
                    invalidate();
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_UP");
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        imageMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 根据图片的宽和高以及屏幕的宽和高，对图片进行缩放以及移动至屏幕的中心。
     * 如果图片很小，那就正常显示，不放大了~
     */
    @Override
    public void onGlobalLayout() {
        resetView();
    }

    private void resetView() {
        Drawable d = getDrawable();
        if (d == null)
            return;
        Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
        int width = getWidth();
        int height = getHeight();
        // 拿到图片的宽和高
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();

        // 初始缩放到高度或者宽度充满容器
        initScale = Math.min(width * 1.0f / dw, height * 1.0f / dh);

        Log.e(TAG, "initScale = " + initScale);
        imageMatrix.reset();
        //// 图片移动至屏幕中心

        imageMatrix.postTranslate((width - dw) / 2 + getX(), (height - dh) / 2 + getY());
        imageMatrix.postScale(initScale, initScale, getX() + width / 2, getY() + height / 2);
        invalidate();
    }



    /**
     * 是否是推动行为
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
    }

}