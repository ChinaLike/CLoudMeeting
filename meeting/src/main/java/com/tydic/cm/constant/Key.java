package com.tydic.cm.constant;

/**
 * 一些常量
 * Created by like on 2017-09-18
 */

public interface Key {

    String JS_PARAMS = "params";

    int SUCCESS = 0;//成功

    int FAIL = 1;//失败

    int OVER_TIME = -1;//超时

    int ON_LINE_USER = 0;//在线人员

    int SEND_MESSAGE = 1;//发送消息

    int USER_STATE = 2;//用户状态

    int UPDATE_CLIENT_STATUS = 1001;
    int CLIENT_DISABLE_MIC = 1002;
    int CLIENT_ENAble_MIC = 1003;
    int CLIENT_DISABLE_VIDEO = 1004;
    int CLIENT_ENAble_VIDEO = 1005;
    int CLIENT_SET_PRIMARY_SPEAKER = 1006;
    int CLIENT_NOTICE_PRIMARY_SPEAKER = 1007;
    int CLIENT_RESET_PRIMARY_SPEAKER = 1008;

    String EMPNAME = "EMPNAME";
    //登录用户
    String USER_ID = "USER_ID";
    //登录密码
    String PASSWORD = "PASSWORD";
    String ANYCHAT_USER_ID = "anychat_user_id";

    int COLUMN_ONE = 1;//远程视频显示1列
    int COLUMN_TWO = 2;//远程视频显示2列
    int COLUMN_THREE = 3;//远程视频显示3列
    int COLUMN_FOUR = 4;//远程视频显示4列

    String VIDEO_OTHER = "-1";//人员已经退出，恢复摄像头初始状态
    String VIDEO_NO = "0";//没有摄像头
    String VIDEO_CLOSE = "1";//有摄像头但是是关闭状态
    String VIDEO_OPEN = "2";//摄像头打开状态

    String AUDIO_CLOSE = "0";//语音关闭
    String AUDIO_OPEN = "1";//语音打开

    String SPEAKER = "1";//是主讲人
    String NO_SPEAKER = "0";//不是主讲人
    String IS_START = "IS_START";
}
