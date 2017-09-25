//package com.tydic.cloudmeeting.model;
//
//import android.content.Context;
//
//import com.bairuitech.anychat.AnyChatBaseEvent;
//import com.bairuitech.anychat.AnyChatCoreSDK;
//import com.bairuitech.anychat.AnyChatDefine;
//import com.bairuitech.anychat.AnyChatObjectDefine;
//import com.bairuitech.anychat.AnyChatObjectEvent;
//import com.tydic.cloudmeeting.config.Config;
//import com.tydic.cloudmeeting.config.ConfigEntity;
//import com.tydic.cloudmeeting.config.ConfigService;
//import com.tydic.cloudmeeting.overwrite.ECProgressDialog;
//
///**
// * AnyChat初始化、连接和登录
// * Created by like on 2017-09-18
// */
//
//public class AnyChatInit {
//
//    private AnyChatCoreSDK anyChat;
//
//    private ECProgressDialog progressDialog;//弹窗
//
//    private Context mContext;
//
//    private final int LOCALVIDEOAUTOROTATION = 1;    // 本地视频自动旋转控制
//
//    private int USER_TYPE_ID = 0;                    //0代表是进入客户界面，2代表是接入座席界面
//
//    public AnyChatInit(AnyChatCoreSDK anyChatCoreSDK, Context context) {
//        this.anyChat = anyChatCoreSDK;
//        this.mContext = context;
//        init(anyChatCoreSDK);
//    }
//
//    /**
//     * 初始化弹窗，和AnyChatCoreSDK的基本属性
//     *
//     * @param anyChat
//     */
//    private void init(AnyChatCoreSDK anyChat) {
//        if (anyChat == null) {
//            return;
//        }
//        progressDialog = new ECProgressDialog(mContext);
//        progressDialog.setPressText("加载中...");
//        progressDialog.setCancelable(true);
//        progressDialog.show();
//
//        anyChat.mSensorHelper.InitSensor(mContext);
//        AnyChatCoreSDK.mCameraHelper.SetContext(mContext);
//
//        anyChat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);//初始化sdk
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, LOCALVIDEOAUTOROTATION);
//
//    }
//
//    /**
//     * 关闭弹窗
//     */
//    public void dismiss() {
//        progressDialog.dismiss();
//    }
//
//    /**
//     * 连接服务器
//     */
//    public void connect() {
//        anyChat.Connect(Config.mStrIP, Config.mSPort);
//    }
//
//    /**
//     * 销毁
//     */
//    public void onDestroy() {
//        anyChat.LeaveRoom(-1);
//        anyChat.Logout();
//        anyChat.Release();
//    }
//
//    /**
//     * 重新开始
//     */
//    public void onRestart() {
//        anyChat.SetBaseEvent((AnyChatBaseEvent) mContext);
//        anyChat.SetObjectEvent((AnyChatObjectEvent) mContext);
//    }
//
//    /**
//     * 连接关闭
//     */
//    public void onClose() {
//        anyChat.LeaveRoom(-1);
//        anyChat.Logout();
//        anyChat.R
//    }
//
//    /**
//     * 初始化服务对象事件；触发回调OnAnyChatObjectEvent函数
//     *
//     * @param dwUserId
//     */
//    public void initClientObjectInfo(int dwUserId) {
//        // 业务对象身份初始化；0代表普通客户，2是代表座席 (USER_TYPE_ID)
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_OBJECT_INITFLAGS, USER_TYPE_ID);
//        // 业务对象优先级设定；
//        int dwPriority = 10;
//        AnyChatCoreSDK.ObjectSetIntValue(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_CLIENTUSER, dwUserId,
//                AnyChatObjectDefine.ANYCHAT_OBJECT_INFO_PRIORITY, dwPriority);
//        // 业务对象属性设定,必须是-1；
//        int dwAttribute = -1;
//        AnyChatCoreSDK.ObjectSetIntValue(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_CLIENTUSER, dwUserId,
//                AnyChatObjectDefine.ANYCHAT_OBJECT_INFO_ATTRIBUTE, dwAttribute);
//        // 向服务器发送数据同步请求指令
//        AnyChatCoreSDK.ObjectControl(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_AREA,
//                AnyChatObjectDefine.ANYCHAT_INVALID_OBJECT_ID,
//                AnyChatObjectDefine.ANYCHAT_OBJECT_CTRL_SYNCDATA, dwUserId, 0,
//                0, 0, "");
//    }
//
//    /**
//     * 有关视频的参数配置
//     */
//    public void applyVideoConfig() {
//        ConfigEntity configEntity = ConfigService.LoadConfig(mContext);
//        // 自定义视频参数配置
//        if (configEntity.configMode == 1) {
//            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, configEntity.videoBitrate);
//            if (configEntity.videoBitrate == 0) {
//                // 设置本地视频编码的质量
//                AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, configEntity.videoQuality);
//            }
//            // 设置本地视频编码的帧率
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, configEntity.videoFps);
//            // 设置本地视频编码的关键帧间隔
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, configEntity.videoFps * 4);
//            // 设置本地视频采集分辨率
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, 640); //configEntity.resolution_width
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, 480); //configEntity.resolution_height
//            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
//            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, configEntity.videoPreset);
//        }
//        // 让视频参数生效
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, configEntity.configMode);
//        // P2P设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC, configEntity.enableP2P);
//        // 本地视频Overlay模式设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY, configEntity.videoOverlay);
//        // 回音消除设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, configEntity.enableAEC);
//        // 平台硬件编码设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC, configEntity.useHWCodec);
//        // 视频旋转模式设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL, configEntity.videorotatemode);
//        // 本地视频采集偏色修正设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA, configEntity.fixcolordeviation);
//        // 视频GPU渲染设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER, configEntity.videoShowGPURender);
//        // 本地视频自动旋转设置
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, configEntity.videoAutoRotation);
//    }
//
//}

package com.tydic.cloudmeeting.model;

import android.content.Context;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.tydic.cloudmeeting.config.Config;
import com.tydic.cloudmeeting.config.ConfigEntity;
import com.tydic.cloudmeeting.config.ConfigService;
import com.tydic.cloudmeeting.constant.Key;
import com.tydic.cloudmeeting.overwrite.ECProgressDialog;
import com.tydic.cloudmeeting.util.CacheUtil;


/**
 * @Description: AnyChat初始化、连接和登录
 * @author: cyq7on
 * @date: 2017/5/3 11:48
 * @version: V1.0
 */

public class AnyChatInit implements AnyChatBaseEvent, AnyChatObjectEvent {

    private String mStrName;
    private String mStrPwd;
    private AnyChatCoreSDK anyChatCoreSDK;
    private final int SHOWLOGINSTATEFLAG = 1;        // 显示的按钮是登陆状态的标识
    private final int SHOWWAITINGSTATEFLAG = 2;    // 显示的按钮是等待状态的标识
    private final int LOCALVIDEOAUTOROTATION = 1;    // 本地视频自动旋转控制
    private final int ACTIVITY_ID_MAINUI = 1;        // MainActivity的id标致，onActivityResult返回
    private int USER_TYPE_ID;                    //0代表是进入客户界面，2代表是接入座席界面
    private Context mContext;

    private LoginCallBack loginCallBack;
    private final ECProgressDialog progressDialog;


    public interface LoginCallBack {
        void success(int userId);

        void fail(int errorCode);
    }

    public AnyChatInit(Context context, LoginCallBack loginCallBack) {
        this.mContext = context;
        progressDialog = new ECProgressDialog(mContext);
        progressDialog.setPressText("加载中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        this.loginCallBack = loginCallBack;
        mStrName = CacheUtil.get(mContext).getAsString(Key.EMPNAME);
        mStrPwd = CacheUtil.get(mContext).getAsString(Key.PASSWORD);
        if (anyChatCoreSDK == null) {
            anyChatCoreSDK = AnyChatCoreSDK.getInstance(mContext);
        }
        anyChatCoreSDK.SetBaseEvent(this);//基本事件
        anyChatCoreSDK.SetObjectEvent(this);//营业厅排队事件
        anyChatCoreSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);//初始化sdk

        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, LOCALVIDEOAUTOROTATION);
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,1);
        anyChatCoreSDK.Connect(Config.mStrIP, Config.mSPort);
    }

    public void onDestroy() {
        anyChatCoreSDK.LeaveRoom(-1);
        anyChatCoreSDK.Logout();
        anyChatCoreSDK.Release();
    }

    public void onClose(){
        anyChatCoreSDK.LeaveRoom(-1);
        anyChatCoreSDK.Logout();
    }


    public void onRestart() {
        anyChatCoreSDK.SetBaseEvent(this);
        anyChatCoreSDK.SetObjectEvent(this);
    }

    @Override
    public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2, int dwParam3, int dwParam4, String strParam) {

    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if (bSuccess) {
            anyChatCoreSDK.Login(mStrName, mStrPwd);
            return;
        }
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            //初始化业务对象属性身份
            InitClientObjectInfo(dwUserId);
            CacheUtil.get(mContext).put(Key.ANYCHAT_USER_ID, dwUserId + "");
            if (loginCallBack != null) {
                loginCallBack.success(dwUserId);
                ApplyVideoConfig();
            }
        } else {
            if (loginCallBack != null) {
                loginCallBack.fail(dwErrorCode);
            }
        }
        progressDialog.dismiss();
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        anyChatCoreSDK.LeaveRoom(-1);
        anyChatCoreSDK.Logout();
    }

    /**
     * 根据配置文件配置视频参数

     */
    private void ApplyVideoConfig(){
        ApplyVideoConfig(960,540);
    }

    /**
     * 根据配置文件配置视频参数
     * @param width 视频采集的宽
     * @param height    视频采集的高
     */
    private void ApplyVideoConfig(int width,int height) {
        ConfigEntity configEntity = ConfigService.LoadConfig(mContext);
        // 自定义视频参数配置
       if (configEntity.configMode == 1) {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, configEntity.videoBitrate);
            if (configEntity.videoBitrate == 0) {
                // 设置本地视频编码的质量
                AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, configEntity.videoQuality);
            }
            // 设置本地视频编码的帧率
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, configEntity.videoFps);
            // 设置本地视频编码的关键帧间隔
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, configEntity.videoFps * 4);
            // 设置本地视频采集分辨率
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, width); //configEntity.resolution_width
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, height); //configEntity.resolution_height
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, configEntity.videoPreset);
        }
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, configEntity.configMode);
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC, configEntity.enableP2P);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY, configEntity.videoOverlay);
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, configEntity.enableAEC);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC, configEntity.useHWCodec);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL, configEntity.videorotatemode);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA, configEntity.fixcolordeviation);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER, configEntity.videoShowGPURender);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, configEntity.videoAutoRotation);
    }

    // 初始化服务对象事件；触发回调OnAnyChatObjectEvent函数
    private void InitClientObjectInfo(int dwUserId) {
        // 业务对象身份初始化；0代表普通客户，2是代表座席 (USER_TYPE_ID)
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_OBJECT_INITFLAGS, USER_TYPE_ID);
        // 业务对象优先级设定；
        int dwPriority = 10;
        AnyChatCoreSDK.ObjectSetIntValue(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_CLIENTUSER, dwUserId,
                AnyChatObjectDefine.ANYCHAT_OBJECT_INFO_PRIORITY, dwPriority);
        // 业务对象属性设定,必须是-1；
        int dwAttribute = -1;
        AnyChatCoreSDK.ObjectSetIntValue(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_CLIENTUSER, dwUserId,
                AnyChatObjectDefine.ANYCHAT_OBJECT_INFO_ATTRIBUTE, dwAttribute);
        // 向服务器发送数据同步请求指令
        AnyChatCoreSDK.ObjectControl(AnyChatObjectDefine.ANYCHAT_OBJECT_TYPE_AREA,
                AnyChatObjectDefine.ANYCHAT_INVALID_OBJECT_ID,
                AnyChatObjectDefine.ANYCHAT_OBJECT_CTRL_SYNCDATA, dwUserId, 0,
                0, 0, "");
    }
}

