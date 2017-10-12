package com.tydic.cm.model;

import android.content.Context;
import android.widget.ImageView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.tydic.cm.R;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.OnAudioStateChangeListener;

/**
 * 音频控制
 * Created by like on 2017-10-11
 */

public class Audio {

    private AnyChatCoreSDK anychat;

    private Context mContext;
    /**
     * 麦克风状态
     */
    private String micState = "1";
    /**
     * 是否可以说话
     */
    private boolean isSpeaking = true;
    /**
     * 是否有声音
     */
    private boolean isSound = true;
    /**
     * 状态改变回调
     */
    private OnAudioStateChangeListener onAudioStateChangeListener;

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

    /**
     * 初始化状态
     * @param view
     * @param userState
     */
    public void init(ImageView view , UsersBean userState){
        this.micState = userState.getAudioStatus();
        if (this.micState.equals(Key.AUDIO_OPEN)) {
            view.setImageResource(R.drawable.img_meeting_microphone);
            anychat.UserSpeakControl(-1, 1);
            isSpeaking = true;
        } else {
            view.setImageResource(R.drawable.meeting_microphone_disable);
            anychat.UserSpeakControl(-1, 0);
            isSpeaking = false;
        }
    }

    /**
     * 改变远程声音按钮状态
     *
     * @param view 按钮
     */
    public void changeDistance(ImageView view) {
        if (isSound) {
            view.setImageResource(R.drawable.meeting_speaker_disable);
            closeDistanceAudio();
        } else {
            view.setImageResource(R.drawable.img_meeting_sound);
            openDistanceAudio();
        }
        isSound = !isSound;
    }

    /**
     * 改变本地状态
     *
     * @param view
     * @param userState
     */
    public void changeLocal(ImageView view, UsersBean userState,OnAudioStateChangeListener onAudioStateChangeListener) {
        if (isSpeaking) {
            userState.setAudioStatus("0");
            setMicState("0");
            view.setImageResource(R.drawable.meeting_microphone_disable);
            anychat.UserSpeakControl(-1, 0);
        } else {
            view.setImageResource(R.drawable.img_meeting_microphone);
            userState.setAudioStatus("1");
            setMicState("1");
            anychat.UserSpeakControl(-1, 1);
        }
        if (onAudioStateChangeListener != null){
            onAudioStateChangeListener.audioStateChange();
        }
        isSpeaking = !isSpeaking;
    }

    /**
     * 根据本地状态，判断是否打开远程声音
     */
    public void refreshDistanceAudio(UsersBean bean) {
        if (isSound) {
            bean.setAudioStatus(Key.AUDIO_OPEN);
        } else {
            bean.setAudioStatus(Key.AUDIO_CLOSE);
        }
    }

    public void setOnAudioStateChangeListener(OnAudioStateChangeListener onAudioStateChangeListener) {
        this.onAudioStateChangeListener = onAudioStateChangeListener;
    }

    public String getMicState() {
        return micState;
    }

    public void setMicState(String micState) {
        this.micState = micState;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    public boolean isSound() {
        return isSound;
    }

    public void setSound(boolean sound) {
        isSound = sound;
    }
}
