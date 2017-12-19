package com.tydic.cm;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cm.base.BaseActivity;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.overwrite.MeetingMenuPop;
import com.tydic.cm.overwrite.MenuLayout;
import com.tydic.cm.overwrite.OnlinePop;
import com.tydic.cm.util.L;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播视频模式
 */
public class OnLiveActivity extends BaseActivity implements View.OnClickListener, MeetingMenuPop.MenuClickListener, MenuLayout.MenuClickListener {
    private RelativeLayout.LayoutParams params;
    private SurfaceView mSurfaceView;

    private ImageView mImageView;

    private List<UsersBean> usersBeanList = new ArrayList<>();

//    private ImageView menu;
//    private ImageView user;

    private RelativeLayout rootView;

    private OnlinePop mOnlinePop;
    //    private String getVideoStatus;
//    private String getAudioStatus;
    //功能菜单
    private MenuLayout menuLayout;
    private Handler handler = new Handler();

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
//        getVideoStatus = getIntent().getStringExtra("getVideoStatus");
//        getAudioStatus = getIntent().getStringExtra("getAudioStatus");
//        mRetrofitMo.userState(mJsParamsBean.getRoomId(),mJsParamsBean.getFeedId(),this);
        meetingMenuPop.setMenuClickListener(this);
        mOnlinePop = new OnlinePop(this, mJsParamsBean);
        mOnlinePop.onLineUser();
        initView();
        initSDK();
//        Toast.makeText(this, "当前为直播会议,摄像头和麦克风已关闭!", Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        menuLayout = (MenuLayout) findViewById(R.id.menu_layout);
        menuLayout.setMenuClickListener(this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mImageView = (ImageView) findViewById(R.id.hint_image);
//        menu = (ImageView) findViewById(R.id.meeting_menu);
//        menu.setOnClickListener(this);
//        user = (ImageView) findViewById(R.id.meeting_user_list);
//        user.setOnClickListener(this);
        rootView = (RelativeLayout) findViewById(R.id.rootview);
        showSurface(false);
    }

    private void initSDK() {
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

    private void showSurface(boolean isShow) {
        if (isShow) {
            mSurfaceView.setZOrderMediaOverlay(false);
            mImageView.setVisibility(View.GONE);
            mSurfaceView.setVisibility(View.VISIBLE);
        } else {
//            mImageView.setVisibility(View.VISIBLE);
//            mImageView.setImageResource(R.drawable.img_meeting_wait);
//            mSurfaceView.setVisibility(View.GONE);
            params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            rootView.setBackgroundColor(0xFF000000);
            mSurfaceView.setVisibility(View.GONE);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mImageView.setLayoutParams(params);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageResource(R.drawable.img_meeting_wait);
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_on_live;
    }

    @Override
    public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2, int dwParam3, int dwParam4, String strParam) {

    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    /**
     * 显示远程视屏
     */
    private void showView(int userID) {
        int index = anychat.mVideoHelper.bindVideo(mSurfaceView.getHolder());
        anychat.mVideoHelper.SetVideoUser(index, userID);
        anychat.UserCameraControl(userID, 1);
        anychat.UserSpeakControl(userID, 1);
    }

    /**
     * 当前房间在线用户消息，进入房间成功后调用一次。dwUserNum当前房间总人数（包括自	己）
     *
     * @param dwUserNum
     * @param dwRoomId
     */
    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Log.d("OnAnyChatOnlineUser", dwUserNum + "  " + dwRoomId);
        if (dwUserNum == 1) {
            //只有自己
            showSurface(false);
        } else {
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
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
        try {
            Thread.sleep(500);
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

    }

    /**
     * 判断是否有主讲人
     */
    private void initSpeaker(List<UsersBean> list) {
        L.d("数据测试", list.toString());
        boolean hasSpeaker = false;
        for (UsersBean usersBean : list) {
            if (usersBean.getIsPrimarySpeaker().equals("1")) {
                //主讲人
                hasSpeaker = true;
                isSetCamera(Integer.parseInt(usersBean.getUserId()), Integer.parseInt(usersBean.getVideoStatus()));
//                showSurface(true);
//                showView(Integer.parseInt(usersBean.getUserId()));
            } else {
                hasSpeaker = false;
            }
        }
        if (!hasSpeaker) {
            showSurface(false);
        }
    }

    @Override
    public void onSuccess(int type, Object obj) {
        if (type == Key.ON_LINE_USER) {
            usersBeanList.clear();
            usersBeanList.addAll((List<UsersBean>) obj);
            initSpeaker(usersBeanList);
        } else if (type == Key.USER_STATE) {
            userState = (UsersBean) obj;
            mOnlinePop.onLineUser();
        }
    }

    @Override
    public void onError(int type, int code) {

    }

    @Override
    public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
        try {
            String receiveMsg = new String(lpBuf, "utf-8");
            L.d(TAG, "OnAnyChatTransBuffer:lpBuf=" + receiveMsg + ",dwLen=" + dwLen);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
        try {
            String receiveMsg = new String(lpBuf, "utf-8");
            L.d(TAG, "OnAnyChatSDKFilterData:lpBuf=" + receiveMsg + ",dwLen=" + dwLen);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
//        int i = view.getId();
//        if (i == R.id.meeting_menu) {
//            //菜单键
//            meetingMenuPop.show(rootView);
//        } else if (i == R.id.meeting_user_list) {
//            //打开侧边弹窗
//            if (mOnlinePop != null) {
//                mOnlinePop.show(rootView);
//            }
//        }
    }

    @Override
    public void onClick(int index) {
        meetingMenuPop.dismiss();
        switch (index) {
            case 0:
                //打开侧边弹窗
                if (mOnlinePop != null) {
                    mOnlinePop.show(rootView);
                }
                break;
            case 3:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnAnyChatMicStateChgMessage(int dwUserId, boolean bOpenMic) {

    }

    @Override
    public void OnAnyChatCameraStateChgMessage(final int dwUserId, final int dwState) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSetCamera(dwUserId, dwState);
            }
        }, 300);

    }

    @Override
    public void OnAnyChatChatModeChgMessage(int dwUserId, boolean bPublicChat) {

    }

    @Override
    public void OnAnyChatActiveStateChgMessage(int dwUserId, int dwState) {

    }

    @Override
    public void OnAnyChatP2PConnectStateMessage(int dwUserId, int dwState) {

    }

    @Override
    public void onMenuClick(int type, ImageView imageView) {
        switch (type) {
            case MenuLayout.TYPE_TRANSCRIBE:
                Toast.makeText(this, "当前为直播模式，无法开启或关闭!", Toast.LENGTH_SHORT).show();

                //摄像头的状态，打开，关闭，无摄像头  0-无摄像头，1-有摄像头关闭，2-有摄像头打开
//                video.changeCamera(imageView, userState, new OnVideoStateChangeListener() {
//                    @Override
//                    public void videoStateChange() {
//                        feedbackState(Key.UPDATE_CLIENT_STATUS);
//                    }
//                });
                break;
            case MenuLayout.TYPE_CAMERA:
                Toast.makeText(this, "当前为直播模式，无法开启或关闭!", Toast.LENGTH_SHORT).show();

                //摄像头的前后转换
//                video.switchCamera();
                break;
            case MenuLayout.TYPE_MICROPHONE:
                Toast.makeText(this, "当前为直播模式，无法开启或关闭!", Toast.LENGTH_SHORT).show();

                //本地声音
//                audio.changeLocal(imageView, userState, new OnAudioStateChangeListener() {
//                    @Override
//                    public void audioStateChange() {
//                        feedbackState(Key.UPDATE_CLIENT_STATUS);
//                    }
//                });
                break;
            case MenuLayout.TYPE_SOUND:
                Toast.makeText(this, "当前为直播模式，无法开启或关闭!", Toast.LENGTH_SHORT).show();

                //远程声音
//                audio.changeDistance(imageView);
                break;
            case MenuLayout.TYPE_FUN:
                //菜单键
                meetingMenuPop.show(rootView);
                break;
            case MenuLayout.TYPE_USER:
                //打开侧边弹窗
                mOnlinePop.show(rootView);
                break;
            default:
                break;
        }
    }

    private void isSetCamera(int mUserId, int mState) {
        params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        switch (mState) {
            case 0:
                rootView.setBackgroundColor(0xFF000000);
                mSurfaceView.setVisibility(View.GONE);
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                mImageView.setLayoutParams(params);
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageResource(R.drawable.img_meeting_wait);
                break;
            case 1:
                rootView.setBackgroundColor(0xFF12182d);
                mSurfaceView.setVisibility(View.GONE);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.width = dip2px(this, 40);
                params.height = dip2px(this, 40);
                mImageView.setLayoutParams(params);
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageResource(R.drawable.shut_camera);
                break;
            case 2:
                mSurfaceView.setZOrderMediaOverlay(false);
                showSurface(true);
                showView(mUserId);
                break;
        }
    }

    /**
     * dp转为px
     *
     * @param context  上下文
     * @param dipValue dp值
     * @return
     */
    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
