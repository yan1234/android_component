package com.yanling.android.view.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;


/**
 * 带数字效果的进度条
 * @author yanling
 * @date 2016-08-01
 */
public class NumberProgressView extends View {


    //定义标志常量，用于保存和还原instancestate
    private static final String INSTANCE_STATE = "save_state";
    //基本参数
    private static final String INSTANCE_PROGRESS_PARAM = "progress_param";
    //进度值
    private static final String INSTANCE_PROGRESS = "progress";

    //定义上下文
    private Context mContext = null;

    //定义进度参数配置实体
    private ProgressViewParam progress_param;

    //定义画笔对象
    private Paint mReachedBarPaint, mUnreachedBarPaint, mTextPaint;

    //定义绘制的矩形区域
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);

    //定义绘制进度信息的参数
    private float mDrawTextWidth, mDrawTextStart, mDrawTextEnd;

    //定义进度值
    private int progress;



    public NumberProgressView(Context context) {
        this(context, null);
    }

    public NumberProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberProgressViewStyle);
    }

    public NumberProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //载入样式参数
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(
            attrs, R.styleable.NumberProgressView, defStyleAttr, 0);
        //初始化参数配置信息
        initParam(attributes);
        attributes.recycle();
        //初始化绘制画笔
        initPainters();
        //设置初始进度值
        setProgress(progress_param.getProgress_current());
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        //最小的高度比较3者的值得出
        return Math.max(progress_param.getText_size(),
                Math.max(progress_param.getReached_bar_height(), progress_param.getUnreached_bar_height()));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        //最小的宽度设置为显示进度信息宽度
        return progress_param.getText_size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    /**
     * 计算尺寸
     * @param measureSpec
     * @param isWidth
     * @return
     */
    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //首先判断是否需要绘制中间的进度值
        if (progress_param.getText_visibility() == Constants.default_text_visible){
            //计算绘制的矩形
            calculateDrawRectF();
        } else {
            calculateDrawRectFWithoutProgressText();
        }
        //绘制载入圆角矩形
        canvas.drawRoundRect(mReachedRectF, progress_param.getReached_bar_height() / 4, progress_param.getReached_bar_height() / 4, mReachedBarPaint);
        //绘制文字
        canvas.drawText(progress_param.getText_prefix() + progress + progress_param.getText_suffix(),
                mDrawTextStart, mDrawTextEnd, mTextPaint);
        //绘制未载入圆角矩形
        canvas.drawRoundRect(mUnreachedRectF, progress_param.getUnreached_bar_height() / 4, progress_param.getUnreached_bar_height() / 4, mUnreachedBarPaint);

    }


    @Override
    protected Parcelable onSaveInstanceState() {
        //解决横竖屏切换view重绘问题，所以对数据进行保存
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        //保存基本的参数信息
        bundle.putSerializable(INSTANCE_PROGRESS_PARAM, progress_param);
        //保存进度值
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //从保存的参数中获取信息
        if (state instanceof Bundle){
            final Bundle bundle = (Bundle)state;
            progress_param = (ProgressViewParam)bundle.getSerializable(INSTANCE_PROGRESS_PARAM);
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 初始化参数配置信息
     * @param attributes 配置参数
     */
    private void initParam(TypedArray attributes){
        //获取默认配置信息
        ProgressViewParam default_param = initDefaultParam();
        //初始化当前参数信息
        progress_param = new ProgressViewParam();
        //设置高度
        progress_param.setReached_bar_height((int)attributes.getDimension(
                R.styleable.NumberProgressView_progress_reached_bar_height,
                default_param.getReached_bar_height()));
        progress_param.setUnreached_bar_height((int) attributes.getDimension(
                R.styleable.NumberProgressView_progress_unreached_bar_height,
                default_param.getUnreached_bar_height()));
        //设置颜色
        progress_param.setReached_bar_color(attributes.getColor(
                R.styleable.NumberProgressView_progress_reached_color,
                default_param.getReached_bar_color()));
        progress_param.setUnreached_bar_color(attributes.getColor(
                R.styleable.NumberProgressView_progress_unreached_color,
                default_param.getUnreached_bar_color()));
        progress_param.setText_color(attributes.getColor(
                R.styleable.NumberProgressView_progress_text_color,
                default_param.getText_color()));
        //字体信息
        progress_param.setText_size((int) attributes.getDimension(
                R.styleable.NumberProgressView_progress_text_size,
                default_param.getText_size()));
        progress_param.setText_offset((int) attributes.getDimension(
                R.styleable.NumberProgressView_progress_text_offset,
                default_param.getText_offset()));
        progress_param.setText_prefix(default_param.getText_prefix());
        progress_param.setText_suffix(default_param.getText_suffix());
        //进度值
        progress_param.setProgress_max(attributes.getInt(
                R.styleable.NumberProgressView_progress_max,
                default_param.getProgress_max()));
        progress_param.setProgress_current(attributes.getInt(
                R.styleable.NumberProgressView_progress_current,
                0));
        //设置进度是否隐藏
        progress_param.setText_visibility(attributes.getInt(
                R.styleable.NumberProgressView_progress_text_visibility,
                Constants.default_text_visible));

    }

    /**
     * 初始化默认参数配置信息
     */
    private ProgressViewParam initDefaultParam(){
        ProgressViewParam default_param = new ProgressViewParam();
        //设置高度
        default_param.setReached_bar_height(Utils.dp2px(mContext, Constants.default_reached_bar_height));
        default_param.setUnreached_bar_height(Utils.dp2px(mContext, Constants.default_reached_bar_height * 0.9f));
        //设置颜色
        default_param.setReached_bar_color(Constants.default_reached_color);
        default_param.setUnreached_bar_color(Constants.default_unreached_color);
        default_param.setText_color(Constants.default_text_color);
        //设置字体大小
        default_param.setText_size(Utils.sp2px(mContext, Constants.default_text_size));
        //设置偏移
        default_param.setText_offset(Utils.dp2px(mContext, Constants.default_text_offset));
        //设置前后缀
        default_param.setText_prefix(Constants.default_prefix);
        default_param.setText_suffix(Constants.default_suffix);
        default_param.setProgress_max(Constants.default_progress_max);

        return default_param;
    }

    /**
     * 初始化绘制画笔
     */
    private void initPainters(){
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(progress_param.getReached_bar_color());

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(progress_param.getUnreached_bar_color());

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(progress_param.getText_color());
        mTextPaint.setTextSize(progress_param.getText_size());
    }

    /**
     * 计算矩形区域（不需要绘制进度值）
     */
    private void calculateDrawRectFWithoutProgressText() {
        //计算加载的矩形
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2 - progress_param.getReached_bar_height() / 2;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight())  * getProgress() / progress_param.getProgress_max() + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2 + progress_param.getReached_bar_height() / 2;

        //计算未加载的矩形
        mUnreachedRectF.left = mReachedRectF.right;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2 - progress_param.getUnreached_bar_height()/2;
        mUnreachedRectF.bottom = getHeight() / 2 + progress_param.getUnreached_bar_height()/2;
    }

    /**
     * 计算绘制的矩形区域（需要绘制进度值）
     */
    private void calculateDrawRectF() {

        //计算绘制文字宽度
        mDrawTextWidth = mTextPaint.measureText(progress_param.getText_prefix()
                + getProgress() + progress_param.getText_suffix());

        //绘制载入进度的矩形
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight()/ 2 - progress_param.getReached_bar_height()/2;
        mReachedRectF.right = (int)(getWidth()-getPaddingLeft()-getPaddingRight()-mDrawTextWidth-progress_param.getText_offset()) * getProgress() / progress_param.getProgress_max()
                 + getPaddingLeft();
        mReachedRectF.bottom = getHeight()/2 + progress_param.getReached_bar_height()/2;

        //绘制中间的文字
        mDrawTextStart = mReachedRectF.right + progress_param.getText_offset();
        //end表示baseline在屏幕中的位置
        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        //绘制未载入进度的矩形
        mUnreachedRectF.left = mDrawTextStart + mDrawTextWidth;
        mUnreachedRectF.top = getHeight() / 2 - progress_param.getUnreached_bar_height()/2;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.bottom = getHeight()/2 + progress_param.getUnreached_bar_height()/2;

    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (this.progress != progress && progress <= progress_param.getProgress_max() && progress >=0){
            this.progress = progress;
            //刷新
            invalidate();
        }
    }

    public ProgressViewParam getProgress_param() {
        return progress_param;
    }
}
