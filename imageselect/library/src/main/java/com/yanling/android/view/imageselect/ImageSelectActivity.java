package com.yanling.android.view.imageselect;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择界面
 * @author yanling
 * @date 2017-04-12
 */
public class ImageSelectActivity extends Activity implements View.OnClickListener{

    private static final String TAG = ImageSelectActivity.class.getSimpleName();

    //定义上下文
    private Context mContext;
    //定义图片展示网格布局
    private GridView imageGrid;
    //定义适配器
    private ImageGridAdapter adapter;
    //定义图片数据
    private List<String> images;

    //定义自定义参数配置对象
    private Params params;

    //定义List保存选中的图片
    private List<String> selectImgs = new ArrayList<>();

    //定义返回按钮
    private ImageView img_back;
    //定义完成按钮
    private TextView tv_ok;
    //定义预览按钮
    private TextView tv_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imageselect);
        mContext = ImageSelectActivity.this;
        //初始化参数配置
        initParams(getIntent().getExtras());
        //判断是否存在已经选中的
        if (params.selected_items != null){
            selectImgs.addAll(params.selected_items);
        }
        //获取图片数据
        images = resolverImages();
        //初始化界面和事件
        initView();
        initEvent();
    }

    /**
     * 初始化参数设置
     * @param bundle
     */
    private void initParams(Bundle bundle){
        params = new Params();
        if (bundle != null){
            //获取配置的列数
            int column_num = bundle.getInt(Params.KEY_COLUMN_NUM, 0);
            //根据列数计算列宽
            int[] result = calculateColumn(mContext, column_num);
            //设定列数和列宽
            params.column_num = result[0];
            params.column_width =result[1];
            //设定最多项数
            params.select_total_num = bundle.getInt(Params.KEY_SELECT_TOTAL_NUM,
                    Params.default_select_total_num);
            params.selected_items = (List<String>)bundle.getSerializable(Params.KEY_SELECTED_ITEMS);
        }else{
            //加载默认的数据
            //计算默认列数和列宽
            int[] result = calculateColumn(mContext, 3);
            params.column_num = result[0];
            params.column_width = result[1];
            params.select_total_num = Params.default_select_total_num;
            //已选择的item列表为空
            params.selected_items = null;
        }
    }

    /**
     * 初始化界面
     */
    private void initView(){
        imageGrid = (GridView)this.findViewById(R.id.imageselect_grid);
        imageGrid.setNumColumns(params.column_num);
        imageGrid.setColumnWidth(params.column_width);
        img_back = (ImageView)this.findViewById(R.id.imageselect_back);
        tv_ok = (TextView)this.findViewById(R.id.imageselect_ok);
        tv_preview = (TextView)this.findViewById(R.id.imageselect_preview);
    }

    /**
     * 初始化事件操作
     */
    private void initEvent(){
        //初始化适配器
        adapter = new ImageGridAdapter(mContext, images, selectImgs, params.column_width);
        imageGrid.setAdapter(adapter);
        //设置滑动监听事件
        imageGrid.setOnScrollListener(onScrollListener);
        //添加选项列表点击事件
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageGridAdapter.ViewHolder viewHolder = (ImageGridAdapter.ViewHolder) view.getTag();
                //判断当前图片是否选中
                if (selectImgs.contains(images.get(position))){
                    //表示当前item被选中,点击后取消选中
                    //调整当前背景
                    viewHolder.select_layout.setBackgroundColor(getResources().getColor(R.color.bg_transparent_two));
                    //取消选项框
                    viewHolder.select.setImageResource(R.drawable.unselected);
                    //去除该项
                    selectImgs.remove(images.get(position));
                }else if (selectImgs.size() <= params.select_total_num-1){
                    //选中该项
                    viewHolder.select_layout.setBackgroundColor(getResources().getColor(R.color.bg_transparent_three));
                    viewHolder.select.setImageResource(R.drawable.selected);
                    selectImgs.add(images.get(position));
                }else{
                    Toast.makeText(mContext, "已超出最大选择张数", Toast.LENGTH_SHORT).show();
                }
                //判断当前是否有选中的列表
                if (selectImgs.size() > 0){
                    //如果有选中的,则改变预览和完成按钮的状态
                    tv_ok.setBackgroundColor(getResources().getColor(R.color.tv_bg_reached));
                    tv_ok.setTextColor(getResources().getColor(R.color.tv_textColor_reached));
                    tv_ok.setText("完成(" + selectImgs.size() + "/" + params.select_total_num + ")");
                    tv_preview.setTextColor(getResources().getColor(R.color.tv_textColor_reached));
                }else{
                    //变成无法选中的状态
                    tv_ok.setBackgroundColor(getResources().getColor(R.color.tv_bg_unreached));
                    tv_ok.setTextColor(getResources().getColor(R.color.tv_textColor_unreached));
                    tv_ok.setText("完成");
                    tv_preview.setTextColor(getResources().getColor(R.color.tv_textColor_unreached));
                }
            }
        });
        //添加返回按钮点击事件
        img_back.setOnClickListener(this);
        //添加完成按钮点击事件
        tv_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageselect_back){
            //返回按钮
        }else if (v.getId() == R.id.imageselect_ok){
            //完成按钮
            if (selectImgs.size() <= 0){
                Toast.makeText(mContext, "请至少选择一张图片", Toast.LENGTH_SHORT).show();
            }else{
                //返回选择的图片数据
            }
        }
    }

    /**
     * 定义GridView的滑动监听事件
     */
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState){
                //停止滑动
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    //设置为停止滑动
                    adapter.setScrollState(false);
                    int count = view.getChildCount();
                    for (int i = 0; i < count; i++){
                        //加载图像
                        ImageView img = (ImageView) (view.getChildAt(i).findViewById(R.id.item_imageselect_img));
                        //标示需要加载图片数据
                        if (img.getTag(R.id.image_tag) != null){
                            //显示图片(通过Glide加载图片)
                            String url = (String)img.getTag(R.id.image_tag);
                            Glide.with(mContext).load(url)
                                    .crossFade().into(img);
                            //设置已经加载过了
                            img.setTag(R.id.image_tag, null);
                        }

                    }
                    break;
                //滑动做出了抛的动作
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    adapter.setScrollState(true);
                    break;
                //正在滚动
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    adapter.setScrollState(true);
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };


    /**
     * 获取图片数据
     * @return
     */
    public List<String> resolverImages(){
        List<String> images = new ArrayList<>();
        //获取ContentResolver对象
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.MIME_TYPE +" in (?, ?) "    //只查询jpg/png
                + "and " + MediaStore.Images.Media.HEIGHT + ">100 "
                + "and " + MediaStore.Images.Media.WIDTH + ">100 "  //控制宽高都大于100
                + "and " + MediaStore.Images.Media.SIZE + ">10240", //控制图片大于10k
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_ADDED + " desc"  //按照添加的先后顺序获取
        );
        while (cursor.moveToNext()){
            //获取图片的路径
            images.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        }
        if (cursor != null){
            cursor.close();
        }
        return images;
    }

    /**
     * 根据列数计算列宽
     * １. 当前列数为０时，根据当前屏幕宽度的显示效果动态的设置列数和列宽
     * 2. 指定列数时，根据指定的列数计算列宽
     *
     * @param context
     * @param column_num,指定的列数
     * @return int[],返回长度为２的int数组，第１个为列数，第２个为列宽
     */
    private int[] calculateColumn(Context context, int column_num){
        //获取当前屏幕宽度
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        //定义结果对象
        int[] result = new int[2];
        //标示未设置列数, 根据当前屏幕宽度尺寸设置合适的列数
        if (column_num == 0){

        }else{
            //直接根据指定的列数计算列宽
            result[0] = column_num;
            result[1] = screenWidth / column_num;
        }
        return result;
    }
}
