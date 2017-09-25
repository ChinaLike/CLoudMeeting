package com.tydic.cloudmeeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播视频模式
 */
public class OnLiveActivity extends BaseActivity implements View.OnClickListener,MeetingMenuPop.MenuClickListener{

    private SurfaceView mSurfaceView;

    private ImageView mImageView;

    private List<UsersBean> usersBeanList = new ArrayList<>();

    private ImageView menu;

    private RelativeLayout rootView;

    private MeetingMenuPop meetingMenuPop;

    private OnlinePop mOnlinePop;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        mRetrofitMo.userState(mJsParamsBean.getRoomId(),mJsParamsBean.getFeedId(),this);
        meetingMenuPop = new MeetingMenuPop(this,mJsParamsBean.getMeetingId(),mJsParamsBean.getCreated_by(),Integer.parseInt(mJsParamsBean.getIsBroadcastMode()));
        meetingMenuPop.setMenuClickListener(this);
        mOnlinePop = new OnlinePop(this,mJsParamsBean);
        mOnlinePop.onLineUser();
        initView();
        initSDK();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mImageView = (ImageView) findViewById(R.id.hint_image);
        menu = (ImageView) findViewById(R.id.meeting_menu);
        menu.setOnClickListener(this);
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
            mImageView.setVisibility(View.GONE);
            mSurfaceView.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageResource(R.drawable.img_meeting_wait);
            mSurfaceView.setVisibility(View.GONE);
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
        if (dwUserNum == 1) {
            //只有自己
            showSurface(false);
        }else {
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(),this);
        }
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

    }

    /**
     * 判断是否有主讲人
     */
    private void initSpeaker() {
        boolean hasSpeaker = false;
        for (UsersBean usersBean : usersBeanList) {
            if (usersBean.getIsPrimarySpeaker().equals("1")) {
                //主讲人
                hasSpeaker = true;
                showSurface(true);
                showView(Integer.parseInt(usersBean.getUserId()));
                return;
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
            usersBeanList = (List<UsersBean>) obj;
            initSpeaker();
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
        //菜单键
        meetingMenuPop.showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

    @Override
    public void onClick(int index) {
        meetingMenuPop.dismiss();
        switch (index) {
            case 0:
                //打开侧边弹窗
                if (mOnlinePop != null) {
                    mOnlinePop.onLineUser();
                    mOnlinePop.showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.ALIGN_TOP,
                            RelativePopupWindow.HorizontalPosition.RIGHT, true);
                }
                break;
            case 3:
                finish();
                break;
            default:
                break;
        }
    }
}
