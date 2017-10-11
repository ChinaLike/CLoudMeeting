package com.tydic.cm.model;

import android.content.Context;

import com.bairuitech.anychat.AnyChatCoreSDK;

/**
 * 音频控制
 * Created by like on 2017-10-11
 */

public class Audio {

    private AnyChatCoreSDK anychat;

    private Context mContext;

    public Audio(AnyChatCoreSDK anychat, Context mContext) {
        this.anychat = anychat;
        this.mContext = mContext;
    }

    /**
     * 关闭本地声音，即关闭本地麦克风
     */
    public void closeLocalAudio() {
        anychat.UserSpeakControl(-1, 0);
    }

    /**
     * 打开本地声音。即打开本地麦克风
     */
    public void openLocalAudio() {
        anychat.UserSpeakControl(-1, 1);
    }

    /**
     * 关闭远程指定用户声音
     *
     * @param userId
     */
    public void closeDistanceAudio(int userId) {
        anychat.UserSpeakControl(userId, 0);
    }

    /**
     * 打开远程指定用户声音
     *
     * @param userId
     */
    public void openDistanceAudio(int userId) {
        anychat.UserSpeakControl(userId, 1);
    }

    /**
     * 关闭远程所有声音
     */
    public void closeDistanceAudio() {
        int[] onlineUserCount = AnyChatCoreSDK.getInstance(mContext).GetOnlineUser();
        int size = onlineUserCount.length;
        for (int i = 0; i < size; i++) {
            closeDistanceAudio(onlineUserCount[i]);
        }
    }

    /**
     * 打开远程所有声音
     */
    public void openDistanceAudio() {
        int[] onlineUserCount = AnyChatCoreSDK.getInstance(mContext).GetOnlineUser();
        int size = onlineUserCount.length;
        for (int i = 0; i < size; i++) {
            openDistanceAudio(onlineUserCount[i]);
        }
    }

}
