package com.yanling.android.view.imageshow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 大图展示弹框
 * @author yanling
 * @date 2016-08-04
 */
public class ImageShowDialog extends Dialog implements View.OnClickListener{

    //定义上下文
    private Context mContext;
    //定义变量保存整个根布局
    private View view;
    //定义ImageView对象
    private PhotoView imageView;
    //定义photoview事件监听
    private PhotoViewListener mPhotoViewListener;

    public ImageShowDialog(Context context, PhotoViewListener photoViewListener) {
        super(context);
        this.mContext = context;
        this.mPhotoViewListener = photoViewListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        init();
    }

    private void init(){
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽
        // 总之达不到想要的效果
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //载入布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.image_show_dialog, null);
        //添加动画
        view.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.dialog_bottom_to_top_default));
        imageView = (PhotoView)view.findViewById(R.id.image_show_image);
        imageView.setOnClickListener(this);
        imageView.setOnPhotoViewListener(mPhotoViewListener);
        setContentView(view);
        // 这句话起全屏的作用
        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT);
    }

    @Override
    public void onClick(View view) {

        /*//点击ImageView时关闭弹框
        if (view.getId() == R.id.image_show_image){
            this.dismiss();
        }*/
    }

    public PhotoView getImageView() {
        return imageView;
    }
}
