package com.tydic.cm.model;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tydic.cm.CommonActivity;
import com.tydic.cm.OnLiveActivity;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.util.CacheUtil;
import com.tydic.cm.util.L;

/**
 * React-Native与原生通信的桥梁
 * Created by like on 2017-09-20
 */

public class JsAndroidModule extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME = "JsAndroid";
    private static ReactApplicationContext mContext;

    public JsAndroidModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void jsActivity(String empName, String passWord, String roomId, String meetingId, String feedId,
                           String initiator, String created_by, int isBroadcastMode) {

        CacheUtil.get(getCurrentActivity()).put(Key.EMPNAME, empName);
        CacheUtil.get(getCurrentActivity()).put(Key.PASSWORD, passWord);
        CacheUtil.get(getCurrentActivity()).put(Key.USER_ID, feedId);
        JsParamsBean bean = new JsParamsBean();
        bean.setEmpName(empName);
        bean.setPassWord(passWord);
        bean.setRoomId(Integer.valueOf(roomId));
        bean.setMeetingId(meetingId);
        bean.setFeedUserName(empName);
        bean.setFeedId(feedId);
        bean.setInitiator(initiator);
        bean.setCreated_by(created_by);
        bean.setIsBroadcastMode(isBroadcastMode + "");
        L.d("会议视频", "React-native传递过来的参数：" + bean.toString());
        Intent intent;
        if (isBroadcastMode == 0) {
            intent = new Intent(getCurrentActivity(), CommonActivity.class);
        } else {
            intent = new Intent(getCurrentActivity(), OnLiveActivity.class);
        }
        intent.putExtra(Key.JS_PARAMS, bean);
        getCurrentActivity().startActivity(intent);
    }

    public static void sendEvent(String name) {
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, "meeting");
    }
}
