package com.tydic.cm;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cm.adapter.SurfaceAdapter;
import com.tydic.cm.base.BaseActivity;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.helper.BannerHelper;
import com.tydic.cm.helper.LocalViewHelper;
import com.tydic.cm.helper.TimerHandler;
import com.tydic.cm.model.inf.OnAudioStateChangeListener;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnLocationListener;
import com.tydic.cm.model.inf.OnRequestListener;
import com.tydic.cm.model.inf.OnVideoStateChangeListener;
import com.tydic.cm.overwrite.CarouselPop;
import com.tydic.cm.overwrite.CustomGridManner;
import com.tydic.cm.overwrite.LocationPop;
import com.tydic.cm.overwrite.MeetingMenuPop;
import com.tydic.cm.overwrite.MenuLayout;
import com.tydic.cm.overwrite.OnlinePop;
import com.tydic.cm.overwrite.SimpleDividerItemDecoration;
import com.tydic.cm.util.CollectionsUtil;
import com.tydic.cm.util.L;
import com.tydic.cm.util.T;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通视频模式
 */
public class CommonActivity extends BaseActivity implements MenuLayout.MenuClickListener, LocalViewHelper,
        MeetingMenuPop.MenuClickListener, OnItemClickListener, OnLocationListener, BannerHelper {

    private RelativeLayout rootView;
    /**
     * 远程视频显示
     */
    private RecyclerView recyclerView;

    private MenuLayout menuLayout;//功能菜单

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
    /**
     * 保存所有人员
     */
    private List<UsersBean> allUserList = new ArrayList<>();
    /**
     * 轮播
     */
    private CarouselPop carouselPop;
    /**
     * 列表管理器
     */
    private CustomGridManner manager;
    /**
     * 定时器
     */
    private TimerHandler mTimerHandler;

    private RelativeLayout localParent;

    private SurfaceView localChild;
    /**
     * 是否添加本地轮播视频
     */
    private boolean isAddLocalSurface = true;


    @Override
    protected void init(@Nullable Bundle savedInstanceState) {

        meetingMenuPop.setMenuClickListener(this);
        mOnlinePop = new OnlinePop(this, mJsParamsBean);
        mOnlinePop.onLineUser();
        mOnlinePop.setOnItemClickListener(this);
        //视频轮播init
        carouselPop = new CarouselPop(mContext);
        carouselPop.setBannerHelper(this);
        //初始化控件
        initView();
        //通用配置
        initSurfaceSDK();
        //初始化适配器
        initAdapter(showNum, surfaceBeanList);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mRetrofitMo != null) {
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(1, 0xFFFFFFFF));
        rootView = (RelativeLayout) findViewById(R.id.root);
        menuLayout = (MenuLayout) findViewById(R.id.menu_layout);
        menuLayout.setMenuClickListener(this);

        localParent = (RelativeLayout) findViewById(R.id.local_parent);
        localChild = (SurfaceView) findViewById(R.id.local_child);
    }

    /**
     * 初始化适配器
     *
     * @param showCount
     */
    private void initAdapter(int showCount, List<UsersBean> list) {
        adapter = new SurfaceAdapter(mContext, list);
        adapter.setLocalViewHelper(this);
        adapter.setSelfID(selfUserId);
        adapter.setAnychat(anychat);
        adapter.setColumn(showCount);
        manager = new CustomGridManner(mContext, showCount);
        manager.setScrollEnabled(false);
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
                        menuLayout.performClick(MenuLayout.TYPE_MICROPHONE);
                        break;
                    case Key.CLIENT_ENAble_MIC:
                        T.showShort("您的麦克风被管理员打开");
                        audio.setSpeaking(false);
                        menuLayout.performClick(MenuLayout.TYPE_MICROPHONE);
                        break;
                    case Key.CLIENT_DISABLE_VIDEO:
                        if (video.isHasCamera()) {
                            //有摄像头
                            T.showShort("您的摄像头被管理员关闭");
                            video.setOpenCamera(true);
                            menuLayout.performClick(MenuLayout.TYPE_TRANSCRIBE);
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
                            menuLayout.performClick(MenuLayout.TYPE_TRANSCRIBE);
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
                        bean.setNickName(mUserMo.getSpeakerNickName(allUserList, speaker));
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
                        initAdapter(1, surfaceBeanList);
                        mOnlinePop.onLineUser();
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
        audio.init(menuLayout.getMicrophone(), userState);
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
        video.init(menuLayout.getTranscribe(), userState);
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
//            if (userState.getVideoStatus().equals("1")) {
//                anychat.UserCameraControl(-1, 0);
//            } else if (userState.getVideoStatus().equals("2")) {
//                anychat.UserCameraControl(-1, 1);
//            }
//            if (userState.getAudioStatus().equals("1")) {
//                anychat.UserSpeakControl(-1, 0);
//            } else if (userState.getAudioStatus().equals("2")) {
//                anychat.UserSpeakControl(-1, 1);
//            }
            refreshCamera(userState.getVideoStatus());
            refreshMic(userState.getAudioStatus());
            //更新一次本地状态
            feedbackState(Key.UPDATE_CLIENT_STATUS);
            //获取用户状态,更新本地按钮状态
//            mRetrofitMo.userState(mJsParamsBean.getRoomId(), mJsParamsBean.getFeedId(), this);
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
     * 当有人进入时获取用户昵称
     *
     * @param index
     * @param bean
     */
    private void compareData(final int index, final UsersBean bean) {
        //此处获取用户信息，当用户信息获取完毕后刷新对应数据
        try {
            Thread.sleep(300);
            final String userId = bean.getUserId();
            mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), new OnRequestListener() {
                @Override
                public void onSuccess(int type, Object obj) {
                    List<UsersBean> list = (List<UsersBean>) obj;
                    allUserList.clear();
                    allUserList.addAll(list);
                    //视频轮播init
                    carouselPop.setUserBean(list);
                    for (UsersBean item : list) {
                        if (userId.equals(item.getUserId())) {
                            bean.setNickName(item.getNickName());
                            bean.setMeetingId(item.getMeetingId());
                            bean.setYhyUserId(item.getYhyUserId());
                            adapter.notifyItemChanged(index);
                            break;
                        }
                    }
                }

                @Override
                public void onError(int type, int code) {
                    adapter.notifyItemChanged(index);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                    compareData(i, bean);
                    // adapter.notifyItemChanged(i);
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
            int userId = Integer.parseInt(surfaceBeanList.get(0).getUserId());
            if (userId == dwUserId) {
                //主讲人退出
                refreshCamera(Key.VIDEO_OPEN);//回复本地摄像头状态
                refreshMic(Key.AUDIO_OPEN);//回复本地语音状态
                mRetrofitMo.onLineUsers(mJsParamsBean.getRoomId(), this);
            } else {
                //非主讲人退出

            }

        } else {
            for (UsersBean bean : surfaceBeanList) {
                if (Integer.parseInt(bean.getUserId()) == dwUserId) {
                    bean.setUserId("0");
                    bean.setVideoStatus(Key.VIDEO_OTHER);
                    bean.setIsPrimarySpeaker(Key.NO_SPEAKER);
                    bean.setNickName("");
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
        if (carouselPop != null && carouselPop.isShowing()) {
            carouselPop.dismiss();
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
                video.init(menuLayout.getTranscribe(), userState);
                audio.init(menuLayout.getMicrophone(), userState);
                //更新状态
                feedbackState(Key.UPDATE_CLIENT_STATUS);
                break;
            case Key.ON_LINE_USER:
                //获取在线人员列表
                L.d(TAG, "在线人员列表：" + obj.toString());
                surfaceBeanList.clear();
                allUserList.clear();
                allUserList.addAll((List<UsersBean>) obj);
                carouselPop.setUserBean(allUserList);
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
                resetAdapter(surfaceBeanList.size(), surfaceBeanList);
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
    private void resetAdapter(int size, List<UsersBean> list) {
        if (size > 1 && size <= 4) {
            showNum = 2;
        } else if (size > 4 && size <= 6) {
            showNum = 3;
        } else if (size > 6) {
            showNum = 4;
        } else {
            showNum = 1;
        }
        initAdapter(showNum, list);
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
    public void onMenuClick(int type, ImageView imageView) {
        switch (type) {
            case MenuLayout.TYPE_TRANSCRIBE:
                //摄像头的状态，打开，关闭，无摄像头  0-无摄像头，1-有摄像头关闭，2-有摄像头打开
                video.changeCamera(imageView, userState, new OnVideoStateChangeListener() {
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
                //处理轮播
                if (carouselPop.isCarousel()) {
                    showLocal(userState.getVideoStatus() == Key.VIDEO_OPEN ? true : false);
                }
                break;
            case MenuLayout.TYPE_CAMERA:
                //摄像头的前后转换
                video.switchCamera();
                break;
            case MenuLayout.TYPE_MICROPHONE:
                //本地声音
                audio.changeLocal(imageView, userState, new OnAudioStateChangeListener() {
                    @Override
                    public void audioStateChange() {
                        feedbackState(Key.UPDATE_CLIENT_STATUS);
                    }
                });
                break;
            case MenuLayout.TYPE_SOUND:
                //远程声音
                audio.changeDistance(imageView);
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
            case 5:
                if (!mUserMo.isHasSpeaker(allUserList)) {
                    //mOnlinePop.onLineUser();
                    carouselPop.show(rootView);
                } else {
                    T.showShort("当前为主讲人模式,不可设置视频轮播!");
                }
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
                initAdapter(showNum, surfaceBeanList);
            }
        } else {
            //交换位置
            if ((oldPos == 0 && newPos == 1) || (oldPos == 1 && newPos == 0)) {
                //处理1位置和2位置交换
                CollectionsUtil.swap3(surfaceBeanList, oldPos, newPos);
            } else {
                //处理非1位置切换到其他位置
                UsersBean oldBean = surfaceBeanList.get(oldPos);
                surfaceBeanList.remove(oldPos);
                surfaceBeanList.add(oldPos, emptyBean());
                surfaceBeanList.remove(newPos);
                surfaceBeanList.add(newPos, oldBean);
            }
            int newId = Integer.parseInt(surfaceBeanList.get(newPos).getUserId());
            int oldId = Integer.parseInt(surfaceBeanList.get(oldPos).getUserId());
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
                initAdapter(showNum, surfaceBeanList);
            }
        }
    }

    @Override
    public void OnAnyChatMicStateChgMessage(int dwUserId, boolean bOpenMic) {

    }

    @Override
    public void OnAnyChatCameraStateChgMessage(int dwUserId, int dwState) {
        Log.e("OnAnyChatCameraStateChg", dwUserId + " " + dwState);
        feedbackState(Key.UPDATE_CLIENT_STATUS);
        if (dwUserId != selfUserId) {
            for (UsersBean bean : surfaceBeanList) {
                int userId = Integer.parseInt(bean.getUserId());
                if (userId == dwUserId) {
                    bean.setVideoStatus(dwState + "");
                    adapter.notifyItemChanged(surfaceBeanList.indexOf(bean));
                    return;
                }
            }
        }
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

    /**
     * 视频轮播回调
     *
     * @param mode 轮播模式， 1-表示全屏轮播  2-表示非全屏且2为一组轮播  依次类推
     * @param list 轮播参数，轮播时以此集合中参数进行轮播
     */
    @Override
    public void banner(final int mode, final List<UsersBean> list) {
        if (mode == -1) {
            cancelBanner();
            showLocal(false);
        } else {

            final int size = list == null ? 0 : list.size();
            handleBannerData(list, mode);
            //重置界面
            resetAdapter(mode, list);
            //关闭动画效果
            closeDefaultAnimator();
            showLocal(true);
            mTimerHandler = new TimerHandler(mode, list) {
                @Override
                public void handleBanner(int page) {
                    //判断集合中还存在人数比轮播人数少的时候，关闭轮播
                    if (size <= mode) {
                        cancelBanner();
                        return;
                    }
                    //滚动到指定页面
                    manager.scrollToPosition(mTimerHandler.getIndex(page));

                }
            };
            mTimerHandler.start();
        }
    }

    /**
     * 轮播时显示本地视频
     *
     * @param show
     */
    private void showLocal(boolean show) {
        if (show) {
            if (!isAddLocalSurface) {
                isAddLocalSurface = true;
                localParent.addView(localChild);
            }
            // 视频如果是采用java采集
            SurfaceHolder surfaceHolder = localChild.getHolder();
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                surfaceHolder.addCallback(AnyChatCoreSDK.mCameraHelper);
            }
            //处理本地视频
            if (userState.getVideoStatus().equals(Key.VIDEO_OPEN)) {
                anychat.UserCameraControl(-1, 1);
            } else {
                anychat.UserCameraControl(-1, 0);
            }
        } else {
            isAddLocalSurface = false;
            localParent.removeView(localChild);
        }
    }

    /**
     * 处理轮播数据
     *
     * @param list
     */
    private void handleBannerData(List<UsersBean> list, int mode) {
        int size = list.size();
        for (int i = 0; i < mode - 1; i++) {
            for (int j = 0; j < size; j++) {
                list.add(list.get(j));
            }
        }
    }

    /**
     * 取消轮播
     */
    private void cancelBanner() {
        //停止计时器
        if (mTimerHandler != null) {
            mTimerHandler.stop();
        }
        //取消轮播
        if (surfaceBeanList.size() > 1) {
            resetAdapter(4, surfaceBeanList);
        } else {
            resetAdapter(1, surfaceBeanList);
        }
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void removeView() {
        // showLocal(false);
    }
}