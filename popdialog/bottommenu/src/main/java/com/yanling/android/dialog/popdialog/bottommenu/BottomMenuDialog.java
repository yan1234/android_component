package com.yanling.android.dialog.popdialog.bottommenu;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yanling.android.dialog.popdialog.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建仿IOS的底部弹出选项框
 *
 * 调用方式：
 *
 *  //创建对象
 *  BottomMenuDialog dialog = new BottomMenuDialog();
 *  //添加选项列表
 *  String[] array = {"选项１", "选项２"};
 *  dialog.setMenuItems(context, array);
 *  //添加选项点击监听事件
 *  dialog.setOnItemClickListener(listener);
 *  //展示界面
 *  dialog.show();
 *  //关闭界面
 *  dialog.dismiss();
 *
 *  //自定义选项框
 *  通过setMenuItems(context, items, listDrawable)扩展弹框选项
 *  其中items表示BottomMenuItem对象列表，listDrawable标示取消按钮选项的drawable背景
 *
 *
 *
 * @author yanling
 * @date 2017-03-18
 */
public class BottomMenuDialog extends DialogFragment {

    private static final String TAG = BottomMenuDialog.class.getSimpleName();

    //定义上下文对象
    private Context mContext;
    //定义menu菜单listview
    private ListView listView;
    //定义取消按钮
    private TextView tv_cancel;
    //定义Item列表
    private List<BottomMenuItem> list = new ArrayList<BottomMenuItem>();
    private MenuAdapter adapter;
    //定义ItemClick事件
    private AdapterView.OnItemClickListener onItemClickListener;

    //定义取消按钮的背景
    private StateListDrawable cancel_listDrawable;


    public BottomMenuDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //  判断是否添加数据源
        if (list == null || list.size() == 0){
            return null;
        }

        //去掉弹框的标题并设置透明
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getDialog().getWindow().setWindowAnimations(R.style.bottom_menu_animation);//添加一组进出动画

        this.mContext = getActivity();

        //加载弹框布局界面
        View view = inflater.inflate(R.layout.popdialog_bottom_menu, container, false);
        listView = (ListView)view.findViewById(R.id.bottom_menu_lv);
        tv_cancel = (TextView)view.findViewById(R.id.bottom_menu_tv_cancel);

        //添加自定义的背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            //listView.setBackground(listDrawable);
            tv_cancel.setBackground(cancel_listDrawable);
        }else{
            //listView.setBackgroundDrawable(listDrawable);
            tv_cancel.setBackgroundDrawable(cancel_listDrawable);
        }

        //添加取消按钮的点击事件
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuDialog.this.dismiss();
            }
        });
        //初始化listview
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        return view;
    }

    /**
     * 直接通过数组构造item列表，载入默认的样式
     * @param context, 上下文
     * @param array，待显示的选项列表
     */
    public void setMenuItems(Context context, String[] array){
        this.mContext = context;
        if (array != null){
            //将array数组转化为MenuItem列表
            List<BottomMenuItem> items = new ArrayList<BottomMenuItem>();
            for (int i = 0; i < array.length; i++){
                BottomMenuItem item = new BottomMenuItem();
                item.setText(array[i]);
                //载入默认的字体
                item.setText_colorStateList(mContext.getResources().getColorStateList(R.color.bottom_menu_item_text));
                //载入默认的布局
                StateListDrawable stateListDrawable;
                if (array.length == 1){
                    //当只有一个选项时,加载4个圆角
                    stateListDrawable = (StateListDrawable)mContext.getResources()
                            .getDrawable(R.drawable.bottom_menu_bg_selector);
                }else if ( i == 0){
                    //构造第一项
                    stateListDrawable = (StateListDrawable)mContext.getResources()
                            .getDrawable(R.drawable.bottom_menu_bg_first_selector);
                }else if (i == array.length - 1){
                    //构造最后一项
                    stateListDrawable = (StateListDrawable)mContext.getResources()
                            .getDrawable(R.drawable.bottom_menu_bg_last_selector);
                }else{
                    //构造中间的选项
                    stateListDrawable = (StateListDrawable)mContext.getResources()
                            .getDrawable(R.drawable.bottom_menu_bg_middle_selector);
                }

                item.setBg_stateListDrawable(stateListDrawable);
                items.add(item);
            }
            //设置取消按钮和listview的背景
            this.cancel_listDrawable = (StateListDrawable)mContext.getResources()
                    .getDrawable(R.drawable.bottom_menu_bg_selector);
            setMenuItems(context,items, this.cancel_listDrawable);
        }
    }

    /**
     * 设置自定义的Item选项
     * @param context
     * @param items
     * @param listDrawable, 下面取消按钮以及listview的背景
     */
    public void setMenuItems(Context context, List<BottomMenuItem> items, StateListDrawable listDrawable){
        if (context == null || items == null || items.size() == 0 || listDrawable == null){
            Log.e(TAG, "请确保参数不能为空");
            return;
        }
        //初始化自定义的布局
        this.cancel_listDrawable = listDrawable;
        //初始化适配器
        this.list.clear();
        this.list.addAll(items);
        //初始化ListView适配器
        adapter = new MenuAdapter(context, this.list);
        adapter.setList(this.list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 添加选项点击事件
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    /**
     * 快捷的展示操作
     */
    public void show(){
        //判断下是否有数据源
        if (mContext == null || list == null || list.size() == 0){
            Log.e(TAG, "请先添加选项列表");
            return;
        }
        //展示界面
        super.show(((Activity)mContext).getFragmentManager(), "BottomMenuDialog");

    }

    @Override
    public void onStart() {
        super.onStart();
        //设置弹出框适应屏幕宽度(消除默认的左右留白)
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels,
                getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onStop() {
        //加载消失动画
        //this.getView().setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_menu_disappear));
        super.onStop();
    }
}
