package com.tydic.cloudmeeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cloudmeeting.base.BaseActivity;
import com.tydic.cloudmeeting.bean.UsersBean;
import com.tydic.cloudmeeting.constant.Key;
import com.tydic.cloudmeeting.overwrite.MeetingMenuPop;
import com.tydic.cloudmeeting.overwrite.OnlinePop;
import com.tydic.cloudmeeting.util.L;
import com.tydic.cloudmeeting.util.T;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.tydic.cloudmeeting.constant.Key.CLIENT_NOTICE_PRIMARY_SPEAKER;

/**
 * 普通视频模式
 */
public class CommonActivity extends BaseActivity implements View.OnClickListener, MeetingMenuPop.MenuClickListener {


    private LinearLayout topLayout, bottomLayout;
    /**
     * 显示视频控件
     */
    private SurfaceView selfSurface, surface2, surface3, surface4, surface5, surface6, surface7, surface8;

    private RelativeLayout rootView;

    private ImageView transcribe, camera, microphone, sound, menu;//视频录制，摄像头切换，本地麦克风，远程麦克风，菜单

    private List<Integer> userIDList = new ArrayList<>();//远程的ID

    private SurfaceView[] surfaceViews;//显示远程视频的承载

    boolean isSpeaking = true;//是否可以说话

    boolean isSound = true;//是否有声音

    boolean isOpenCamera = true;

    private MeetingMenuPop meetingMenuPop;

    private OnlinePop mOnlinePop;

    private List<UsersBean> onLineUsers = new ArrayList<>();

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        meetingMenuPop = new MeetingMenuPop(mContext, mJsParamsBean.getMeetingId(), mJsParamsBean.getCreated_by(), Integer.parseInt(mJsParamsBean.getIsBroadcastMode()));
        meetingMenuPop.setMenuClickListener(this);
        mOnlinePop = new OnlinePop(this, mJsParamsBean);
        mOnlinePop.onLineUser();
        initView();
        initSelfSurface();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        topLayout = (LinearLayout) findViewById(R.id.top);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom);
        selfSurface = (SurfaceView) findViewById(R.id.self_surface);
        surface2 = (SurfaceView) findViewById(R.id.surface2);
        surface3 = (SurfaceView) findViewById(R.id.surface3);
        surface4 = (SurfaceView) findViewById(R.id.surface4);
        surface5 = (SurfaceView) findViewById(R.id.surface5);
        surface6 = (SurfaceView) findViewById(R.id.surface6);
        surface7 = (SurfaceView) findViewById(R.id.surface7);
        surface8 = (SurfaceView) findViewById(R.id.surface8);
        rootView = (RelativeLayout) findViewById(R.id.root);
        transcribe = (ImageView) findViewById(R.id.meeting_transcribe);
        transcribe.setOnClickListener(this);
        camera = (ImageView) findViewById(R.id.meeting_camera);
        camera.setOnClickListener(this);
        microphone = (ImageView) findViewById(R.id.meeting_microphone);
        microphone.setOnClickListener(this);
        sound = (ImageView) findViewById(R.id.meeting_sound);
        sound.setOnClickListener(this);
        menu = (ImageView) findViewById(R.id.meeting_menu);
        menu.setOnClickListener(this);
        //刚进来先显示自己画面
        showSelf();
    }

    /**
     * 根据当前获取参会人员的数量来显示视频数，最大8频
     *
     * @param size
     */
    private void showSurface(int size) {
        size++;
        selfSurface.setVisibility(View.VISIBLE);
        if (size > 8) {
            surfaceViews = new SurfaceView[8];
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            surfaceViews[3] = surface4;
            surfaceViews[4] = surface5;
            surfaceViews[5] = surface6;
            surfaceViews[6] = surface7;
            surfaceViews[7] = surface8;
            selfSurface.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.VISIBLE);
            surface4.setVisibility(View.VISIBLE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.VISIBLE);
            surface8.setVisibility(View.VISIBLE);
            return;
        }
        surfaceViews = new SurfaceView[size];
        if (size == 1) {
            //只显示自己
            surfaceViews[0] = selfSurface;
            bottomLayout.setVisibility(View.GONE);
            surface2.setVisibility(View.GONE);
            surface3.setVisibility(View.GONE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.GONE);
            surface6.setVisibility(View.GONE);
            surface7.setVisibility(View.GONE);
            surface8.setVisibility(View.GONE);
        } else if (size == 2) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.GONE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.INVISIBLE);
            surface6.setVisibility(View.INVISIBLE);
            surface7.setVisibility(View.GONE);
            surface8.setVisibility(View.GONE);
        } else if (size == 3) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.GONE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.INVISIBLE);
            surface7.setVisibility(View.GONE);
            surface8.setVisibility(View.GONE);
        } else if (size == 4) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface5;
            surfaceViews[3] = surface6;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.GONE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.GONE);
            surface8.setVisibility(View.GONE);
        } else if (size == 5) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            surfaceViews[3] = surface5;
            surfaceViews[4] = surface6;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.VISIBLE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.INVISIBLE);
            surface8.setVisibility(View.GONE);
        } else if (size == 6) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            surfaceViews[3] = surface5;
            surfaceViews[4] = surface6;
            surfaceViews[5] = surface7;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.VISIBLE);
            surface4.setVisibility(View.GONE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.VISIBLE);
            surface8.setVisibility(View.GONE);
        } else if (size == 7) {
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            surfaceViews[3] = surface4;
            surfaceViews[4] = surface5;
            surfaceViews[5] = surface6;
            surfaceViews[6] = surface7;
            bottomLayout.setVisibility(View.VISIBLE);
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.VISIBLE);
            surface4.setVisibility(View.VISIBLE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.VISIBLE);
            surface8.setVisibility(View.INVISIBLE);
        } else if (size == 8) {
            bottomLayout.setVisibility(View.VISIBLE);
            surfaceViews[0] = selfSurface;
            surfaceViews[1] = surface2;
            surfaceViews[2] = surface3;
            surfaceViews[3] = surface4;
            surfaceViews[4] = surface5;
            surfaceViews[5] = surface6;
            surfaceViews[6] = surface7;
            surfaceViews[7] = surface8;
            surface2.setVisibility(View.VISIBLE);
            surface3.setVisibility(View.VISIBLE);
            surface4.setVisibility(View.VISIBLE);
            surface5.setVisibility(View.VISIBLE);
            surface6.setVisibility(View.VISIBLE);
            surface7.setVisibility(View.VISIBLE);
            surface8.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示主讲人
     */
    private void showMainSpeaker() {
        topLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
        selfSurface.setVisibility(View.GONE);
        surface2.setVisibility(View.VISIBLE);
        surface3.setVisibility(View.GONE);
        surface4.setVisibility(View.GONE);
        surface5.setVisibility(View.GONE);
        surface6.setVisibility(View.GONE);
        surface7.setVisibility(View.GONE);
        surface8.setVisibility(View.GONE);
    }

    /**
     * 只显示自己
     */
    private void showSelf() {
        topLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
        selfSurface.setVisibility(View.VISIBLE);
        surface2.setVisibility(View.GONE);
        surface3.setVisibility(View.GONE);
        surface4.setVisibility(View.GONE);
        surface5.setVisibility(View.GONE);
        surface6.setVisibility(View.GONE);
        surface7.setVisibility(View.GONE);
        surface8.setVisibility(View.GONE);
    }

    /**
     * 初始化自己的视频
     */
    private void initSelfSurface() {
        // 视频如果是采用java采集
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            selfSurface.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
        }

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

        anychat.UserSpeakControl(-1, 1);
        anychat.UserCameraControl(-1, 1);

    }

    /**
     * 初始化远程视频
     */
    private void initNetSurface() {
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
        if (userIDList.size() != surfaceViews.length - 1) {
            return;
        }
        for (int i = 0; i < userIDList.size(); i++) {
            int index = anychat.mVideoHelper.bindVideo(surfaceViews[i + 1].getHolder());
            anychat.mVideoHelper.SetVideoUser(index, userIDList.get(i));
            anychat.UserCameraControl(userIDList.get(i), 1);
            //解决在关闭远程声音下有人进出房间时又能听见远程声音
            if (isSound) {
                anychat.UserSpeakControl(userIDList.get(i), 1);
            } else {
                anychat.UserSpeakControl(userIDList.get(i), 0);
            }
        }

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_common;
    }


    @Override
    public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
        try {
            String receiveMsg = new String(lpBuf, "utf-8");
            L.d(TAG, "OnAnyChatTransBuffer:lpBuf=" + receiveMsg + ",dwLen=" + dwLen);
            //获取用户状态
            mRetrofitMo.userState(mJsParamsBean.getRoomId(), mJsParamsBean.getFeedId(), this);

            if (receiveMsg.contains(" ")) {
                return;
            } else {
                switch (Integer.parseInt(receiveMsg)) {
                    case Key.CLIENT_DISABLE_MIC:
                        T.showShort("您的麦克风被管理员关闭");
                        isSpeaking = true;
                        microphone.performClick();
                        break;
                    case Key.CLIENT_ENAble_MIC:
                        T.showShort("您的麦克风被管理员打开");
                        isSpeaking = false;
                        microphone.performClick();
                        break;
                    case Key.CLIENT_DISABLE_VIDEO:
                        T.showShort("您的摄像头被管理员关闭");
                        isOpenCamera = true;
                        transcribe.performClick();
                        break;
                    case Key.CLIENT_ENAble_VIDEO:
                        T.showShort("您的摄像头被管理员打开");
                        isOpenCamera = false;
                        transcribe.performClick();
                        break;
                    default:
                        break;
                }
            }
            feedbackState(Key.UPDATE_CLIENT_STATUS);
            if (mOnlinePop != null && mOnlinePop.isShowing()) {
                mOnlinePop.onLineUser();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收过滤的消息
     *
     * @param lpBuf
     * @param dwLen
     */
    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
        try {
            String receiveMsg = new String(lpBuf, "utf-8");
            L.d(TAG, "OnAnyChatSDKFilterData:lpBuf=" + receiveMsg + ",dwLen=" + dwLen);
            String[] arr = receiveMsg.split(" ");
            switch (arr[0]) {
                case CLIENT_NOTICE_PRIMARY_SPEAKER + "":
                    //userId为0表示取消主讲人
                    if ("0".equals(arr[1])) {
                        T.showShort("已取消主讲人");
                        int[] onlineUserCount = AnyChatCoreSDK.getInstance(this).GetOnlineUser();
                        //显示所有人画面
                        userIDList.clear();
                        for (int i = 0; i < onlineUserCount.length; i++) {
                            userIDList.add(onlineUserCount[i]);
                        }
                        showSurface(userIDList.size());
                        initNetSurface();

                    } else {
                        int speaker = Integer.parseInt(arr[1]);
                        if (speaker == selfUserId) {
                            T.showShort("你被设置为主讲人了！");
                            //主讲人时自己
                            showSelf();
                        } else {
                            //主讲人是别人
                            userIDList.clear();
                            for (UsersBean onLineUser : onLineUsers) {
                                if (onLineUser.getUserId().equals(arr[1] + "")) {
                                    T.showShort(onLineUser.getNickName() + "被设置为主讲人了！");
                                }
                            }
                            userIDList.add(speaker);
                            showMainSpeaker();
                            initNetSurface();
                        }
                    }
                    break;
                default:
                    break;
            }
            if (mOnlinePop != null && mOnlinePop.isShowing()) {
                mOnlinePop.onLineUser();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        L.d(TAG, "OnAnyChatEnterRoomMessage:dwRoomId=" + dwRoomId + ",dwErrorCode=" + dwErrorCode);
        if (dwErrorCode == 0) {
            //打开本地音视频
            anychat.UserCameraControl(-1, 1);
            anychat.UserSpeakControl(-1, 1);
            //获取用户状态
            mRetrofitMo.userState(mJsParamsBean.getRoomId(), mJsParamsBean.getFeedId(), this);
            //获取在线人员
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
            feedbackState(Key.UPDATE_CLIENT_STATUS);
            int[] onlineUserCount = AnyChatCoreSDK.getInstance(this).GetOnlineUser();
            userIDList.clear();
            for (int i = 0; i < onlineUserCount.length; i++) {
                userIDList.add(onlineUserCount[i]);
            }
            showSurface(userIDList.size());
            initNetSurface();

            initAudio();
        } else {
            T.showShort("进入会议房间失败，即将退出！");
        }
    }


    /**
     * 当前房间用户离开或者进入房间触发这个回调，dwUserId用户  id," bEnter==true"表示进入房间,反之表示离开房间
     *
     * @param dwUserId
     * @param bEnter
     */
    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        L.d(TAG, "OnAnyChatUserAtRoomMessage:dwUserId=" + dwUserId + ",bEnter=" + bEnter);
        //获取在线人员,每当有人进入或退出时获取一次
        mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        if (bEnter) {
            //有人进入房间
            enterRoom(dwUserId);
            showSurface(userIDList.size());
            initNetSurface();
        } else {
            //有人退出房间
            exitRoom(dwUserId);
            showSurface(userIDList.size());
            initNetSurface();
        }
    }

    /**
     * 有人进入房间
     *
     * @param dwUserId
     * @return
     */
    private void enterRoom(int dwUserId) {
        if (!userIDList.contains(dwUserId)) {
            userIDList.add(dwUserId);
        }
    }

    /**
     * 有人退出房间
     *
     * @param dwUserId
     * @return
     */
    private void exitRoom(int dwUserId) {
        if (userIDList.contains(dwUserId)) {
            int index = userIDList.indexOf(dwUserId);
            userIDList.remove(index);
        }
    }

    /**
     * 跟服务器网络断触发该消息。收到该消息后可以关闭音视频以及做相关提示工作
     *
     * @param dwErrorCode
     */
    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        T.showShort("网络断开连接,即将退出房间！");
        mAnyChatInit.onDestroy();
        if (meetingMenuPop != null && meetingMenuPop.isShowing()) {
            meetingMenuPop.dismiss();
        }
        if (mOnlinePop != null && mOnlinePop.isShowing()) {
            mOnlinePop.dismiss();
        }
        finish();
    }

    @Override
    public void onSuccess(int type, Object obj) {
        switch (type) {
            case Key.USER_STATE:
                //获取用户状态
                userState = (UsersBean) obj;
                L.d(TAG, "用户状态：" + userState.toString());
                break;
            case Key.ON_LINE_USER:
                //获取在线人员列表
                L.d(TAG, "在线人员列表：" + obj.toString());
                onLineUsers = (List<UsersBean>) obj;
                break;
            case Key.SEND_MESSAGE:
                //发送消息
                L.d(TAG, "发送信息：" + obj.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(int type, int code) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.meeting_transcribe) {
            //视频录制
        } else if (id == R.id.meeting_camera) {
            //摄像头的打开和关闭
            AnyChatCoreSDK.mCameraHelper.SwitchCamera();
            feedbackState(Key.UPDATE_CLIENT_STATUS);
        } else if (id == R.id.meeting_microphone) {
            //本地声音
            if (isSpeaking) {
                userState.setAudioStatus("0");
                micState = "0";
                feedbackState(Key.UPDATE_CLIENT_STATUS);
                microphone.setImageResource(R.drawable.meeting_microphone_disable);
                AnyChatCoreSDK.getInstance(this).UserSpeakControl(-1, 0);

//                String mReleaseMic = anyChatUserId + "|0";
//                AnyChatCoreSDK.getInstance(this).TransBuffer(0, mReleaseMic.getBytes(), mReleaseMic.length());
            } else {
                microphone.setImageResource(R.drawable.img_meeting_microphone);
                userState.setAudioStatus("1");
                micState = "1";
                feedbackState(Key.UPDATE_CLIENT_STATUS);
//                String mRequestMic = anyChatUserId + "|1";
                AnyChatCoreSDK.getInstance(this).UserSpeakControl(-1, 1);
//                AnyChatCoreSDK.getInstance(this).TransBuffer(0, mRequestMic.getBytes(), mRequestMic.length());
            }
            isSpeaking = !isSpeaking;
        } else if (id == R.id.meeting_sound) {
            //远程声音
            int[] onlineUserCount = AnyChatCoreSDK.getInstance(this).GetOnlineUser();
            int size = onlineUserCount.length;
            if (isSound) {
                sound.setImageResource(R.drawable.meeting_speaker_disable);
                for (int i = 0; i < size; i++) {
                    anychat.UserSpeakControl(onlineUserCount[i], 0);
                }
            } else {
                sound.setImageResource(R.drawable.img_meeting_sound);
                for (int i = 0; i < size; i++) {
                    anychat.UserSpeakControl(onlineUserCount[i], 1);
                }
            }
            isSound = !isSound;
        }
        if (id == R.id.meeting_menu) {
            //菜单键
            meetingMenuPop.showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                    RelativePopupWindow.HorizontalPosition.CENTER, false);
        }
    }

    @Override
    public void onClick(int index) {
        meetingMenuPop.dismiss();
        switch (index) {
            case 0:
                //打开侧边弹窗
                mOnlinePop.onLineUser();
                mOnlinePop.showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.ALIGN_TOP,
                        RelativePopupWindow.HorizontalPosition.RIGHT, true);
                break;
            case 3:
                finish();
                break;
            default:
                break;
        }
    }
}
