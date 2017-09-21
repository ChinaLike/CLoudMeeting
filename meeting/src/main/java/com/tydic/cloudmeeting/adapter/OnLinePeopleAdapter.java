package com.tydic.cloudmeeting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tydic.cloudmeeting.R;
import com.tydic.cloudmeeting.bean.UsersBean;
import com.tydic.cloudmeeting.constant.Key;
import com.tydic.cloudmeeting.model.RetrofitMo;
import com.tydic.cloudmeeting.model.inf.OnRequestListener;
import com.tydic.cloudmeeting.overwrite.ECProgressDialog;
import com.tydic.cloudmeeting.util.T;

import java.util.List;


/**
 * 在线人员适配器
 * Created by like on 2017-09-19
 */

public class OnLinePeopleAdapter extends RecyclerView.Adapter<OnLinePeopleAdapter.OnLinePeopleViewHolder> implements OnRequestListener {

    private Context mContext;

    private List<UsersBean> mList;

    //会议发起人或者创建者才有操作权
    private boolean canCtrl = false;
    //用户ID
    private String userId;
    //创建者ID
    private String created_by;
    //发起人
    private String initiator;

    private RetrofitMo mRetrofitMo;

    private ECProgressDialog progressDialog;

    public OnLinePeopleAdapter(Context mContext, List<UsersBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mRetrofitMo = new RetrofitMo(mContext);
        progressDialog = new ECProgressDialog(mContext);
    }

    @Override
    public OnLinePeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OnLinePeopleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_online, parent, false));
    }

    @Override
    public void onBindViewHolder(OnLinePeopleViewHolder holder, int position) {
        final UsersBean bean = mList.get(position);
        holder.tvName.setText(bean.getNickName());
        holder.tvSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canCtrl) {
                    int index = mList.indexOf(bean);
                    if (index == -1) {
                        T.showShort("数据有误");
                        return;
                    }
                    sendMicMsg(index);
                } else {
                    T.showShort("您无权进行此操作");
                }
            }
        });

        if ("1".equals(bean.getAudioStatus())) {
            holder.iv.setImageResource(R.mipmap.meeting_fullscreen_microphone_enable);
            holder.tvSpeak.setText("取消发言");
        } else {
            holder.iv.setImageResource(R.mipmap.meeting_fullscreen_microphone_disable);
            holder.tvSpeak.setText("发言");
        }

        holder.tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canCtrl) {
                    int index = mList.indexOf(bean);
                    if (index == -1) {
                        T.showShort("数据有误");
                        return;
                    }
                    sendCameraMsg(index);
                } else {
                    T.showShort("您无权进行此操作");
                }
            }
        });

        if ("2".equals(bean.getVideoStatus())) {
            holder.tvVideo.setText("关闭视频");
        } else {
            holder.tvVideo.setText("视频");
        }

        holder.tvSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canCtrl) {
                    int index = mList.indexOf(bean);
                    if (index == -1) {
                        T.showShort("数据有误");
                        return;
                    }
                    sendSpeakerMsg(index);
                } else {
                    T.showShort("您无权进行此操作");
                }
            }
        });

        if ("1".equals(bean.getIsPrimarySpeaker())) {
            holder.tvSpeaker.setText("取消主讲人");
        } else {
            holder.tvSpeaker.setText("主讲人");
        }

        if (!(userId.equals(created_by) || userId.equals(initiator))) {
            holder.tvSpeak.setVisibility(View.GONE);
            holder.tvVideo.setVisibility(View.GONE);
            holder.tvSpeaker.setVisibility(View.GONE);
        } else {
            holder.tvSpeak.setVisibility(View.VISIBLE);
            holder.tvVideo.setVisibility(View.VISIBLE);
            holder.tvSpeaker.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onSuccess(int type, Object obj) {
        UsersBean dataBean = (UsersBean) obj;
        if (type == 0) {
            if ("1".equals(dataBean.getAudioStatus())) {
                dataBean.setAudioStatus("0");
            } else {
                dataBean.setAudioStatus("1");
            }
            if (mList != null && mList.size() > 1) {
                notifyDataSetChanged();
            }
        } else if (type == 1) {
            if ("2".equals(dataBean.getVideoStatus())) {
                dataBean.setVideoStatus("1");
            } else {
                dataBean.setVideoStatus("2");
            }
            notifyDataSetChanged();
        } else if (type == 2) {
            if ("1".equals(dataBean.getIsPrimarySpeaker())) {
                dataBean.setIsPrimarySpeaker("0");
            } else {
                dataBean.setIsPrimarySpeaker("1");
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onError(int type, int code) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    class OnLinePeopleViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvName, tvSpeaker, tvVideo, tvSpeak;

        public OnLinePeopleViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpeaker = itemView.findViewById(R.id.tvSpeaker);
            tvVideo = itemView.findViewById(R.id.tvVideo);
            tvSpeak = itemView.findViewById(R.id.tvSpeak);
        }
    }

    public void setCanCtrl(boolean canCtrl) {
        this.canCtrl = canCtrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /**
     * 麦克风控制
     *
     * @param position
     */
    private void sendMicMsg(int position) {
        if (progressDialog == null) {
            progressDialog = new ECProgressDialog(mContext);
        }
        progressDialog.setPressText("加载中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String userId, character;
        UsersBean dataBean = mList.get(position);
        if ("1".equals(dataBean.getAudioStatus())) {
            character = Key.CLIENT_DISABLE_MIC + "";

        } else {
            character = Key.CLIENT_ENAble_MIC + "";
        }
        userId = mList.get(position).getUserId();
        mRetrofitMo.sendMsg(userId, character, dataBean, 0, this);
    }

    /**
     * 对摄像头操作
     *
     * @param position
     */
    private void sendCameraMsg(int position) {
        if (progressDialog == null) {
            progressDialog = new ECProgressDialog(mContext);
        }
        progressDialog.setPressText("加载中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String userId, character;
        UsersBean dataBean = mList.get(position);
        if ("2".equals(dataBean.getVideoStatus())) {
            character = Key.CLIENT_DISABLE_VIDEO + "";

        } else {
            character = Key.CLIENT_ENAble_VIDEO + "";
        }
        userId = mList.get(position).getUserId();
        mRetrofitMo.sendMsg(userId, character, dataBean, 1, this);

    }

    /**
     * 设置主讲人
     *
     * @param position
     */
    private void sendSpeakerMsg(int position) {
        if (progressDialog == null) {
            progressDialog = new ECProgressDialog(mContext);
        }
        progressDialog.setPressText("加载中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        String userId, character;
        UsersBean dataBean = mList.get(position);
        if ("1".equals(dataBean.getIsPrimarySpeaker())) {
            character = Key.CLIENT_RESET_PRIMARY_SPEAKER + "";

        } else {
            character = Key.CLIENT_SET_PRIMARY_SPEAKER + "";
        }
        userId = mList.get(position).getUserId();
        mRetrofitMo.sendMsg(userId, character, dataBean, 2, this);

    }

}