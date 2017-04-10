package com.yanling.android.utils.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Android 6.0动态权限申请工具类
 * @author yanling
 * @date 2017-03-29
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    /**
     * 定义接口用于处理权限授予
     */
    interface PermissionGrant{
        /**
         * 权限授予后的回调
         * @param permission，当前授予的权限
         */
        void onPermissionGranted(String permission);
    }

    public static void requestPermission(Activity activity, String permission, int requestCode, PermissionGrant grant){

        try{

            //检查是否已经申请了该权限
            int checkSelfPermission = ActivityCompat.checkSelfPermission(activity, permission);
            //判断该权限是否已经申请了
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                //判断是否是第二次申请,是否需要弹框
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                    Log.d(TAG, "showRequestPermissionRationale");
                    //弹框选择
                    shouldShowRationale(activity, permission, requestCode);
                }else{
                    //不需要展示请求框
                    //直接进行权限申请
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                    Log.d(TAG, permission + "  requestPermission");
                }
            }else{
                Log.d(TAG, "权限已经申请："+ permission);
                //处理回调
                grant.onPermissionGranted(permission);
            }

        }catch (RuntimeException e){
            // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
            // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
            // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
            Log.e(TAG, "RuntimeException:"+ e.getMessage());
        }

    }

    private static void shouldShowRationale(final Activity activity, final String permission, final int requestCode){
        //弹出提示框
        String message = "申请权限: " + permission;
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击确定请求权限
                        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        Log.d(TAG, permission + "  requestPermission");
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
