package com.yanling.android.view.imageshow;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * 扩展ImageView，实现图片的缩放，滑动等手势操作
 * @author yanling
 * @date 2016-08-08
 */
public class PhotoView extends ImageView{


    public static final String TAG = PhotoView.class.getSimpleName();

    //定义X轴方向上左右滑动最小距离阀值
    private static final int FLING_MIN_DISTANCE_X = 150;
    //定义X轴方向上左右滑动最小速度阀值
    private static final int FLING_MIN_SPEED_X = 100;

    //定义矩阵最大缩放值
    private static final float SCALE_MAX = 4.0f;

    //存放矩阵的9个值
    private final float[] matrixValues = new float[9];

    //定义上下文
    private Context mContext;

    //定义Photo对外接口监听事件
    private PhotoViewListener mPhotoViewListener;
    //定义手势监听
    private GestureDetector mGestureDetector;
    //定义点击事件和长按事件
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    //定义用来缩放的手势监听
    private ScaleGestureDetector mScaleGestureDetector;
    //定义缩放的矩阵
    private Matrix mScaleMatrix = new Matrix();

    //定义标志保存当前是否处于放大状态
    private boolean isZoomUp = false;



    public PhotoView(Context context) {
        super(context);
        init(context);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 初始化
     * @param context，上下文
     */
    private void init(Context context){
        this.mContext = context;
        //添加手势监听事件
        this.mGestureDetector = new GestureDetector(mContext, mOnGestureListener);
        this.setLongClickable(true);
        //添加用来检测缩放的手势监听
        this.mScaleGestureDetector = new ScaleGestureDetector(mContext, mOnScaleGestureListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //return mGestureDetector.onTouchEvent(event);
        return mScaleGestureDetector.onTouchEvent(event);
    }

    public void setOnPhotoViewListener(PhotoViewListener mPhotoViewListener) {
        this.mPhotoViewListener = mPhotoViewListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        this.mOnClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        this.mOnLongClickListener = l;
    }

    /**
     * 获取当前的缩放比例
     * @return
     */
    private float getScale(){
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 定义手势监听事件
     */
    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        /**
         * 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
         */
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            Log.d(TAG, "OnGestureListener：onDown");
            return true;
        }

        /**
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
         */
        @Override
        public void onShowPress(MotionEvent motionEvent) {
            Log.d(TAG, "OnGestureListener：onShowPress");
        }

        /**
         * 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
         */
        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Log.d(TAG, "OnGestureListener：onSingleTapUp");
            //充当用户的点击事件
            mOnClickListener.onClick(PhotoView.this);
            return true;
        }

        /**
         * 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
         */
        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            Log.d(TAG, "OnGestureListener：onScroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            Log.d(TAG, "OnGestureListener：onLongPress");
            //充当用户的长按事件
            mOnLongClickListener.onLongClick(PhotoView.this);
        }

        /**
         * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
         * @param motionEvent,  第1个ACTION_DOWN MotionEvent
         * @param motionEvent1, 最后一个ACTION_MOVE MotionEvent
         * @param velocityX,    X轴上的移动速度，像素/秒
         * @param velocityY,    Y轴上的移动速度，像素/秒
         * @return
         */
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
            Log.d(TAG, "OnGestureListener：onFling");

            //如果当前状态是处于非放大状态，则处理左右滑动事件
            if (!isZoomUp){
                //表示变化范围在滑动阀值内
                if (Math.abs(motionEvent.getX() - motionEvent1.getX()) >= FLING_MIN_DISTANCE_X
                        && Math.abs(velocityX) > FLING_MIN_SPEED_X){
                    //如果起点x轴方向坐标大于终点x轴方向坐标，则表示向左滑动
                    if (motionEvent.getX() > motionEvent1.getX()){
                        Log.d(TAG, "OnGestureListener: slideToLeft");
                        mPhotoViewListener.onSlideToLeft(PhotoView.this);
                    } else{
                      //向右滑动
                        mPhotoViewListener.onSlideToRight(PhotoView.this);
                        Log.d(TAG, "OnGestureListener: slideToRight");
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "OnGestureListener：onFling");
            return super.onDoubleTap(e);
        }
    };

    /**
     * 定义用来监听手势缩放的事件监听
     */
    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            //获取缩放比例
            float scale = getScale();
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            if (getDrawable() == null){
                return true;
            }
            /**
             * 缩放范围控制
             */
            if ((scale < SCALE_MAX && scaleFactor > 1.0f)){
                //控制最大缩放倍数
                if (scaleFactor * scale > SCALE_MAX){
                    scaleFactor = SCALE_MAX / scale;
                }
            }
            //设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor
                    ,scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            setImageMatrix(mScaleMatrix);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

        }
    };
}
