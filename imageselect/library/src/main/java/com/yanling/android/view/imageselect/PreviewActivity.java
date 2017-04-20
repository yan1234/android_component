package com.yanling.android.view.imageselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片预览界面
 * @author yanling
 * @date 2017-04-19
 */
public class PreviewActivity extends Activity implements View.OnClickListener{

    private static final String TAG = PreviewActivity.class.getSimpleName();

    //定义数据Bundle传输的key值
    //预览图片列表
    public static final String BUNDLE_SHOW_IMAGES = "BUNDLE_SHOW_IMAGES";
    //已选图片列表
    public static final String BUNDLE_SELECTED_IMAGES = "BUNDLE_SELECTED_IMAGES";
    //当前预览位置
    public static final String BUNDLE_CURRENT_POSITION = "BUNDLE_CURRENT_POSITION";
    //当前能选中的最大张数
    public static final String BUNDLE_TOTAL_SELECTED = "BUNDLE_TOTAL_SELECTED";

    //定义偏移量
    private static final float DEVIATION = 100f;

    //定义界面操作
    private ImageView iv_back;
    private TextView tv_number;
    private TextView tv_ok;
    //定义展示图片的imageview
    private ImageView imageView;
    //定义是否选择的标志
    private ImageView iv_select;

    //定义待预览的数据列表
    private List<String> showImages;
    //定义当前已经选中的数据列表
    private List<String> selectedImages;
    //定义当前预览的位置
    private int current_position;
    //定义当前能选中的最大张数
    private int total_selected;

    //定义触摸焦点
    private float action_down_x_before;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);
        //获取上个界面传递的数据
        Bundle bundle = getIntent().getExtras();
        showImages = (List<String>)bundle.getSerializable(BUNDLE_SHOW_IMAGES);
        selectedImages = (List<String>)bundle.getSerializable(BUNDLE_SELECTED_IMAGES);
        current_position = bundle.getInt(BUNDLE_CURRENT_POSITION, 0);
        total_selected = bundle.getInt(BUNDLE_TOTAL_SELECTED, Params.default_select_total_num);
        initView();
        initEvent();
    }

    /**
     * 初始化界面
     */
    private void initView(){
        iv_back = (ImageView) this.findViewById(R.id.activity_preview_back);
        tv_number = (TextView)this.findViewById(R.id.activity_preview_number);
        tv_ok = (TextView)this.findViewById(R.id.activity_preview_ok);
        imageView = (ImageView)this.findViewById(R.id.activity_preview_image);
        iv_select = (ImageView)this.findViewById(R.id.activity_preview_select);

        //设置当前预览的数目
        tv_number.setText(current_position+1 + "/" + showImages.size());
        //设置完成按钮的状态
        showOkStatus();
        //载入图片
        Glide.with(PreviewActivity.this).load(showImages.get(current_position))
                .crossFade().into(imageView);
        //设置select按钮的状态
        if (selectedImages.contains(showImages.get(current_position))){
            //设置为选中状态
            iv_select.setImageResource(R.drawable.selected);
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //为控件设置监听事件
        iv_back.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        iv_select.setOnClickListener(this);
        //监听预览的onTouch事件, 实现左右滑动切换图片的功能
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //记录当前触摸的x坐标
                        action_down_x_before = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        //获取当前的x坐标
                        float current_x = event.getX();
                        //如果弹起的x坐标比按下的大,则手势是向右滑动,向前翻一页
                        if (current_x - action_down_x_before >= DEVIATION){
                            //向前翻页
                            pageTurn(false);
                        }else{
                            //向后翻页
                            pageTurn(true);
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.activity_preview_back){
            //返回按钮
            setResult(Activity.RESULT_CANCELED, null);
            PreviewActivity.this.finish();
        }else if (v.getId() == R.id.activity_preview_ok){
            //完成按钮
            //封装选择的列表给到前一个界面
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_SELECTED_IMAGES, (ArrayList)selectedImages);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            PreviewActivity.this.finish();
        }else if (v.getId() == R.id.activity_preview_select){
            //选择/取消选择按钮
            //处理当前状态的选中与取消事件
            if (selectedImages.contains(showImages.get(current_position))){
                //当前已经处于选中,则取消选中
                selectedImages.remove(showImages.get(current_position));
                iv_select.setImageResource(R.drawable.unselected);
            }else{
                //选中
                selectedImages.add(showImages.get(current_position));
                iv_select.setImageResource(R.drawable.selected);
            }
            //改变完成按钮的状态
            showOkStatus();
        }
    }

    /**
     * 翻页操作
     * @param isToNext，表示是否向后翻页
     */
    private void pageTurn(boolean isToNext){
        //标示向后翻页
        if (isToNext){
            if (current_position < showImages.size() - 1){
                //向后翻一页
                Glide.with(PreviewActivity.this).load(showImages.get(++current_position))
                        .animate(R.anim.from_right_to_left).into(imageView);
            }
        }else{
            //向前翻页
            if (current_position > 0){
                Glide.with(PreviewActivity.this).load(showImages.get(--current_position))
                        .animate(R.anim.from_left_to_right).into(imageView);
            }
        }
        //改变当前的页数显示
        tv_number.setText(current_position + 1 + "/" + showImages.size());
        //翻页完成后判断当前的是否被选中
        if (selectedImages.contains(showImages.get(current_position))){
            iv_select.setImageResource(R.drawable.selected);
        }else{
            iv_select.setImageResource(R.drawable.unselected);
        }
    }

    /**
     * 改变完成按钮的状态
     */
    private void showOkStatus(){
        if (selectedImages.size() > 0){
            //如果有选中的,则改变预览和完成按钮的状态
            tv_ok.setBackgroundColor(getResources().getColor(R.color.tv_bg_reached));
            tv_ok.setTextColor(getResources().getColor(R.color.tv_textColor_reached));
            tv_ok.setText("完成(" + selectedImages.size() + "/" + total_selected + ")");
        }else{
            //变成无法选中的状态
            tv_ok.setBackgroundColor(getResources().getColor(R.color.tv_bg_unreached));
            tv_ok.setTextColor(getResources().getColor(R.color.tv_textColor_unreached));
            tv_ok.setText("完成");
        }
    }

}
