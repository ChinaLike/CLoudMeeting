package com.tydic.cm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cm.R;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.helper.LocalViewHelper;
import com.tydic.cm.util.CacheUtil;
import com.tydic.cm.util.ScreenUtil;

import java.util.List;

/**
 * 远程界面适配
 * Created by like on 2017-09-26
 */

public class SurfaceAdapter extends RecyclerView.Adapter<SurfaceAdapter.SurfaceViewHolder> {

    private Context mContext;

    private List<UsersBean> mList;
    /**
     * 每一屏幕的宽和高
     */
    private int width, height;

    private RelativeLayout.LayoutParams params;

    private AnyChatCoreSDK anychat;
    /**
     * 列数
     */
    private int column = 1;

    private int selfID;

    private LocalViewHelper localViewHelper;

    public SurfaceAdapter(Context mContext, List<UsersBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public SurfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SurfaceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_surface, parent, false));
    }

    @Override
    public void onBindViewHolder(SurfaceViewHolder holder, int position) {
        UsersBean bean = mList.get(position);
        holder.parent.setLayoutParams(params);
        nickName(holder.tvName, bean);
        videoControl(holder.surfaceLayout, holder.camera_img, holder.parent, bean);
        audioControl(bean);
    }

    /**
     * 设置本地视图移除监听
     * @param localViewHelper
     */
    public void setLocalViewHelper(LocalViewHelper localViewHelper) {
        this.localViewHelper = localViewHelper;
    }

    /**
     * 设置昵称
     *
     * @param textView
     * @param bean
     */
    private void nickName(TextView textView, UsersBean bean) {
        int userId = Integer.parseInt(bean.getUserId());
        if (userId == 0 || bean.getNickName() == null) {
            textView.setText("");
        } else {
            textView.setText(bean.getNickName());
        }
    }

    /**
     * 视频控制
     *
     * @param surfaceView
     * @param imageView
     * @param layout
     * @param bean
     */
    private void videoControl(SurfaceView surfaceView, ImageView imageView, RelativeLayout layout, UsersBean bean) {
        String videoStatus = bean.getVideoStatus();
        int userId = Integer.parseInt(bean.getUserId());
        boolean isValid = userId == 0 ? false : true;
        if (videoStatus.equals(Key.VIDEO_OPEN) && isValid) {
            //打开视频
            surfaceView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            if (userId == selfID) {
                //在轮播状态下监听才回掉&& "1".equals(mCacheUtil.getAsString(Key.IS_START))
                if (localViewHelper != null ){
                    localViewHelper.removeView();
                }
                initLocalVideo(surfaceView.getHolder());
            } else {
                initNetSurface(surfaceView.getHolder(), bean);
            }
//               anychat.UserCameraControl(-1, 1);//本地流开始上传
        } else if (videoStatus.equals(Key.VIDEO_CLOSE) && isValid) {
            //关闭视频
            closeCamera(bean);//先关闭摄像头释放资源
            surfaceView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.shut_camera);//设置关闭摄像头标志
            layout.setBackgroundColor(0xFF12182d);//设置背景颜色
            //  anychat.UserCameraControl(-1, 0);//本地流停止上传
        } else if (videoStatus.equals(Key.VIDEO_NO) && isValid) {
            //没有摄像头
            closeCamera(bean);//先关闭摄像头释放资源
            surfaceView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.shut_camera);//设置没有摄像头标志
            layout.setBackgroundColor(0xFF12182d);//设置背景颜色
            //  anychat.UserCameraControl(-1, 0);//本地流停止上传
        } else {
            //对视频不处理
            closeCamera(bean);//先关闭摄像头释放资源
            surfaceView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            layout.setBackgroundColor(0xFF1F232E);
            //   anychat.UserCameraControl(-1, 0);//本地流停止上传
        }
    }

    /**
     * 关闭摄像头
     *
     * @param bean
     */
    private void closeCamera(UsersBean bean) {
        int userId = Integer.parseInt(bean.getUserId());
        if (userId == selfID) {
            AnyChatCoreSDK.mCameraHelper.CloseCamera();
        }
    }

    /**
     * 音频控制
     *
     * @param bean
     */
    private void audioControl(UsersBean bean) {
        String audioStatus = bean.getAudioStatus();
        int userID = Integer.parseInt(bean.getUserId());
        if (audioStatus.equals(Key.AUDIO_OPEN)) {
            //打开音频
            anychat.UserSpeakControl(userID, 1);
        } else if (audioStatus.equals(Key.AUDIO_CLOSE)) {
            //关闭音频
            anychat.UserSpeakControl(userID, 0);
        } else {
            //对音频不处理
        }
    }

    /**
     * 初始化本地视频
     *
     * @param surfaceHolder
     */
    private void initLocalVideo(final SurfaceHolder surfaceHolder) {
        // 视频如果是采用java采集
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            surfaceHolder.addCallback(AnyChatCoreSDK.mCameraHelper);
        }
    }


    @Override
    public int getItemCount() {
        initWH();

        //处理一屛时右侧有白色分割线
        if (width == ScreenUtil.getScreenWidth(mContext)) {
            params = new RelativeLayout.LayoutParams(width, height);
        } else {
            params = new RelativeLayout.LayoutParams(width - 1, height);
        }
        int size = mList == null ? 0 : mList.size();
        return size;
    }

    public void setSelfID(int selfID) {
        this.selfID = selfID;
    }

    /**
     * 设置显示列数
     *
     * @param column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * 初始化远程视频
     */
    private void initNetSurface(SurfaceHolder surfaceHolder, UsersBean surfaceBean) {
        int userID = Integer.parseInt(surfaceBean.getUserId());
        int index = anychat.mVideoHelper.bindVideo(surfaceHolder);
        anychat.mVideoHelper.SetVideoUser(index, userID);

        if (surfaceBean.getVideoStatus().equals(Key.VIDEO_OPEN)) {
            anychat.UserCameraControl(userID, 1);
        } else {
            anychat.UserCameraControl(userID, 0);
        }

    }

    public void setAnychat(AnyChatCoreSDK anychat) {
        this.anychat = anychat;
    }

    /**
     * 初始化宽高
     */
    private void initWH() {
//        if (mList == null || mList.size() == 0 || mList.size() == 1) {
//            width = ScreenUtil.getScreenWidth(mContext);
//            height = ScreenUtil.getScreenHeight(mContext);
//            return;
//        }
//        int size = mList.size();
//        if (size > 1 && size <= 4) {
//            //4屛
//            width = (int) (ScreenUtil.getScreenWidth(mContext) / 2 + 0.5);
//            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
//        } else if (size > 4 && size <= 6) {
//            //6屛
//            width = (int) (ScreenUtil.getScreenWidth(mContext) / 3 + 0.5);
//            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
//        } else if (size > 6) {
//            //8屛
//            width = (int) (ScreenUtil.getScreenWidth(mContext) / 4 + 0.5);
//            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
//        }
        if (column == 1) {
            width = ScreenUtil.getScreenWidth(mContext);
            height = ScreenUtil.getScreenHeight(mContext);
        } else if (column == 2) {
            //4屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 2 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        } else if (column == 3) {
            //6屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 3 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        } else if (column == 4) {
            //8屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 4 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        }
    }

    class SurfaceViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parent;

        SurfaceView surfaceLayout;

        TextView tvName;

        ImageView camera_img;

        public SurfaceViewHolder(View itemView) {
            super(itemView);
            parent = (RelativeLayout) itemView.findViewById(R.id.surface_parent);
            surfaceLayout = (SurfaceView) itemView.findViewById(R.id.surface_view);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            camera_img = (ImageView) itemView.findViewById(R.id.camera_close);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
