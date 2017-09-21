package com.tydic.cloudmeeting.adapter;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cloudmeeting.R;
import com.tydic.cloudmeeting.bean.SurfaceBean;
import com.tydic.cloudmeeting.bean.UsersBean;
import com.tydic.cloudmeeting.config.SurfaceConfig;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音视频列表适配器
 * Created by like on 2017-09-18
 */

public class SurfaceViewAdapter extends RecyclerView.Adapter<SurfaceViewAdapter.SurfaceViewHolder> {


    private List<SurfaceBean> mList;

    private Context mContext;

    private SurfaceConfig config;

    private AnyChatCoreSDK anychat;

    private Handler mHandler;

    public static final int MSG_CHECKAV = 1;

    public static final int MSG_TIMEUPDATE = 2;

    private Timer mTimerCheckAv;

    private TimerTask mTimerTask;

    private Timer mTimerShowVideoTime;

    public SurfaceViewAdapter(List<SurfaceBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        config = new SurfaceConfig(mContext);
        anychat = AnyChatCoreSDK.getInstance(mContext);

    }

    public void setAnyChat(AnyChatCoreSDK anyChat) {
        // this.anychat = anyChat;
    }

    @Override
    public SurfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SurfaceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_surface, parent, false));
    }

    @Override
    public void onBindViewHolder(SurfaceViewHolder holder, int position) {
        SurfaceBean bean = mList.get(position);
        //处理Item的大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(config.itemWidth(), config.itemHeight());
        holder.parent.setLayoutParams(params);
        if (bean.isLocal()) {
            //本地视频
            // 视频如果是采用java采集
            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                holder.mSurfaceView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
            }
            initVideo();
        } else {
            //非本地视频，即显示远程视频
            initVideo();
            initNetVideo(holder.mSurfaceView,bean);
        }
        updataAv(bean,holder.mSurfaceView);
    }

    /**
     * 实时更新视频
     */
    private void updataAv(final SurfaceBean bean, final SurfaceView surfaceView){
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CHECKAV:
                        //实时视频刷新
                        checkVideoStatus(bean,surfaceView);
                        break;
                    case MSG_TIMEUPDATE:
                        break;
                }

            }
        };
        initTimerCheckAv();
        initTimerShowTime();
    }

    /**
     * 切换视频状态
     */
    private void checkVideoStatus(SurfaceBean bean , SurfaceView surfaceView) {
        if (bean.isLocal()){
            //本地
            if (anychat.GetCameraState(-1) == 2 && anychat.GetUserVideoWidth(-1) != 0) {
                SurfaceHolder holder = surfaceView.getHolder();

                if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                    holder.setFormat(PixelFormat.RGB_565);
                    holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
                }

                Surface s = holder.getSurface();
                anychat.SetVideoPos(-1, s, 0, 0, 0, 0);

            }
        }else {
            //远程
            SurfaceHolder holder = surfaceView.getHolder();
            int userId = bean.getUserId();
            int index = anychat.mVideoHelper.bindVideo(holder);

            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                holder.setFormat(PixelFormat.RGB_565);
                holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
            }
            Surface s = holder.getSurface();
            if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                anychat.mVideoHelper.SetVideoUser(index, userId);
            } else
                anychat.SetVideoPos(userId, s, 0, 0, 0, 0);
        }



//        final double v = anychat.QueryUserStateInt(-1, AnyChatDefine.BRAC_USERSTATE_VIDEOBITRATE) / 1000.0;
    }

    private void initTimerCheckAv() {
        if (mTimerCheckAv == null) {
            mTimerCheckAv = new Timer();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_CHECKAV);
            }
        };
        mTimerCheckAv.schedule(mTimerTask, 1000, 100);
    }

    private void initTimerShowTime() {
        if (mTimerShowVideoTime == null) {
            mTimerShowVideoTime = new Timer();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_TIMEUPDATE);
            }
        };
        mTimerShowVideoTime.schedule(mTimerTask, 100, 1000);
    }

    /**
     * 初始化远程视频
     * @param surfaceView
     */
    private void initNetVideo(SurfaceView surfaceView , SurfaceBean bean){
        int userId = bean.getUserId();
        int index = anychat.mVideoHelper.bindVideo(surfaceView.getHolder());
        anychat.mVideoHelper.SetVideoUser(index, userId);
        anychat.UserCameraControl(userId, 1);
        anychat.UserSpeakControl(userId, 1);
    }

    /**
     * 初始化视频
     *
     */
    private void initVideo() {
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
     * 初始化音频，打开本地音频
     */
    private void initAudio() {

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class SurfaceViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parent;
        SurfaceView mSurfaceView;

        public SurfaceViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.surface_parent);
            mSurfaceView = itemView.findViewById(R.id.surface);
        }
    }

}
