package com.tydic.cm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.tydic.cm.R;
import com.tydic.cm.base.BaseActivity;
import com.tydic.cm.bean.SurfaceBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.LocalHelper;
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
     * 本地视频显示的位置
     */
    private LocalHelper mLocalHelper;
    /**
     * 列数
     */
    private int column = 1;

    private int selfID;

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
        int userID = Integer.parseInt(bean.getUserId());
        //   holder.surfaceLayout.setBackgroundColor(testColor[position]);
        initLocalSurface(position, bean);
        if (bean.getVideoStatus().equals(Key.VIDEO_OPEN)) {
            if (userID == selfID) {

                // 视频如果是采用java采集
                holder.surfaceLayout.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                    holder.surfaceLayout.getHolder().addCallback(AnyChatCoreSDK.mCameraHelper);
                }
                if (bean.getVideoStatus().equals(Key.VIDEO_OPEN)){
                    anychat.UserCameraControl(-1, 1);
                }else {
                    anychat.UserCameraControl(-1, 0);
                }
                if (bean.getAudioStatus().equals(Key.AUDIO_OPEN)){
                    anychat.UserSpeakControl(-1, 1);
                }else {
                    anychat.UserSpeakControl(-1, 0);
                }
                //    holder.surfaceLayout.setZOrderOnTop(false);
            } else {
                initNetSurface(holder.surfaceLayout, bean);
                //     holder.surfaceLayout.setZOrderOnTop(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        initWH();
        params = new RelativeLayout.LayoutParams(width, height);
        return mList == null ? 0 : mList.size();
    }

    public void setSelfID(int selfID) {
        this.selfID = selfID;
    }

    /**
     * 设置显示列数
     * @param column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    public void setLocalHelper(LocalHelper mLocalHelper) {
        this.mLocalHelper = mLocalHelper;
    }

    /**
     * 初始化本地
     *
     * @param position
     * @param bean
     */
    private void initLocalSurface(int position, UsersBean bean) {
        if (mLocalHelper != null) {
            int x = 0;//X轴偏移位置
            int y = 0;//Y轴偏移位置
            if (position >= column){
                x = (position-column) * width;
                y = height;
            }else {
                x = position * width;
                y = 0;
            }

            mLocalHelper.local(position, bean, width, height, x, y);
        }
    }

    /**
     * 初始化远程视频
     */
    private void initNetSurface(SurfaceView surfaceView, UsersBean surfaceBean) {
        int userID = Integer.parseInt(surfaceBean.getUserId());
        int index = anychat.mVideoHelper.bindVideo(surfaceView.getHolder());
        anychat.mVideoHelper.SetVideoUser(index, userID);

        //解决在关闭远程声音下有人进出房间时又能听见远程声音
        if (surfaceBean.getAudioStatus().equals(Key.AUDIO_OPEN)) {
            anychat.UserSpeakControl(userID, 1);
        } else {
            anychat.UserSpeakControl(userID, 0);
        }
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
        if (mList == null || mList.size() == 0 || mList.size() == 1) {
            width = ScreenUtil.getScreenWidth(mContext);
            height = ScreenUtil.getScreenHeight(mContext);
            return;
        }
        int size = mList.size();
        if (size > 1 && size <= 4) {
            //4屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 2 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        } else if (size > 4 && size <= 6) {
            //6屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 3 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        } else if (size > 6) {
            //8屛
            width = (int) (ScreenUtil.getScreenWidth(mContext) / 4 + 0.5);
            height = (int) (ScreenUtil.getScreenHeight(mContext) / 2 + 0.5);
        }
    }

    class SurfaceViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parent;

        SurfaceView surfaceLayout;

        public SurfaceViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.surface_parent);
            surfaceLayout = itemView.findViewById(R.id.surface_view);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
