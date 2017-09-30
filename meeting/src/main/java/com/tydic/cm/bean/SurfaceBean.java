package com.tydic.cm.bean;

/**
 * 视屏的参数
 * Created by like on 2017-09-19
 */

public class SurfaceBean {

    private Boolean isLocal;//是否是本地视频

    private int userId;//用户视频ID

    private Boolean isSound;//是否打开喇叭

    private Boolean isSpeaker;//是否打开麦克风

    private Boolean isOpenCamera;//是否打开摄像头

    private int width;//视频的宽度

    private int height;//视频的高度

    public boolean isLocal() {
        if (isLocal == null) {
            return false;
        }
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

    public boolean isSound() {
        if (isSound == null) {
            return true;
        }
        return isSound;
    }

    public void setSound(boolean sound) {
        isSound = sound;
    }

    public boolean isSpeaker() {
        if (isSpeaker == null) {
            return true;
        }
        return isSpeaker;
    }

    public void setSpeaker(boolean speaker) {
        isSpeaker = speaker;
    }

    public boolean isOpenCamera() {
        if (isOpenCamera == null) {
            return true;
        }
        return isOpenCamera;
    }

    public void setOpenCamera(boolean openCamera) {
        isOpenCamera = openCamera;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
