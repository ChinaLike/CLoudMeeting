package com.tydic.cm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cm.adapter.SurfaceAdapter;
import com.tydic.cm.base.BaseActivity;
import com.tydic.cm.bean.SurfaceBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.LocalHelper;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnLocationListener;
import com.tydic.cm.overwrite.LocationPop;
import com.tydic.cm.overwrite.MeetingMenuPop;
import com.tydic.cm.overwrite.OnlinePop;
import com.tydic.cm.util.L;
import com.tydic.cm.util.T;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.tydic.cm.constant.Key.CLIENT_NOTICE_PRIMARY_SPEAKER;

/**
 * 普通视频模式
 */
public class CommonActivity extends BaseActivity implements View.OnClickListener,
        MeetingMenuPop.MenuClickListener, OnItemClickListener, OnLocationListener,
        LocalHelper {

    private RelativeLayout rootView;
    /**
     * 远程视频显示
     */
    private RecyclerView recyclerView;
    /**
     * 本地视频显示
     */
    private SurfaceView selfSurface;

    private FrameLayout localParent;

    private ImageView cameraStatus, camera, microphone, sound, menu;//摄像头的状态，摄像头切换，本地麦克风，远程麦克风，菜单

    boolean isSpeaking = true;//是否可以说话

    boolean isSound = true;//是否有声音

    boolean isOpenCamera = true;//是否打开摄像头

    boolean isHasCamera = true; //是否有摄像头，针对个别手机

    private MeetingMenuPop meetingMenuPop;

    private OnlinePop mOnlinePop;

    private LocationPop mLocationPop;
    /**
     * 一排显示的个数
     */
    private int showNum = 1;

    /**
     * 远程视频适配器
     */
    private SurfaceAdapter adapter;
    /**
     * 适配数据
     */
    private List<SurfaceBean> surfaceBeanList = new ArrayList<>();


    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        meetingMenuPop = new MeetingMenuPop(mContext, mJsParamsBean.getMeetingId(), mJsParamsBean.getCreated_by(), Integer.parseInt(mJsParamsBean.getIsBroadcastMode()));
        meetingMenuPop.setMenuClickListener(this);
        mOnlinePop = new OnlinePop(this, mJsParamsBean);
        mOnlinePop.onLineUser();
        mOnlinePop.setOnItemClickListener(this);
        //初始化控件
        initView();
        //通用配置
        initSurfaceSDK();
        //初始化适配器
        initAdapter(showNum);
        //初始化本地视频
        initLocalSurface();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selfSurface = (SurfaceView) findViewById(R.id.self_surface);
        localParent = (FrameLayout) findViewById(R.id.local_parent);
        rootView = (RelativeLayout) findViewById(R.id.root);
        cameraStatus = (ImageView) findViewById(R.id.meeting_transcribe);
        cameraStatus.setOnClickListener(this);
        camera = (ImageView) findViewById(R.id.meeting_camera);
        camera.setOnClickListener(this);
        microphone = (ImageView) findViewById(R.id.meeting_microphone);
        microphone.setOnClickListener(this);
        sound = (ImageView) findViewById(R.id.meeting_sound);
        sound.setOnClickListener(this);
        menu = (ImageView) findViewById(R.id.meeting_menu);
        menu.setOnClickListener(this);
    }

    /**
     * 初始化适配器
     *
     * @param showCount
     */
    private void initAdapter(int showCount) {
        adapter = new SurfaceAdapter(mContext, surfaceBeanList);
        adapter.setAnychat(anychat);
        adapter.setColumn(showCount);
        adapter.setLocalHelper(this);
        GridLayoutManager manager = new GridLayoutManager(mContext, showCount);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 初始化通用配置
     */
    private void initSurfaceSDK() {
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

    private void initLocalSurface() {
        // 视频如果是采用java采集
        selfSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            selfSurface.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
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
                        if (isHasCamera) {
                            //有摄像头
                            T.showShort("您的摄像头被管理员关闭");
                            isOpenCamera = true;
                            cameraStatus.performClick();
                        } else {
                            //没有摄像头
                        }
                        break;
                    case Key.CLIENT_ENAble_VIDEO:
                        if (isHasCamera) {
                            //有摄像头
                            T.showShort("您的摄像头被管理员打开");
                            isOpenCamera = false;
                            cameraStatus.performClick();
                        } else {
                            //没有摄像头
                        }
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
                        mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
                    } else {
                        int speaker = Integer.parseInt(arr[1]);
                        surfaceBeanList.clear();
                        SurfaceBean bean = new SurfaceBean();
                        bean.setUserId(speaker);
                        bean.setOpenCamera(true);
                        bean.setSound(true);
                        bean.setSpeaker(true);
                        if (speaker == selfUserId) {
                            T.showShort("你被设置为主讲人了！");
                            //主讲人时自己
                            bean.setLocal(true);
                        } else {
                            //主讲人是别人
                            bean.setLocal(false);
                        }
                        surfaceBeanList.add(bean);
                        adapter.notifyDataSetChanged();
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

    /**
     * 进入房间成功
     *
     * @param dwRoomId
     * @param dwErrorCode
     */
    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        L.d(TAG, "OnAnyChatEnterRoomMessage:dwRoomId=" + dwRoomId + ",dwErrorCode=" + dwErrorCode);
        //进入房间成功
        if (dwErrorCode == 0) {
            //打开本地音视频，初次进入先打开本地视频
            anychat.UserCameraControl(-1, 1);
            anychat.UserSpeakControl(-1, 1);
            //更新一次本地状态
            feedbackState(Key.UPDATE_CLIENT_STATUS);
            //获取用户状态,更新本地按钮状态
            mRetrofitMo.userState(mJsParamsBean.getRoomId(), mJsParamsBean.getFeedId(), this);
            //获取在线人员，适配界面数据
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        } else {
            //进入房间失败
            T.showShort("进入会议房间失败，即将退出！");
        }
    }


    /**
     * 当前房间用户离开或者进入房间触发这个回调，dwUserId用户  id," bEnter==true"表示进入房间,反之表示离开房间
     * -
     *
     * @param dwUserId
     * @param bEnter
     */
    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        L.d(TAG, "OnAnyChatUserAtRoomMessage:dwUserId=" + dwUserId + ",bEnter=" + bEnter);
        //获取在线人员,每当有人进入或退出时获取一次
        // mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        if (bEnter) {
            //有人进入房间
            enterRoom(dwUserId);
        } else {
            //有人退出房间
            exitRoom(dwUserId);
        }
    }

    /**
     * 有人进入房间
     *
     * @param dwUserId
     * @return
     */
    private void enterRoom(int dwUserId) {
        boolean isAdd = false;
        for (SurfaceBean bean : surfaceBeanList) {
            if (bean.getUserId() == dwUserId) {
                isAdd = true;
                break;
            }
        }
        if (!isAdd) {
            SurfaceBean bean = new SurfaceBean();
            bean.setUserId(dwUserId);
            bean.setLocal(false);
            bean.setSpeaker(true);
            bean.setSound(true);
            bean.setOpenCamera(true);
            surfaceBeanList.add(bean);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 有人退出房间
     *
     * @param dwUserId
     * @return
     */
    private void exitRoom(int dwUserId) {
        SurfaceBean removeBean = null;
        for (SurfaceBean bean : surfaceBeanList) {
            if (bean.getUserId() == dwUserId) {
                removeBean = bean;
                break;
            }
        }
        if (removeBean != null) {
            surfaceBeanList.remove(removeBean);
            adapter.notifyDataSetChanged();
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

    /**
     * 初始化用户状态
     */
    private void initUserState() {
        if (videoState.equals(Key.VIDEO_OPEN)) {
            cameraStatus.setImageResource(R.drawable.img_meeting_camera_open);
            anychat.UserCameraControl(-1,1);
        } else {
            cameraStatus.setImageResource(R.drawable.img_meeting_camera_close);
            anychat.UserCameraControl(-1,0);
        }
        if (micState.equals(Key.AUDIO_OPEN)) {
            microphone.setImageResource(R.drawable.img_meeting_microphone);
            anychat.UserSpeakControl(-1,1);
        } else {
            microphone.setImageResource(R.drawable.meeting_microphone_disable);
            anychat.UserSpeakControl(-1,0);
        }
        //更新状态
        feedbackState(Key.UPDATE_CLIENT_STATUS);
    }

    @Override
    public void onSuccess(int type, Object obj) {
        switch (type) {
            case Key.USER_STATE:
                //获取用户状态
                userState = (UsersBean) obj;
                L.d(TAG, "用户状态：" + userState.toString());
                videoState = userState.getVideoStatus();
                micState = userState.getAudioStatus();
                initUserState();
                break;
            case Key.ON_LINE_USER:
                //获取在线人员列表
                L.d(TAG, "在线人员列表：" + obj.toString());
                surfaceBeanList.clear();
                for (UsersBean item : (List<UsersBean>) obj) {
                    SurfaceBean bean = new SurfaceBean();
                    bean.setUserId(Integer.parseInt(item.getUserId()));
                    if (Integer.parseInt(item.getUserId()) == (selfUserId)) {
                        bean.setLocal(true);
                    } else {
                        bean.setLocal(false);
                    }
                    //设置语音状态
                    if (item.getAudioStatus().equals(Key.AUDIO_OPEN)) {
                        bean.setSound(true);
                    } else {
                        bean.setSound(false);
                    }
                    //设置摄像头状态
                    if (item.getVideoStatus().equals(Key.VIDEO_OPEN)) {
                        bean.setOpenCamera(true);
                    } else {
                        bean.setOpenCamera(false);
                    }
                    surfaceBeanList.add(bean);
                }
                resetAdapter(surfaceBeanList.size());
            case Key.SEND_MESSAGE:
                //发送消息
                L.d(TAG, "发送信息：" + obj.toString());
                break;
            default:
                break;
        }
    }

    /**
     * 重置适配器
     *
     * @param size
     */
    private void resetAdapter(int size) {
        GridLayoutManager manager = null;
        if (size > 1 && size <= 4) {
            showNum = 2;
        } else if (size > 4 && size <= 6) {
            showNum = 3;
        } else if (size > 6) {
            showNum = 4;
        } else {
            showNum = 1;
        }
        manager = new GridLayoutManager(mContext, showNum);
        adapter.setColumn(showNum);
        recyclerView.setLayoutManager(manager);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(int type, int code) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.meeting_transcribe) {
            //摄像头的状态，打开，关闭，无摄像头  0-无摄像头，1-有摄像头关闭，2-有摄像头打开
            if (isHasCamera) {
                if (isOpenCamera) {
                    //执行关闭摄像头
                    userState.setVideoStatus("1");
                    videoState = "1";
                    feedbackState(Key.UPDATE_CLIENT_STATUS);
                    cameraStatus.setImageResource(R.drawable.img_meeting_camera_close);
                    anychat.UserCameraControl(-1, 0);
                } else {
                    //执行打开摄像头
                    userState.setVideoStatus("2");
                    videoState = "2";
                    feedbackState(Key.UPDATE_CLIENT_STATUS);
                    cameraStatus.setImageResource(R.drawable.img_meeting_camera_open);
                    anychat.UserCameraControl(-1, 1);
                }
                mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
                isOpenCamera = !isOpenCamera;
            } else {
                //没有摄像头
                T.showShort("未检测到有摄像头可使用，请在设置中打开摄像头权限！");
            }
        } else if (id == R.id.meeting_camera) {
            //摄像头的前后转换
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
            } else {
                microphone.setImageResource(R.drawable.img_meeting_microphone);
                userState.setAudioStatus("1");
                micState = "1";
                feedbackState(Key.UPDATE_CLIENT_STATUS);
                AnyChatCoreSDK.getInstance(this).UserSpeakControl(-1, 1);
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

    /**
     * 在线人员Item位置切换点击
     *
     * @param position
     * @param bean
     */
    @Override
    public void onItemClick(int position, UsersBean bean) {
        mOnlinePop.dismiss();
        mLocationPop = new LocationPop(this, surfaceBeanList.size());
        mLocationPop.setOnLocationListener(this);
        mLocationPop.setOldPos(position);
        mLocationPop.showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

    /**
     * 位置切换回调
     *
     * @param oldPos
     * @param newPos
     */
    @Override
    public void loacation(int oldPos, int newPos) {
        T.showShort("从" + oldPos + "位置切换到" + newPos + "位置");

    }

    /**
     * 设置本地视频显示的位置
     *
     * @param position 第几个元素
     * @param bean     参数
     * @param width    视频的宽度
     * @param height   视频的高度
     * @param x        X轴移动的位置
     * @param y        Y轴移动的位置
     */
    @Override
    public void local(int position, SurfaceBean bean, int width, int height, int x, int y) {
        L.d(TAG,"width="+width+",,,height="+height);
        localParent.removeView(selfSurface);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) localParent.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        params.width = width;
        params.height = height;
        localParent.setLayoutParams(params);
        localParent.invalidate();
        //  selfSurface.setZOrderOnTop(true);
        L.d(TAG,"width="+localParent.getWidth()+",height="+localParent.getHeight());
    }
}
