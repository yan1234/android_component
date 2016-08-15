package com.yanling.android.view.imageshow;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * 扩展ImageView，实现图片的缩放，滑动等手势操作
 * @author yanling
 * @date 2016-08-08
 */
public class PhotoView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener{


    public static final String TAG = PhotoView.class.getSimpleName();

    //定义X轴方向上左右滑动最小距离阀值
    private static final int FLING_MIN_DISTANCE_X = 150;
    //定义X轴方向上左右滑动最小速度阀值
    private static final int FLING_MIN_SPEED_X = 100;

    //定义矩阵最大缩放值
    private static final float SCALE_MAX = 4.0f;


    //定义变量表示是否进行PhotoView扩展
    private boolean isEnable = true;
    //定义标志表示是否是多点触控
    private boolean isMultTouch = false;
    //定义变量表示是否第一次载入
    private boolean isFirst = true;

    //定义上下文
    private Context mContext;

    //定义Photo对外接口监听事件
    private PhotoViewListener mPhotoViewListener;
    //定义点击事件和长按事件
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    //定义手势监听
    private GestureDetector mGestureDetector;
    //定义用来缩放的手势监听
    private ScaleGestureDetector mScaleGestureDetector;

    //定义缩放的矩阵
    private Matrix mScaleMatrix = new Matrix();
    //保存初始化时的缩放比例，如果原始图片宽或高大于屏幕，进行缩放，则初始值会小于1.0f;
    private float initScale = 1.0f;

    //定义标志保存当前是否处于放大状态
    private boolean isZoomUp = false;

    //定义图片的宽高
    private float mImageWidth, mImageHeight;

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
        //设置ScaleType类型为Matrix
        setScaleType(ScaleType.MATRIX);
        //添加用来检测缩放的手势监听
        this.mScaleGestureDetector = new ScaleGestureDetector(mContext, mOnScaleGestureListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //return mGestureDetector.onTouchEvent(event);
        //如果当前设置为允许扩展
        if (isEnable){
            if (event.getPointerCount() >= 2){
                isMultTouch = true;
                return mScaleGestureDetector.onTouchEvent(event);
            }else{
                //单点触控
                return mGestureDetector.onTouchEvent(event);
            }

        }else{
            //关闭扩展
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //根据对应的编译版本执行
        if (Build.VERSION.SDK_INT > 16){
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }else{
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        //首次载入时，缩放图片的宽高到屏幕的宽高进行适配
        if (isFirst){
            //获取图片元素
            Drawable drawable = getDrawable();
            if (drawable == null){
                return ;
            }
            //获取宽高
            int width = getWidth();
            int height = getHeight();
            //得到图片的宽高
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            float scale = 1.0f;
            //如果图片的宽或高大于屏幕的，则缩放至屏幕的宽或高
            //主要处理一下3种情况
            if (dw > width && dh <= height){
                //1、宽度超出范围，但高度未超出，以宽为刻度缩放
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw < width){
                //2、高度超出范围，宽未超出，以高为刻度缩放
                scale = height * 1.0f / dh;
            }
            if (dw > width && dh > height){
                //3、宽高都超出，则以最小的比例基准刻度缩放，保证能显示整张图片
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            //初始化刻度尺寸
            initScale = scale;
            //将图片移至屏幕中心
            mScaleMatrix.postTranslate((width-dw)*1.0f / 2, (height-dh)*1.0f / 2);
            //以屏幕中心为缩放点缩放到初始比例
            mScaleMatrix.postScale(scale, scale, getWidth()/2, getHeight()/2);
            setImageMatrix(mScaleMatrix);
            //初始化后重置标志
            isFirst = false;
        }
    }

    /*@Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        //设置完图片后获取该图片的坐标变换矩阵
        mScaleMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        //图片宽度为屏幕宽度/缩放倍数
        mImageWidth = getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (getHeight() -values[Matrix.MTRANS_Y]*2) / values[Matrix.MSCALE_Y];
    }*/

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
            if (mOnClickListener != null){
                //充当用户的点击事件
                mOnClickListener.onClick(PhotoView.this);
            }
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
            if (mOnLongClickListener != null){
                //充当用户的长按事件
                mOnLongClickListener.onLongClick(PhotoView.this);
            }
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
            if (!isZoomUp && mPhotoViewListener != null){
                //表示变化范围在滑动阀值内
                if (Math.abs(motionEvent.getX() - motionEvent1.getX()) >= FLING_MIN_DISTANCE_X
                        && Math.abs(velocityX) > FLING_MIN_SPEED_X){
                    //如果起点x轴方向坐标大于终点x轴方向坐标，则表示向左滑动
                    if (motionEvent.getX() > motionEvent1.getX()){
                        Log.d(TAG, "OnGestureListener: slideToLeft");
                        mPhotoViewListener.onSlideToLeft(PhotoView.this);
                    } else{
                      //向右滑动
                        Log.d(TAG, "OnGestureListener: slideToRight");
                        mPhotoViewListener.onSlideToRight(PhotoView.this);
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "OnGestureListener：onDoubleTap");

            return super.onDoubleTap(e);
        }
    };

    /**
     * 定义用来监听手势缩放的事件监听
     */
    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //获取缩放值
            float scale = getScale();
            float scaleFactor = detector.getScaleFactor();
            //做下为空判断
            if (getDrawable() == null){
                return true;
            }
            //对缩放范围进行控制
            if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                    || (scale > initScale && scaleFactor < 1.0f)){
                if (scale * scaleFactor < initScale){
                    //如果缩放比例小于最小的缩放比例，则直接用最小
                    scaleFactor = initScale / scale;
                }
                if (scale * scaleFactor > SCALE_MAX){
                    //如果缩放比例大于最大的缩放比例，则直接用最大的
                    scaleFactor = SCALE_MAX / scale;
                }
                //进行缩放
                mScaleMatrix.postScale(scaleFactor, scaleFactor,
                        detector.getFocusX(), detector.getFocusY());
                //处理边界偏移
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
            }
            return true;
        }
    };

    /**
     * 获取当前图片的缩放值
     * @return
     */
    private float getScale(){
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 在缩放时，进行图片显示范围控制，主要是让图片显示在布局区域内，而不出现在边界外
     */
    private void checkBorderAndCenterWhenScale(){
        //获取当前图片范围
        RectF rectF = getMatrixRectF();
        //定义左右移动的区域
        float deltaX = 0, deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        //如果宽或高大于屏幕，则控制范围
        if (rectF.width() >= width){
            //控制左右边界
            if (rectF.left > 0){
                deltaX = -rectF.left;
            }
            if (rectF.right < width){
                deltaX = width - rectF.right;
            }
        }
        if (rectF.height() >= height){
            //控制上下边界
            if (rectF.top > 0){
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height){
                deltaY = height - rectF.bottom;
            }
        }
        //如果宽或高小于屏幕大小，则居中显示
        if (rectF.width() < width){
            deltaX = width / 2.0f - rectF.right + rectF.width()/2.0f;
        }
        if (rectF.height() < height){
            deltaY = height / 2.0f - rectF.bottom + rectF.height() / 2.0f;
        }
        //执行偏移操作
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据图片当前的matrix获取图片的范围
     * @return
     */
    private RectF getMatrixRectF(){
        Matrix matrix = new Matrix();
        RectF rectF = new RectF();
        if (getDrawable() != null){
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }


}
