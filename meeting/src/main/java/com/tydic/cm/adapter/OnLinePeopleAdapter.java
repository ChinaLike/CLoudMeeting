package com.tydic.cm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tydic.cm.R;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.RetrofitMo;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnRequestListener;
import com.tydic.cm.overwrite.ECProgressDialog;
import com.tydic.cm.util.T;

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

    private JsParamsBean beanJs;//RN传递过来的数据

    private RetrofitMo mRetrofitMo;

    private ECProgressDialog progressDialog;

    private OnItemClickListener onItemClickListener;

    public OnLinePeopleAdapter(Context mContext, List<UsersBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mRetrofitMo = new RetrofitMo(mContext);
        progressDialog = new ECProgressDialog(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setBean(JsParamsBean bean) {
        if (bean.getFeedId().equals(bean.getCreated_by()) || bean.getFeedId().equals(bean.getInitiator())) {
            canCtrl = true;
        } else {
            canCtrl = false;
        }
        this.beanJs = bean;
    }

    @Override
    public OnLinePeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OnLinePeopleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_online, parent, false));
    }

    @Override
    public void onBindViewHolder(OnLinePeopleViewHolder holder, final int position) {
        final UsersBean bean = mList.get(position);
        holder.tvName.setText(bean.getNickName());
        holder.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beanJs.getIsBroadcastMode().equals("0") && onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, bean);
                }
            }
        });
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
                    //关闭弹窗
                    dismissDialog();
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
                    //关闭弹窗
                    dismissDialog();
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
                    //关闭弹窗
                    dismissDialog();
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

        if (!canCtrl) {
            holder.tvSpeak.setVisibility(View.INVISIBLE);
            holder.tvVideo.setVisibility(View.INVISIBLE);
            holder.tvSpeaker.setVisibility(View.INVISIBLE);
        } else {
            holder.tvSpeak.setVisibility(View.VISIBLE);
            holder.tvVideo.setVisibility(View.VISIBLE);
            holder.tvSpeaker.setVisibility(View.VISIBLE);
        }

        if (mList.size() <= 1) {
            holder.tvLocation.setVisibility(View.GONE);
        } else {
            holder.tvLocation.setVisibility(View.VISIBLE);
        }

        if (isHasPrimarySpeaker()){
            holder.tvLocation.setVisibility(View.GONE);
        }else {
            holder.tvLocation.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 是否有主讲人
     *
     * @return
     */
    private boolean isHasPrimarySpeaker() {
        for (UsersBean bean : mList) {
            if (bean.getIsPrimarySpeaker().equals(Key.SPEAKER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onSuccess(int type, Object obj) {
        UsersBean dataBean = (UsersBean) obj;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (type == 0) {
            if ("1".equals(dataBean.getAudioStatus())) {
                dataBean.setAudioStatus("0");
            } else {
                dataBean.setAudioStatus("1");
            }
            notifyDataSetChanged();
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
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    class OnLinePeopleViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvName, tvSpeaker, tvVideo, tvSpeak, tvLocation;
        LinearLayout parent;

        public OnLinePeopleViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpeaker = itemView.findViewById(R.id.tvSpeaker);
            tvVideo = itemView.findViewById(R.id.tvVideo);
            tvSpeak = itemView.findViewById(R.id.tvSpeak);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    /**
     * 麦克风控制
     *
     * @param position
     */
    private void sendMicMsg(int position) {
        showDialog();
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
        showDialog();
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
        showDialog();
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

    /**
     * 关闭弹窗
     */
    private void dismissDialog() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        progressDialog.dismiss();
    }

    /**
     * 显示弹窗
     */
    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ECProgressDialog(mContext);
        }
        progressDialog.setPressText("加载中...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

}
