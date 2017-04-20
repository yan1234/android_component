package com.yanling.android.view.imageselect;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ArrayList<String> images;

    //定义自定义参数配置对象
    private Params params;

    //定义List保存选中的图片
    private ArrayList<String> selectImgs = new ArrayList<>();

    //定义返回按钮
    private ImageView img_back;
    //定义完成按钮
    private TextView tv_ok;
    //定义目录选择布局
    private RelativeLayout layout_category;
    private TextView tv_category;
    //定义预览按钮
    private TextView tv_preview;

    //定义map按照目录分类保存图片列表
    private List<String> bucketList = new ArrayList<>();
    private Map<String, List<String>> bucketMap = new HashMap<>();
    //定义变量保存当前的目录名称
    private String current_bucketName = "所有目录";

    //定义目录fragment对象
    private BucketFragment fragment;


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
        layout_category = (RelativeLayout)this.findViewById(R.id.imageselect_category_layout);
        tv_category = (TextView)this.findViewById(R.id.imageselect_category);
        tv_preview = (TextView)this.findViewById(R.id.imageselect_preview);

        //初始化Fragment
        fragment = new BucketFragment();
        //设置展示数据
        bucketMap.put(current_bucketName, images);
        bucketList.add(0, current_bucketName);
        fragment.setBucketList(bucketList, bucketMap);
        fragment.setChecked_bucketName(current_bucketName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //将fragment添加到当前布局
        transaction.add(R.id.imageselect_fragment, fragment);
        //隐藏当前fragment
        transaction.hide(fragment);
        transaction.commit();
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
                if (selectImgs.contains(adapter.getImages().get(position))){
                    //表示当前item被选中,点击后取消选中
                    //调整当前背景
                    viewHolder.select_layout.setBackgroundColor(getResources().getColor(R.color.bg_transparent_two));
                    //取消选项框
                    viewHolder.select.setImageResource(R.drawable.unselected);
                    //去除该项
                    selectImgs.remove(adapter.getImages().get(position));
                }else if (selectImgs.size() <= params.select_total_num-1){
                    //选中该项
                    viewHolder.select_layout.setBackgroundColor(getResources().getColor(R.color.bg_transparent_three));
                    viewHolder.select.setImageResource(R.drawable.selected);
                    selectImgs.add(adapter.getImages().get(position));
                }else{
                    Toast.makeText(mContext, "已超出最大选择张数", Toast.LENGTH_SHORT).show();
                }
                //改变完成按钮的状态
                showOkStatus();

            }
        });
        //添加item长按跳转到预览界面
        imageGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到所有图片预览界面
                showPreview(adapter.getImages(),
                        selectImgs,
                        position,
                        params.select_total_num);
                return true;
            }
        });
        //添加返回按钮点击事件
        img_back.setOnClickListener(this);
        //添加完成按钮点击事件
        tv_ok.setOnClickListener(this);
        //添加目录点击时间
        layout_category.setOnClickListener(this);
        tv_category.setOnClickListener(this);
        //添加预览界面点击事件
        tv_preview.setOnClickListener(this);
    }

    /**
     * 目录列表点击事件监听
     * @param bucketName,当前点击的目录名称
     */
    public void bucketItemClick(String bucketName){
        //保存当前的目录名
        current_bucketName = bucketName;
        //改变目录显示名称
        tv_category.setText(current_bucketName);
        adapter.setImages(bucketMap.get(current_bucketName));
        adapter.notifyDataSetChanged();
        //隐藏当前fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    /**
     * 改变完成按钮的状态
     */
    private void showOkStatus(){
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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageselect_back){
            //返回按钮
            setResult(Activity.RESULT_CANCELED);
            ImageSelectActivity.this.finish();
        }else if (v.getId() == R.id.imageselect_ok){
            //完成按钮
            if (selectImgs.size() <= 0){
                Toast.makeText(mContext, "请至少选择一张图片", Toast.LENGTH_SHORT).show();
            }else{
                //返回选择的图片数据
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Params.KEY_SELECTED_ITEMS, selectImgs);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                ImageSelectActivity.this.finish();
            }
        }else if (v.getId() == R.id.imageselect_category_layout
                || v.getId() == R.id.imageselect_category){
            //布局点击操作事件
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (fragment.isHidden()){
                transaction.show(fragment);
            }else{
                transaction.hide(fragment);
            }
            transaction.commit();
        }else if (v.getId() == R.id.imageselect_preview){
            //选中图片预览界面
            if (selectImgs.size() <= 0){
                //提示至少选一张图片后再预览
                Toast.makeText(mContext, "请至少选一张图片后再预览", Toast.LENGTH_SHORT).show();
            }else{
                //预览选中的图片
                showPreview(selectImgs, selectImgs, 0, params.select_total_num);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //标示预览界面请求返回
        if (requestCode == 200){
            //返回成功
            if (resultCode == Activity.RESULT_OK){
                //获取选择的列表
                selectImgs = (ArrayList<String>) data.getExtras().getSerializable(PreviewActivity.BUNDLE_SELECTED_IMAGES);
                //改变完成按钮的状态
                showOkStatus();
                //变更GridView
                adapter.setSelectImgs(selectImgs);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 跳转到图片预览界面
     * @param showImages,待预览的图片列表
     * @param selectImages,当前选中的图片列表
     * @param current_position,当前预览的图片的位置
     * @param select_total_num,可以选中的最大张数
     */
    public void showPreview(List<String> showImages, List<String> selectImages, int current_position, int select_total_num){
        Intent intent = new Intent(ImageSelectActivity.this, PreviewActivity.class);
        Bundle bundle = new Bundle();
        //封装待传递的信息
        bundle.putSerializable(PreviewActivity.BUNDLE_SHOW_IMAGES, (ArrayList)showImages);
        bundle.putSerializable(PreviewActivity.BUNDLE_SELECTED_IMAGES, (ArrayList)selectImages);
        bundle.putInt(PreviewActivity.BUNDLE_CURRENT_POSITION, current_position);
        bundle.putInt(PreviewActivity.BUNDLE_TOTAL_SELECTED, select_total_num);
        intent.putExtras(bundle);
        startActivityForResult(intent, 200);
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
    public ArrayList<String> resolverImages(){
        ArrayList<String> images = new ArrayList<>();
        //获取ContentResolver对象
        ContentResolver resolver = mContext.getContentResolver();
        //定义查询器对象
        StringBuilder selection = new StringBuilder();
        //标示查询所有的
        /*if (bucketName != null){
            //添加目录匹配
            selection.append(MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                    + "=" + bucketName + " and");
        }*/
        //添加jpg/png过滤
        selection.append(MediaStore.Images.Media.MIME_TYPE +" in (?, ?) ")
                .append("and " + MediaStore.Images.Media.HEIGHT + ">? ") //添加宽高过滤
                .append("and " + MediaStore.Images.Media.WIDTH + ">? ")
                .append("and " + MediaStore.Images.Media.SIZE + ">?");   //添加大小控制
        String[] selectionArgs ={"image/jpeg", "image/png", "100", "100", "10240"};
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA},
                selection.toString(),
                selectionArgs,
                MediaStore.Images.Media.DATE_ADDED + " desc"  //按照添加的先后顺序获取
        );
        while (cursor.moveToNext()){
            //获取图片的目录和路径
            String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            images.add(url);
            //如果map中已经有该目录下的图片
            if (bucketMap.containsKey(bucketName)){
                //将当前的图片添加到该目录下
                bucketMap.get(bucketName).add(url);
            }else{
                //如果还没有添加该目录
                List<String> list = new ArrayList<>();
                list.add(url);
                bucketMap.put(bucketName, list);
                //记录目录列表
                bucketList.add(bucketName);
            }
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
