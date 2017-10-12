package com.tydic.cm.model;

import android.content.Context;
import android.widget.ImageView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.tydic.cm.R;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.OnVideoStateChangeListener;
import com.tydic.cm.util.T;

/**
 * 视频控制
 * Created by like on 2017-10-11
 */

public class Video {

    private AnyChatCoreSDK anychat;

    private Context mContext;

    /**
     * 摄像头状态
     */
    private String videoState = "2";
    /**
     * 是否打开摄像头
     */
    private boolean isOpenCamera = true;
    /**
     * 是否有摄像头，针对个别手机
     */
    private boolean isHasCamera = true;

    public Video(AnyChatCoreSDK anychat, Context mContext) {
        this.anychat = anychat;
        this.mContext = mContext;
    }

    /**
     * 关闭本地摄像头
     */
    public void closeLocalCamera() {
        anychat.UserCameraControl(-1, 0);
    }

    /**
     * 打开本地摄像头
     */
    public void openLocalCamera() {
        anychat.UserCameraControl(-1, 1);
    }

    /**
     * 初始化摄像头
     * @param view
     * @param userState
     */
    public void init(ImageView view , UsersBean userState){
        this.videoState = userState.getVideoStatus();
        if (this.videoState.equals(Key.VIDEO_OPEN)) {
            view.setImageResource(R.drawable.img_meeting_camera_open);
            anychat.UserCameraControl(-1, 1);
            isOpenCamera = true;
        } else {
            view.setImageResource(R.drawable.img_meeting_camera_close);
            anychat.UserCameraControl(-1, 0);
            isOpenCamera = false;
        }
    }

    /**
     * 设置摄像头状态
     * @param view
     * @param userState
     * @param listener
     */
    public void changeCamera(ImageView view, UsersBean userState, OnVideoStateChangeListener listener) {
        if (isHasCamera) {
            if (isOpenCamera) {
                //执行关闭摄像头
                userState.setVideoStatus("1");
                setVideoState("1");
                view.setImageResource(R.drawable.img_meeting_camera_close);
            } else {
                //执行打开摄像头
                userState.setVideoStatus("2");
                setVideoState("2");
                view.setImageResource(R.drawable.img_meeting_camera_open);
            }
            if (listener != null) {
                listener.videoStateChange();
            }
            isOpenCamera = !isOpenCamera;
        } else {
            //没有摄像头
            T.showShort("未检测到有摄像头可使用，请在设置中打开摄像头权限！");
        }
    }

    /**
     * 前后摄像头切换
     */
    public void switchCamera(){
        AnyChatCoreSDK.mCameraHelper.SwitchCamera();
    }

    public String getVideoState() {
        return videoState;
    }

    public void setVideoState(String videoState) {
        this.videoState = videoState;
    }

    public boolean isOpenCamera() {
        return isOpenCamera;
    }

    public void setOpenCamera(boolean openCamera) {
        isOpenCamera = openCamera;
    }

    public boolean isHasCamera() {
        return isHasCamera;
    }

    public void setHasCamera(boolean hasCamera) {
        isHasCamera = hasCamera;
    }
}
