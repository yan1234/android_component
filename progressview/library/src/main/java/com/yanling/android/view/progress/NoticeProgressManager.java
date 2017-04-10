package com.yanling.android.view.progress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 用于自定义通知栏进度下载manager
 * @author yanling
 * @date 2016-08-03
 */
public class NoticeProgressManager {

    //定义通知的id
    public static final int NOTICE_ID = 999;
    //定义广播接收器的action
    private static final String NOTIFICATION_EVENT = "com.yanling.android.notification";
    //定义pendingIntent request id
    private static final int PENDINGINTENT_REQUEST_CODE = 9999;

    //定义单例模式
    private static NoticeProgressManager noticeProgressManager;

    //定义上下文
    private Context mContext;
    //定义消息管理器
    private NotificationManager mNotificationManager;
    //定义消息构造器
    private NotificationCompat.Builder mBuilder;
    //定义消息对象
    private Notification mNotification;
    //定义自定义RemoteView
    private RemoteViews mRemoteViews;
    //定义广播监听
    private NotificationEventReciver reciver;
    //定义意图pendingintent
    private PendingIntent mPendingIntent;

    //定义变量标志当前是否暂停
    private boolean isPause = false;

    /**
     * 获取单例
     *
     * 初始化通知消息
     * @param context，上下文
     * @param icon，左边的通知图标
     * @param title，显示的下载内容标题
     * @return
     */
    public static NoticeProgressManager getInstance(Context context, Bitmap icon, String title){
        if (noticeProgressManager == null){
            noticeProgressManager = new NoticeProgressManager(context, icon, title);
        }
        return noticeProgressManager;
    }

    private NoticeProgressManager(Context context, Bitmap icon, String title){
        init(context, icon, title);
    }

    /**
     * 初始化通知消息
     * @param context，上下文
     * @param icon，左边的通知图标
     * @param title，显示的下载内容标题
     */
    private void init(Context context, Bitmap icon, String title){
        this.mContext = context;
        //初始化消息管理器
        mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //注册消息监听广播
        reciver = new NotificationEventReciver();
        IntentFilter filter = new IntentFilter(NOTIFICATION_EVENT);
        mContext.registerReceiver(reciver, filter);

        //初始化构造器
        mBuilder = new NotificationCompat.Builder(mContext);
        //配置基本参数
        mBuilder.setOngoing(true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //初始化自定义布局
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_progress_view_item);
        //设置固定的参数
        mRemoteViews.setImageViewBitmap(R.id.notification_icon, icon);
        mRemoteViews.setTextViewText(R.id.notification_titile, title);

        //注意这里要设置smallicon,不然通知不会显示
        mBuilder.setSmallIcon(R.drawable.download_default_icon);

        //给imageview添加点击事件（start/pause)
        Intent intent = new Intent(NOTIFICATION_EVENT);
        mPendingIntent = PendingIntent.getBroadcast(mContext,
                PENDINGINTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //初始化消息对象
        mNotification = mBuilder.build();
        //初始化进度值
        //setProgress(0, "0 k/s", 0, 0);
    }

    /**
     * 更新通知消息界面的进度值
     * @param progress，当前进度值（以100为最大值）
     * @param speed，当前下载速度（注意添加速度单位，如xxx K/s 或 xxx M/s）
     * @param download，当前已下载的大小（单位为 ：M)
     * @param total, 待下载文件总大小（单位为：M)
     */
    public void setProgress(int progress, String speed, int download, int total){
        mRemoteViews.setProgressBar(R.id.notification_progress, 100, progress, false);
        mRemoteViews.setTextViewText(R.id.notification_speed, speed);
        mRemoteViews.setTextViewText(R.id.notification_size, "" + download);
        mRemoteViews.setTextViewText(R.id.notification_total, total + " M");
        //发送通知消息
        sendNotify();
    }

    /**
     * 发送通知消息
     */
    public void sendNotify(){
        mNotification.contentView = mRemoteViews;
        //给按钮添加点击事件
        mNotification.contentView.setOnClickPendingIntent(R.id.notification_operate, mPendingIntent);
        mNotificationManager.notify(NOTICE_ID, mNotification);
    }

    /**
     * 清除通知
     * @param notifyId
     */
    public void clearNotify(int notifyId){
        if (mNotificationManager != null){
            mNotificationManager.cancel(notifyId);
        }
    }

    /**
     * 销毁资源
     */
    public void release(){
        //暂停下载
        isPause = true;
        //注销广播
        mContext.unregisterReceiver(reciver);
        //清除通知
        clearNotify(NOTICE_ID);
    }

    /**
     * 消息广播接收器
     */
    public class NotificationEventReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理"暂定/继续"事件
            //如果当前是暂停状态，那么点击后状态将会变为开启
            int resId = isPause ? R.drawable.download_pause : R.drawable.download_continue;
            isPause = !isPause;
            //更新界面并发送通知
            mRemoteViews.setImageViewResource(R.id.notification_operate, resId);
            sendNotify();
        }
    }

    public boolean isPause() {
        return isPause;
    }
}
