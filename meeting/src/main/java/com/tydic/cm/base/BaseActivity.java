package com.tydic.cm.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatVideoCallEvent;
import com.google.gson.Gson;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.AnyChatInit;
import com.tydic.cm.model.Audio;
import com.tydic.cm.model.RetrofitMo;
import com.tydic.cm.model.UserMo;
import com.tydic.cm.model.Video;
import com.tydic.cm.model.inf.OnRequestListener;
import com.tydic.cm.overwrite.MeetingMenuPop;
import com.tydic.cm.util.CacheUtil;
import com.tydic.cm.util.ConvertUtil;
import com.tydic.cm.util.PermissionUtils;
import com.tydic.cm.util.T;

import java.util.HashMap;
import java.util.Map;

/**
 * 基类Activity
 * Created by like on 2017-09-20
 */

public abstract class BaseActivity extends AppCompatActivity implements AnyChatInit.LoginCallBack,
        OnRequestListener, AnyChatBaseEvent, AnyChatObjectEvent, AnyChatVideoCallEvent, AnyChatTransDataEvent {

    protected static final String TAG = "视频会议";

    protected JsParamsBean mJsParamsBean;//RN-传递过来的数据

    protected Context mContext;
    /**
     * AnyChatCoreSDK 类是 SDK 的核心类，提供各种功能接口，如连接服务
     * 器、登录、进入房间、操作音视频等。在使用这些功能接口构建应用之前，需
     * 要准备一个 AnyChatCoreSDK 对象，这个对象可以是单例模式，在其他 activity
     * 中共享，也可以在每个 activity 中都新建一个
     */
    protected AnyChatCoreSDK anychat;
    /**
     * 获取数据Model
     */
    protected RetrofitMo mRetrofitMo;
    /**
     * anyChat帮助类
     */
    protected AnyChatInit mAnyChatInit;
    /**
     * 本机用户信息
     */
    protected UsersBean userState = new UsersBean();
    /**
     * 用户一些数据操作
     */
    protected UserMo mUserMo;
    /**
     * 本机用户Id
     */
    protected int selfUserId;

    protected String anyChatUserId;

    protected int displayMode = 1;
    /**
     * 最大一页视频显示数目
     */
    public final static int MAX_VIDEO_SHOW_NUMBER = 4;

    /**
     * 音频控制
     */
    protected Audio audio;
    /**
     * 视频控制
     */
    protected Video video;

    protected MeetingMenuPop meetingMenuPop;

    //权限组
    private String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        mJsParamsBean = (JsParamsBean) getIntent().getSerializableExtra(Key.JS_PARAMS);
        setContentView(setLayout());
        mContext = this;
        init();
        init(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 22) {
            //先判断系统并请求权限
            if (!PermissionUtils.checkPermissionAllGranted(permissions, this)) {
                PermissionUtils.requestPermission(permissions, this);
            }
        }
    }

    /**
     * 一些参数初始化
     */
    private void init() {
        T.init(getApplicationContext());
        ConvertUtil.init(getApplicationContext());
        mRetrofitMo = new RetrofitMo(mContext);
        mUserMo = new UserMo();
        //初始化sdk
        if (anychat == null) {
            anychat = AnyChatCoreSDK.getInstance(this);
        }
        mAnyChatInit = new AnyChatInit(mContext, this);

    }


    protected abstract void init(@Nullable Bundle savedInstanceState);

    protected abstract int setLayout();

    /**
     * 初始化Activity的显示模式
     */
    private void initStatus() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    }

    @Override
    public void success(int userId) {
        T.showShort("加载成功");
        anyChatUserId = CacheUtil.get(mContext).getAsString(Key.ANYCHAT_USER_ID);
        selfUserId = userId;
        if (meetingMenuPop != null) {
            meetingMenuPop.setUserId(userId + "");
        }
        initSDK();
        initAV();
    }

    @Override
    public void fail(int errorCode) {
        T.showShort("加载失败");
    }

    /**
     * 初始化音视频参数
     */
    private void initAV() {
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
                AnyChatCoreSDK.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
            }
        } else {
            String[] strVideoCaptures = anychat.EnumVideoCapture();
            if (strVideoCaptures != null && strVideoCaptures.length > 1) {
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    String strDevices = strVideoCaptures[i];
                    if (strDevices.indexOf("Front") >= 0) {
                        anychat.SelectVideoCapture(strDevices);
                        break;
                    }
                }
            }
        }

        AnyChatCoreSDK.getInstance(this).EnterRoom(mJsParamsBean.getRoomId(), "");

    }

    private void initSDK() {

        anychat.mSensorHelper.InitSensor(mContext);
        AnyChatCoreSDK.mCameraHelper.SetContext(mContext);
        audio = new Audio(anychat, this);
        video = new Video(anychat, this);
        feedbackState(Key.UPDATE_CLIENT_STATUS);
        anychat.SetBaseEvent(this);//基本登陆事件接口
        anychat.SetObjectEvent(this);//排队事件接口；
        anychat.SetTransDataEvent(this);
        anychat.SetVideoCallEvent(this);
    }

    /**
     * 用户状态信息
     *
     * @param code
     */
    protected void feedbackState(int code) {
        Map<String, String> params = new HashMap<>(7);
        params.put("userId", anyChatUserId);
        params.put("nickName", mJsParamsBean.getFeedUserName());
        params.put("meetingId", mJsParamsBean.getMeetingId());
        params.put("yhyUserId", mJsParamsBean.getFeedId());
        params.put("audioStatus", audio.getMicState());
        params.put("videoStatus", video.getVideoState());
        params.put("displayMode", displayMode + "");
        String info = new Gson().toJson(params);
        if (anyChatUserId.contains(" ")) {
            return;
        } else {
            AnyChatCoreSDK.getInstance(this).UserInfoControl(Integer.parseInt(anyChatUserId), code, 0, 0, info);
        }
    }

    /**
     * 初始化通用配置
     */
    protected void initSurfaceSDK() {
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
                AnyChatCoreSDK.mCameraHelper.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
            }
        } else {
            String[] strVideoCaptures = anychat.EnumVideoCapture();
            if (strVideoCaptures != null && strVideoCaptures.length > 1) {
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    String strDevices = strVideoCaptures[i];
                    if (strDevices.indexOf("Front") >= 0) {
                        anychat.SelectVideoCapture(strDevices);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 初始化本地
     *
     * @param selfSurface
     */
    protected void initLocalSurface(SurfaceView selfSurface) {
        // 视频如果是采用java采集
        selfSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            selfSurface.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        }
    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if (!bSuccess) {
            T.showShort("登录房间失败，正在重新进入！");
        }
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {

    }

    /**
     * 当前房间在线用户消息，进入房间成功后调用一次。dwUserNum当前房间总人数（包括自	己）
     *
     * @param dwUserNum
     * @param dwRoomId
     */
    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    /**
     * 业务对象回调事件，调用AnyChatCoreSDk.ObjectControl方法触发这个回调
     *
     * @param dwObjectType
     * @param dwObjectId
     * @param dwEventType
     * @param dwParam1
     * @param dwParam2
     * @param dwParam3
     * @param dwParam4
     * @param strParam
     */
    @Override
    public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2, int dwParam3, int dwParam4, String strParam) {

    }

    @Override
    public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid) {

    }

    @Override
    public void OnAnyChatTransFile(int dwUserid, String FileName, String TempFilePath, int dwFileLength, int wParam, int lParam, int dwTaskId) {

    }


    @Override
    public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAnyChatInit.onDestroy();
    }

    /**
     * 制造一个不显示视频的Bean
     *
     * @return
     */
    protected UsersBean emptyBean() {
        UsersBean emptyBean = new UsersBean();
        audio.refreshDistanceAudio(emptyBean);
        emptyBean.setVideoStatus(Key.VIDEO_CLOSE);
        emptyBean.setIsPrimarySpeaker(Key.NO_SPEAKER);
        emptyBean.setDisplayMode(0);
        emptyBean.setMeetingId(mJsParamsBean.getMeetingId());
        emptyBean.setUserId("0");
        return emptyBean;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted && anychat != null) {
                // 如果所有的权限都授予了, 则执行备份代码
                anychat.UserCameraControl(-1, 1);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                //
            }
        }
    }
}
