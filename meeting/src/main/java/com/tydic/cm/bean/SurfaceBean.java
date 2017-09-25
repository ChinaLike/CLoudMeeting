package com.tydic.cm.bean;

/**
 * 视屏的参数
 * Created by like on 2017-09-19
 */

public class SurfaceBean {

    private boolean isLocal;//是否是本地视频

    private int userId;//用户视频ID

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
