package com.tydic.cloudmeeting.overwrite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cloudmeeting.bean.SurfaceBean;
import com.tydic.cloudmeeting.config.ConfigEntity;
import com.tydic.cloudmeeting.config.ConfigService;
import com.tydic.cloudmeeting.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频显示控件自定义
 * Created by like on 2017-09-20
 */

public class SurfaceLayout extends LinearLayout {

    //显示列数
    private int column = 2;
    //显示行数
    private int row = 1;

    private Context mContext;

    private List<SurfaceView> surfaceViews = new ArrayList<>();

    private int[] colors = new int[]{Color.RED, Color.GRAY, Color.BLUE};

    private Handler mHandler;

    public static final int MSG_CHECKAV = 1;

    private Timer mTimerCheckAv;

    private TimerTask mTimerTask;

    private AnyChatCoreSDK anychat;

    private Boolean[] otherVideoOpened;//视频是否打开

    private ConfigEntity mConfigEntity;

    public SurfaceLayout(Context context) {
        this(context, null);
    }

    public SurfaceLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context);

    }


    /**
     * 初始化控件
     */
    private void init(Context context) {
        //垂直布局
        mConfigEntity = ConfigService.LoadConfig(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        creatView();
    }

    /**
     * 创建视图
     */
    private void creatView() {
        surfaceViews.clear();
        int height = ScreenUtil.getScreenHeight(mContext) / row;
        int width = ScreenUtil.getScreenWidth(mContext) / column;

        //设置视频的初始状态
        otherVideoOpened = new Boolean[row * column];
        for (int i = 0; i < row * column; i++) {
            otherVideoOpened[i] = false;
        }

        for (int i = 0; i < row; i++) {
            //初始化行数
            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(HORIZONTAL);
            LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            layout.setLayoutParams(params);
            for (int j = 0; j < column; j++) {
                SurfaceView view = new SurfaceView(mContext);
                LayoutParams itemLayoutParams = new LayoutParams(width, height);
                view.setLayoutParams(itemLayoutParams);
                view.setBackgroundColor(colors[j]);
                layout.addView(view);
                surfaceViews.add(view);
            }
            addView(layout);
        }

        invalidate();
    }

    /**
     * 实时音视频
     */
    private void updateAV(final List<SurfaceBean> list) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CHECKAV:
                        //实时视频刷新
                        checkVideoStatus(list);
                        break;

                }
            }
        };
        initTimerCheckAv();
    }

    /**
     * 检查视频状态
     */
    private void checkVideoStatus(List<SurfaceBean> list) {
        for (int i = 0; i < list.size(); i++) {
            SurfaceBean bean = list.get(i);
            if (bean.isLocal()) {
                //本地视频特殊处理
                if (!otherVideoOpened[i]) {
                    if (anychat.GetCameraState(-1) == 2 && anychat.GetUserVideoWidth(-1) != 0) {
                        SurfaceHolder holder = surfaceViews.get(i).getHolder();

                        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                            holder.setFormat(PixelFormat.RGB_565);
                            holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
                        }
                        Surface s = holder.getSurface();
                        anychat.SetVideoPos(-1, s, 0, 0, 0, 0);
                        otherVideoOpened[i] = true;
                    }
                }
            } else {
                //远程视频处理
                if (!otherVideoOpened[i]) {
                    if (anychat.GetCameraState(bean.getUserId()) == 2
                            && anychat.GetUserVideoWidth(bean.getUserId()) != 0) {
                        SurfaceHolder holder = surfaceViews.get(i).getHolder();

                        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                            holder.setFormat(PixelFormat.RGB_565);
                            holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
                        }
                        Surface s = holder.getSurface();
                        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                            int videoIndex = anychat.mVideoHelper.bindVideo(holder);
                            anychat.mVideoHelper.SetVideoUser(videoIndex, bean.getUserId());
                        } else
                            anychat.SetVideoPos(bean.getUserId(), s, 0, 0, 0, 0);
                        otherVideoOpened[i] = true;
                    }
                }
            }
        }
        double v = anychat.QueryUserStateInt(-1, AnyChatDefine.BRAC_USERSTATE_VIDEOBITRATE) / 1000.0;
    }

    /**
     * 根据界面数据自动创建SurfaceView
     *
     * @param size
     */
    public void configView(int size) {
        if (size < 4) {
            column = size;
            row = 1;
        } else if (size == 4) {
            column = 2;
            row = 2;
        } else if (size == 5 || size == 6) {
            column = 3;
            row = 2;
        } else if (size == 7 || size == 8) {
            column = 4;
            row = 2;
        } else if (size > 8) {
            column = 2;
            row = 2;
        }
        creatView();
    }

    /**
     * 显示数据
     *
     * @param list
     */
    public void showView(List<SurfaceBean> list, AnyChatCoreSDK anychat) {
        this.anychat = anychat;
        if (surfaceViews.size() != list.size()) {
            return;
        }
        for (int i = 0; i < surfaceViews.size(); i++) {
            SurfaceView surfaceView = surfaceViews.get(i);
            SurfaceBean bean = list.get(i);
            if (bean.isLocal()) {
                //本地视频

                showLocalVideo(surfaceView, anychat);
            } else {
                //远程视频
                showNetVideo(surfaceView, bean.getUserId(), anychat);
            }
        }
        //实时更新音视频
        updateAV(list);
    }

    /**
     * 显示本地视频
     */
    private void showLocalVideo(SurfaceView surfaceView, AnyChatCoreSDK anychat) {
        // 视频如果是采用java采集
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            surfaceView.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
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


    /**
     * 显示远程视频
     */
    private void showNetVideo(SurfaceView surfaceView, int userID, AnyChatCoreSDK anychat) {
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
        int index = anychat.mVideoHelper.bindVideo(surfaceView.getHolder());
        anychat.mVideoHelper.SetVideoUser(index, userID);
        anychat.UserCameraControl(userID, 1);
        anychat.UserSpeakControl(userID, 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setColumn(int column) {
        this.column = column;
        creatView();
    }

    public void setRow(int row) {
        this.row = row;
        creatView();
    }
}
