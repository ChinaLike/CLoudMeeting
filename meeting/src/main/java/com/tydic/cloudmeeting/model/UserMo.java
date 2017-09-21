package com.tydic.cloudmeeting.model;

import com.tydic.cloudmeeting.bean.UsersBean;
import com.tydic.cloudmeeting.config.SurfaceConfig;

import java.util.List;

/**
 * 人员数据获取
 * Created by like on 2017-09-19
 */

public class UserMo {

    /**
     * 在线人员列表中是否有主讲人
     *
     * @param list 在线人员
     * @return
     */
    public boolean isPrimarySpeaker(List<UsersBean> list) {
        for (UsersBean bean : list) {
            if (bean.getIsPrimarySpeaker().equals("1")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取主讲人信息
     *
     * @param list 在线人员
     * @return 主讲人信息
     */
    public UsersBean primarySpeaker(List<UsersBean> list) {
        for (UsersBean bean : list) {
            if (bean.getIsPrimarySpeaker().equals("1")) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 配置界面显示视频数目
     * @param size
     */
    public void configView(int size){
        if (size < 4){
            SurfaceConfig.COLUMN = size;
            SurfaceConfig.ROW =1;
        }else if (size ==4){
            SurfaceConfig.COLUMN = 2;
            SurfaceConfig.ROW =2;
        }else if (size ==5 || size == 6){
            SurfaceConfig.COLUMN = 3;
            SurfaceConfig.ROW =2;
        }else if (size ==7 || size == 8){
            SurfaceConfig.COLUMN = 4;
            SurfaceConfig.ROW =2;
        }else if (size >8){
            SurfaceConfig.COLUMN = 2;
            SurfaceConfig.ROW =2;
        }
    }

}
