package com.tydic.cloudmeeting.constant;

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

    public static final String EMPNAME = "EMPNAME";
    //登录用户
    public final static String USER_ID = "USER_ID";
    //登录密码
    public final static String PASSWORD = "PASSWORD";
    public final static String ANYCHAT_USER_ID = "anychat_user_id";

}
