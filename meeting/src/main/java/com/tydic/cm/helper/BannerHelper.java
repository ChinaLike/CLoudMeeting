package com.tydic.cm.helper;

import com.tydic.cm.bean.UsersBean;

import java.util.List;

/**
 * 视频轮播回调
 * Created by li on 2017/11/30.
 */

public interface BannerHelper {
    /**
     * 轮播回调
     * @param mode 轮播模式， 1-表示全屏轮播  2-表示非全屏且2为一组轮播  依次类推
     * @param list 轮播参数，轮播时以此集合中参数进行轮播
     */
    void banner(int mode, List<UsersBean> list);
}
