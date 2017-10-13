package com.tydic.cm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tydic.cm.adapter.SurfaceAdapter;
import com.tydic.cm.base.BaseActivity;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.LocalHelper;
import com.tydic.cm.model.inf.OnAudioStateChangeListener;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnLocationListener;
import com.tydic.cm.model.inf.OnVideoStateChangeListener;
import com.tydic.cm.overwrite.LocationPop;
import com.tydic.cm.overwrite.MeetingMenuPop;
import com.tydic.cm.overwrite.OnlinePop;
import com.tydic.cm.util.CollectionsUtil;
import com.tydic.cm.util.L;
import com.tydic.cm.util.T;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通视频模式
 */
public class CommonActivity extends BaseActivity implements View.OnClickListener,
        MeetingMenuPop.MenuClickListener, OnItemClickListener, OnLocationListener, LocalHelper {

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
    private List<UsersBean> surfaceBeanList = new ArrayList<>();

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
        initLocalSurface(selfSurface);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAnyChatInit!=null){
            mAnyChatInit.onRestart();
        }

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
        adapter.setSelfID(selfUserId);
        adapter.setAnychat(anychat);
        adapter.setColumn(showCount);
        adapter.setLocalHelper(this);
        GridLayoutManager manager = new GridLayoutManager(mContext, showCount);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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
            if (receiveMsg.contains(" ")) {
                return;
            } else {
                switch (Integer.parseInt(receiveMsg)) {
                    case Key.CLIENT_DISABLE_MIC:
                        T.showShort("您的麦克风被管理员关闭");
                        audio.setSpeaking(true);
                        microphone.performClick();
                        break;
                    case Key.CLIENT_ENAble_MIC:
                        T.showShort("您的麦克风被管理员打开");
                        audio.setSpeaking(false);
                        microphone.performClick();
                        break;
                    case Key.CLIENT_DISABLE_VIDEO:
                        if (video.isHasCamera()) {
                            //有摄像头
                            T.showShort("您的摄像头被管理员关闭");
                            video.setOpenCamera(true);
                            cameraStatus.performClick();
                        } else {
                            //没有摄像头
                            video.setOpenCamera(false);
                        }
                        break;
                    case Key.CLIENT_ENAble_VIDEO:
                        if (video.isHasCamera()) {
                            //有摄像头
                            T.showShort("您的摄像头被管理员打开");
                            video.setOpenCamera(false);
                            cameraStatus.performClick();
                        } else {
                            //没有摄像头
                            video.setOpenCamera(false);
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
                case Key.CLIENT_NOTICE_PRIMARY_SPEAKER + "":
                    //userId为0表示取消主讲人
                    if ("0".equals(arr[1])) {
                        T.showShort("已取消主讲人");
                        refreshMic(Key.AUDIO_OPEN);//打开语音
                        refreshCamera(Key.VIDEO_OPEN);//打开摄像头
                        mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
                    } else {
                        int speaker = Integer.parseInt(arr[1]);
                        UsersBean bean = new UsersBean();
                        bean.setUserId(speaker + "");
                        bean.setVideoStatus(Key.VIDEO_OPEN);
                        bean.setAudioStatus(Key.AUDIO_OPEN);
                        if (speaker == selfUserId) {
                            T.showShort("你被设置为主讲人了！");
                            refreshMic(Key.AUDIO_OPEN);//打开语音
                            refreshCamera(Key.VIDEO_OPEN);//打开摄像头
                        } else {
                            //主讲人是别人
                            refreshMic(Key.AUDIO_CLOSE);//关闭语音
                            refreshCamera(Key.VIDEO_CLOSE);//关闭摄像头
                            for (UsersBean item : surfaceBeanList) {
                                if (Integer.parseInt(item.getUserId()) == speaker) {
                                    T.showShort(item.getNickName() + "被设置为主讲人了！");
                                    break;
                                }
                            }
                        }
                        surfaceBeanList.clear();
                        surfaceBeanList.add(bean);
                        initAdapter(1);
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
     * 刷新麦克风状态
     */
    private void refreshMic(String mic) {
        userState.setAudioStatus(mic);
        audio.init(microphone, userState);
        //更新状态
        feedbackState(Key.UPDATE_CLIENT_STATUS);
    }

    /**
     * 刷新摄像头状态
     *
     * @param camera
     */
    private void refreshCamera(String camera) {
        userState.setVideoStatus(camera);
        video.init(cameraStatus, userState);
        //更新状态
        feedbackState(Key.UPDATE_CLIENT_STATUS);
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
     *
     * @param dwUserId
     * @param bEnter
     */
    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        L.d(TAG, "OnAnyChatUserAtRoomMessage:dwUserId=" + dwUserId + ",bEnter=" + bEnter);

        if (bEnter) {
            //有人进入房间
            enterRoom(dwUserId);
        } else {
            //有人退出房间
            exitRoom(dwUserId);
        }
        if (mOnlinePop != null && mOnlinePop.isShowing()) {
            mOnlinePop.onLineUser();
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
        for (UsersBean bean : surfaceBeanList) {
            if (Integer.parseInt(bean.getUserId()) == dwUserId) {
                isAdd = true;
                break;
            }
        }
        /**
         * 添加新用户，如果进入房间的人数超过设定最大人数时，不再添加
         */
        if (!isAdd && surfaceBeanList.size() <= MAX_VIDEO_SHOW_NUMBER) {
            for (int i = 0; i < surfaceBeanList.size(); i++) {
                UsersBean bean = surfaceBeanList.get(i);
                if (bean.getUserId().equals("0")) {
                    bean.setUserId(dwUserId + "");
                    audio.refreshDistanceAudio(bean);
                    bean.setVideoStatus(Key.VIDEO_OPEN);
                    bean.setIsPrimarySpeaker(Key.NO_SPEAKER);
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    /**
     * 有人退出房间
     *
     * @param dwUserId
     * @return
     */
    private void exitRoom(int dwUserId) {
        if (surfaceBeanList.size() == 1) {
            //主讲人退出
          //  T.showShort("主讲人已经退出，正在获取房间其他人员信息！");
            refreshCamera(Key.VIDEO_OPEN);//回复本地摄像头状态
            refreshMic(Key.AUDIO_OPEN);//回复本地语音状态
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        } else {
            for (UsersBean bean : surfaceBeanList) {
                if (Integer.parseInt(bean.getUserId()) == dwUserId) {
                    bean.setUserId("0");
                    bean.setIsPrimarySpeaker(Key.NO_SPEAKER);
                    int index = surfaceBeanList.indexOf(bean);
                    adapter.notifyItemChanged(index);
                    break;
                }
            }
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
                video.init(cameraStatus, userState);
                audio.init(microphone, userState);
                //更新状态
                feedbackState(Key.UPDATE_CLIENT_STATUS);
                break;
            case Key.ON_LINE_USER:
                //获取在线人员列表
                L.d(TAG, "在线人员列表：" + obj.toString());
                surfaceBeanList.clear();
                UsersBean speaker = mUserMo.getSpeaker((List<UsersBean>) obj);
                if (speaker != null) {
                    //已有主讲人
                    audio.refreshDistanceAudio(speaker);
                    surfaceBeanList.add(speaker);
                    if (Integer.parseInt(speaker.getUserId()) != selfUserId) {
                        refreshMic(Key.AUDIO_CLOSE);//关闭语音
                        refreshCamera(Key.VIDEO_CLOSE);//关闭摄像头
                    } else {
                        refreshMic(Key.AUDIO_OPEN);//打开语音
                        refreshCamera(Key.VIDEO_OPEN);//打开摄像头
                    }
                } else {
                    //当前实际进入房间数量
                    int currSize = ((List<UsersBean>) obj).size();
                    for (int i = 0; i < MAX_VIDEO_SHOW_NUMBER; i++) {
                        if (i < currSize) {
                            UsersBean usersBean = ((List<UsersBean>) obj).get(i);
                            if (Integer.parseInt(usersBean.getUserId()) == selfUserId) {
                                //如果是自己，使用本地状态
                                usersBean.setAudioStatus(userState.getAudioStatus());
                                usersBean.setVideoStatus(userState.getVideoStatus());
                                surfaceBeanList.add(usersBean);
                            } else {
                                //远程
                                audio.refreshDistanceAudio(usersBean);
                                surfaceBeanList.add(usersBean);
                            }
                        } else {
                            //不足的地方补空位
                            surfaceBeanList.add(emptyBean());
                        }
                    }
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
        if (size > 1 && size <= 4) {
            showNum = 2;
        } else if (size > 4 && size <= 6) {
            showNum = 3;
        } else if (size > 6) {
            showNum = 4;
        } else {
            showNum = 1;
        }
        initAdapter(showNum);
    }

    @Override
    public void onError(int type, int code) {

    }

    /**
     * 获取本地数据
     *
     * @return
     */
    private UsersBean getLocalBean() {
        for (UsersBean item : surfaceBeanList) {
            if (Integer.parseInt(item.getUserId()) == selfUserId) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.meeting_transcribe) {
            //摄像头的状态，打开，关闭，无摄像头  0-无摄像头，1-有摄像头关闭，2-有摄像头打开
            video.changeCamera(cameraStatus, userState, new OnVideoStateChangeListener() {
                @Override
                public void videoStateChange() {
                    feedbackState(Key.UPDATE_CLIENT_STATUS);
                    if (getLocalBean() != null) {
                        getLocalBean().setVideoStatus(userState.getVideoStatus());
                        getLocalBean().setAudioStatus(userState.getAudioStatus());
                        adapter.notifyItemChanged(surfaceBeanList.indexOf(getLocalBean()));
                    }
                }
            });
        } else if (id == R.id.meeting_camera) {
            //摄像头的前后转换
            video.switchCamera();
        } else if (id == R.id.meeting_microphone) {
            //本地声音
            audio.changeLocal(microphone, userState, new OnAudioStateChangeListener() {
                @Override
                public void audioStateChange() {
                    feedbackState(Key.UPDATE_CLIENT_STATUS);
                }
            });
        } else if (id == R.id.meeting_sound) {
            //远程声音
            audio.changeDistance(sound);
        }
        if (id == R.id.meeting_menu) {
            //菜单键
            meetingMenuPop.show(rootView);
        }
    }

    @Override
    public void onClick(int index) {
        meetingMenuPop.dismiss();
        switch (index) {
            case 0:
                //打开侧边弹窗
                mOnlinePop.show(rootView);
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
        int oldPos = -1;
        for (UsersBean item : surfaceBeanList) {
            if (bean.getUserId().equals(item.getUserId())) {
                oldPos = surfaceBeanList.indexOf(item);
            }
        }
        mLocationPop.setOldPos(oldPos);
        mLocationPop.setBean(bean);
        mLocationPop.show(rootView);
    }

    /**
     * 位置切换回调
     *
     * @param oldPos
     * @param newPos
     */
    @Override
    public void loacation(int oldPos, int newPos, UsersBean bean) {
        if (oldPos == newPos) {
            //处理位置没有变化
            T.showShort("当前位置没有变化哦！");
            return;
        }
        if (oldPos == -1) {
            //表示在显示之外的用户切换到界面
            int newId = Integer.parseInt(surfaceBeanList.get(newPos).getUserId());
            surfaceBeanList.remove(newPos);
            surfaceBeanList.add(newPos, bean);
            if (newId != selfUserId) {
                adapter.notifyItemChanged(newPos);
            } else {
                initAdapter(showNum);
            }
        } else {
            //交换位置
            int newId = Integer.parseInt(surfaceBeanList.get(newPos).getUserId());
            int oldId = Integer.parseInt(surfaceBeanList.get(oldPos).getUserId());
            CollectionsUtil.swap3(surfaceBeanList, oldPos, newPos);
            //在此处先设置声音(必须)
            if (newId != selfUserId) {
                audio.refreshDistanceAudio(surfaceBeanList.get(oldPos));
            }
            if (oldId != selfUserId) {
                audio.refreshDistanceAudio(surfaceBeanList.get(newPos));
            }
            if (newId != selfUserId && oldId != selfUserId) {
                adapter.notifyItemChanged(newPos);
                adapter.notifyItemChanged(oldPos);
            } else {
                initAdapter(showNum);
            }
        }
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
    public void local(int position, UsersBean bean, int width, int height, int x, int y) {
        localParent.removeView(selfSurface);
    }
}