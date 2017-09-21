package com.tydic.cloudmeeting.overwrite;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cloudmeeting.R;
import com.tydic.cloudmeeting.adapter.OnLinePeopleAdapter;
import com.tydic.cloudmeeting.bean.UsersBean;
import com.tydic.cloudmeeting.constant.Key;
import com.tydic.cloudmeeting.model.RetrofitMo;
import com.tydic.cloudmeeting.model.inf.OnRequestListener;
import com.tydic.cloudmeeting.util.ScreenUtil;
import com.tydic.cloudmeeting.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧边弹窗
 * Created by like on 2017-09-19
 */

public class OnlinePop extends RelativePopupWindow implements OnRequestListener {

    private TextView tvNum;

    private RecyclerView recyclerView;
    private int roomId;
    private List<UsersBean> list = new ArrayList<>();
    private OnLinePeopleAdapter adapter;
    public static int PRIMARYUSERONLINE = 0;

    private RetrofitMo mRetrofitMo;//数据获取

    public OnlinePop(Activity context, String userId, int roomId, String created_by, String initiator) {
        this.roomId = roomId;
        // EventBus.getDefault().register(this);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_online_list, null);
        setContentView(view);
        tvNum = view.findViewById(R.id.tvNum);
        recyclerView = view.findViewById(R.id.recyclerView);

        mRetrofitMo = new RetrofitMo(context);

        int width = ScreenUtil.getScreenWidth(context);
        setWidth(width * 2 / 3);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        adapter = new OnLinePeopleAdapter(context, list);
        adapter.setUserId(userId);
        adapter.setCreated_by(created_by);
        adapter.setInitiator(initiator);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (userId.equals(created_by) || userId.equals(initiator)) {
            adapter.setCanCtrl(true);
        }
        onLineUser();
    }

    /**
     * 获取在线人员列表
     */
    public void onLineUser() {
        if (mRetrofitMo != null) {
            mRetrofitMo.onLineUsers(roomId, this);
        }
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y, boolean fitInScreen) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularReveal(@NonNull final View anchor) {
        final View contentView = getContentView();
        contentView.post(new Runnable() {
            @Override
            public void run() {
                final int[] myLocation = new int[2];
                final int[] anchorLocation = new int[2];
                contentView.getLocationOnScreen(myLocation);
                anchor.getLocationOnScreen(anchorLocation);
                final int cx = anchorLocation[0] - myLocation[0] + anchor.getWidth() / 2;
                final int cy = anchorLocation[1] - myLocation[1] + anchor.getHeight() / 2;

                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                final float finalRadius = (float) Math.hypot(dx, dy);
                Animator animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                animator.setDuration(500);
                animator.start();
            }
        });
    }

//    @Subscribe
//    public void toastSetSpeakerInfo(PrimarySpeakerInfo primarySpeakerInfo) {
//        Context context = getContentView().getContext();
//        String userId = CacheUtil.get(context).getAsString(Constants.ANYCHAT_USER_ID);
//        for (int i = 0; i < list.size(); i++) {
//            MeetingUserListInfo.DataBean dataBean = list.get(i);
//            if (dataBean.getUserId().equals(primarySpeakerInfo.userId)) {
//                String msg;
//                if (userId.equals(primarySpeakerInfo.userId)) {
//                    msg = "您被设置为主讲人";
//                } else {
//                    msg = String.format("%s被设置为主讲人", dataBean.getNickName());
//                }
//                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                break;
//            }
//        }
//    }

    @Override
    public void onSuccess(int type, Object obj) {
        if (type == Key.ON_LINE_USER) {
            List<UsersBean> mList = (List<UsersBean>) obj;
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getIsPrimarySpeaker().equals("1")) {
                    PRIMARYUSERONLINE = 0;
                } else {
                    PRIMARYUSERONLINE = 1;
                }
            }
            tvNum.setText("参会人员(" + mList.size() + ")");
            list.clear();
            list.addAll(mList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(int type, int code) {
        T.showShort("获取在线人员列表数据异常");
    }
}
