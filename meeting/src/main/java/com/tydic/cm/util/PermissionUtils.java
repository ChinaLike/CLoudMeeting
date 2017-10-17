package com.tydic.cm.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * android 6.0权限动态申请
 * Created by like on 2017-10-17
 */

public class PermissionUtils {

    public static final int MY_PERMISSION_REQUEST_CODE = 10000;

    /**
     * 检查是否拥有指定的所有权限
     */
    public static boolean checkPermissionAllGranted(String[] permissions, Context mContext) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 请求指定权限组
     *
     * @param permissions
     * @param activity
     */
    public static void requestPermission(String[] permissions, Activity activity) {

//        boolean isAllGranted = checkPermissionAllGranted(permissions, activity);
//        if (isAllGranted) {
//            return;
//        }
        ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 请求指定权限
     *
     * @param permission
     * @param activity
     */
    public static void requestPermission(String permission, Activity activity) {
        String[] permissions = new String[]{permission};
        requestPermission(permissions, activity);
    }

}
