package com.tydic.cm.model.inf;

import com.tydic.cm.bean.SurfaceBean;
import com.tydic.cm.bean.UsersBean;

/**
 * 本地视频帮助类
 * Created by like on 2017-09-27
 */

public interface LocalHelper {
    /**
     * 显示本地视频的位置
     *
     * @param position 第几个元素
     * @param bean     参数
     * @param width    视频的宽度
     * @param height   视频的高度
     * @param x        X轴移动的位置
     * @param y        Y轴移动的位置
     */
    void local(int position, UsersBean bean, int width, int height, int x, int y);
}
