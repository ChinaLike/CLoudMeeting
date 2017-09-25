package com.tydic.cm.config;

import android.content.Context;

import com.tydic.cm.util.ScreenUtil;

/**
 * 视频界面配置
 * Created by like on 2017-09-19
 */

public class SurfaceConfig {

    private Context mContext;

    public static int ROW = 1;//一屏显示行数
    public static int COLUMN = 1;//一屏显示列数

    public SurfaceConfig(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 一页显示的列数
     *
     * @return
     */
    public int itemWidth() {
        return (int) (ScreenUtil.getScreenWidth(mContext) / COLUMN + 0.5);
    }

    /**
     * 一页显示的行数
     *
     * @return
     */
    public int itemHeight() {
        return (int) (ScreenUtil.getScreenHeight(mContext) / ROW + 0.5);
    }

}
