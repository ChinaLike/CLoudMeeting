package com.tydic.cm.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 统一管理Toast
 * Created by like on 2017-08-17
 */

public class T {

    /**
     * 是否显示Toast弹窗
     */
    private static boolean isShow = true;

    private static Context mApplication;

    private T() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Context application) {
        mApplication = application;
    }

    /**
     * 短时间显示
     *
     * @param message 字符串
     */
    public static void showShort(CharSequence message) {
        if (isShow) {
            Toast.makeText(mApplication, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 短时间显示
     *
     * @param message 资源文件字符串
     */
    public static void showShort(int message) {
        if (isShow) {
            Toast.makeText(mApplication, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 长时间显示
     *
     * @param message 字符串
     */
    public static void showLong(CharSequence message) {
        if (isShow) {
            Toast.makeText(mApplication, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 长时间显示
     *
     * @param message 资源文件字符串
     */
    public static void showLong(int message) {
        if (isShow) {
            Toast.makeText(mApplication, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 自定义时间
     *
     * @param message  字符串
     * @param duration 时间
     */
    public static void show(CharSequence message, int duration) {
        if (isShow) {
            Toast.makeText(mApplication, message, duration).show();
        }
    }

    /**
     * 自定义时间
     *
     * @param message  资源文件字符串
     * @param duration 时间
     */
    public static void show(int message, int duration) {
        if (isShow) {
            Toast.makeText(mApplication, message, duration).show();
        }
    }

    /**
     * 短时间显示，显示在屏幕中间
     *
     * @param message
     */
    public static void showShortCenter(CharSequence message) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 短时间显示，显示在屏幕中间
     *
     * @param message
     */
    public static void showShortCenter(int message) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示，显示在屏幕中间
     *
     * @param message
     */
    public static void showLongCenter(CharSequence message) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示，显示在屏幕中间
     *
     * @param message
     */
    public static void showLongCenter(int message) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 短时间在屏幕底部显示视图
     *
     * @param message
     * @param view
     */
    public static void showView(CharSequence message, View view) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            LinearLayout toastView = (LinearLayout) toast.getView();
            toastView.addView(view, 0);
            toast.show();
        }
    }

    /**
     * 短时间在屏幕底部显示视图
     *
     * @param message
     * @param view
     */
    public static void showView(int message, View view) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            LinearLayout toastView = (LinearLayout) toast.getView();
            toastView.addView(view, 0);
            toast.show();
        }
    }

    /**
     * 短时间在屏幕中间显示视图
     *
     * @param message
     * @param view
     */
    public static void showViewCenter(CharSequence message, View view) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            toastView.addView(view, 0);
            toast.show();
        }
    }

    /**
     * 短时间在屏幕中间显示视图
     *
     * @param message
     * @param view
     */
    public static void showViewCenter(int message, View view) {
        if (isShow) {
            Toast toast = Toast.makeText(mApplication, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            toastView.addView(view, 0);
            toast.show();
        }
    }

}
