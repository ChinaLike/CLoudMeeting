package com.tydic.cm.bean;

import java.io.Serializable;

/**
 * 在线人员列表
 * Created by like on 2017-09-19
 */

public class UsersBean implements Serializable {

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    /**
     * userId : -1111
     * nickName : 阚巍_林芝
     * meetingId : 8486269380af421ab19ef03d19392f76
     * yhyUserId : 5428AA34AFF048F3803D3513636CA3A9
     * audioStatus : 1
     * videoStatus : 2
     * displayMode : 1
     * isPrimarySpeaker : 0
     */
    //复选框
    private boolean isChecked = false;

    private String userId;
    private String nickName;
    private String meetingId;
    private String yhyUserId;
    private String audioStatus;
    private String videoStatus;
    private Integer displayMode;

    private String isPrimarySpeaker;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getYhyUserId() {
        return yhyUserId;
    }

    public void setYhyUserId(String yhyUserId) {
        this.yhyUserId = yhyUserId;
    }

    public String getAudioStatus() {
        if (audioStatus == null || audioStatus.equals("")) {
            return "1";
        }
        return audioStatus;
    }

    public void setAudioStatus(String audioStatus) {
        this.audioStatus = audioStatus;
    }

    public String getVideoStatus() {
        if (videoStatus == null || videoStatus.equals("")) {
            return "2";
        }
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public Integer getDisplayMode() {
        if (displayMode == null) {
            return 1;
        }
        return displayMode;
    }

    public void setDisplayMode(Integer displayMode) {
        this.displayMode = displayMode;
    }

    public String getIsPrimarySpeaker() {
        return isPrimarySpeaker;
    }

    public void setIsPrimarySpeaker(String isPrimarySpeaker) {
        this.isPrimarySpeaker = isPrimarySpeaker;
    }

    @Override
    public String toString() {
        return "UsersBean{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", meetingId='" + meetingId + '\'' +
                ", yhyUserId='" + yhyUserId + '\'' +
                ", audioStatus='" + audioStatus + '\'' +
                ", videoStatus='" + videoStatus + '\'' +
                ", displayMode='" + displayMode + '\'' +
                ", isPrimarySpeaker='" + isPrimarySpeaker + '\'' +
                '}';
    }
}
