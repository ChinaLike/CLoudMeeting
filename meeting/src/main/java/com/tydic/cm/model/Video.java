package com.tydic.cm.model;

import android.content.Context;

import com.bairuitech.anychat.AnyChatCoreSDK;

/**
 * 视频控制
 * Created by like on 2017-10-11
 */

public class Video {

    private AnyChatCoreSDK anychat;

    private Context mContext;

    public Video(AnyChatCoreSDK anychat, Context mContext) {
        this.anychat = anychat;
        this.mContext = mContext;
    }

    /**
     * 关闭本地摄像头
     */
    public void closeLocalCamera(){
        anychat.UserCameraControl(-1, 0);
    }

    /**
     * 打开本地摄像头
     */
    public void openLocalCamera(){
        anychat.UserCameraControl(-1, 1);
    }

}
