package com.tydic.cm.model;

import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.config.SurfaceConfig;
import com.tydic.cm.constant.Key;

import java.util.List;

/**
 * 指定人员数据
 * Created by like on 2017-09-19
 */

public class UserMo {

    /**
     * 在线人员列表中是否有主讲人
     *
     * @param list 在线人员
     * @return
     */
    public boolean isHasSpeaker(List<UsersBean> list) {
        for (UsersBean bean : list) {
            if (bean.getIsPrimarySpeaker().equals("1")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取主讲人
     *
     * @param list  在线人员
     * @return  主讲人信息
     */
    public UsersBean getSpeaker(List<UsersBean> list) {
        for (UsersBean bean : list) {
            if (Key.SPEAKER.equals(bean.getIsPrimarySpeaker())) {
                return bean;
            }
        }
        return null;
    }

}
