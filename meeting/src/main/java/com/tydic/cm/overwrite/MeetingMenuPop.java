package com.tydic.cm.overwrite;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cm.R;
import com.tydic.cm.model.JsAndroidModule;
import com.tydic.cm.util.ScreenUtil;

/**
 * 功能菜单键
 * Created by like on 2017-09-19
 */

public class MeetingMenuPop extends RelativePopupWindow implements View.OnClickListener {

    private String meetingId;
    private Context mContext;
    private String createdBy;
    private int isBroadcastMode;

    private String userId;

    public interface MenuClickListener {
        void onClick(int index);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private MenuClickListener menuClickListener;

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public MeetingMenuPop(Context context, String meetingId, String createdBy, int isBroadcastMode) {
        this.mContext = context;
        this.meetingId = meetingId;
        this.createdBy = createdBy;
        this.isBroadcastMode = isBroadcastMode;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_metting_menu, null);
        setContentView(view);

        view.findViewById(R.id.tvSpeak).setOnClickListener(this);
        view.findViewById(R.id.tvInvite).setOnClickListener(this);
        view.findViewById(R.id.tvShare).setOnClickListener(this);
        view.findViewById(R.id.tvExit).setOnClickListener(this);
        view.findViewById(R.id.tvPwd).setOnClickListener(this);
        view.findViewById(R.id.tvMaterial).setOnClickListener(this);

//        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ScreenUtil.getScreenWidth(mContext) / 2);
        setHeight(ScreenUtil.getScreenHeight(mContext) / 2);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
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

    /**
     * 是否直播模式
     *
     * @param isBroadcastMode
     * @return
     */
    private boolean isBroadcast(int isBroadcastMode) {
        if (isBroadcastMode == 1) {
            Toast.makeText(mContext, "当前为会议直播模式,无法操作!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (menuClickListener == null) {
            return;
        }

        if (id == R.id.tvSpeak) {
            menuClickListener.onClick(0);
        } else if (id == R.id.tvInvite) {
            if (isBroadcast(isBroadcastMode)) {
                return;
            }
            menuClickListener.onClick(1);
            if (userId.equals(createdBy)) {
//                    Intent intent = new Intent();
//                    intent.setAction("com.broadcast.meeting.broadcast");
//                    intent.putExtra("data", "1");
//                    mContext.sendBroadcast(intent, null);
                JsAndroidModule.sendEvent("InviteMeeting");
                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, "你不是会议管理员，无法操作！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tvShare) {
            if (isBroadcast(isBroadcastMode)) {
                return;
            }
            menuClickListener.onClick(2);
//                Intent intent = new Intent();
//                intent.setAction("com.broadcast.meeting.broadcast");
//                intent.putExtra("data", "2");
//                mContext.sendBroadcast(intent, null);
            JsAndroidModule.sendEvent("MaterialMeeting");
            ((Activity) mContext).finish();
        } else if (id == R.id.tvPwd) {
            if (isBroadcast(isBroadcastMode)) {
                return;
            }
            menuClickListener.onClick(4);
        } else if (id == R.id.tvMaterial) {
            if (isBroadcast(isBroadcastMode)) {
                return;
            }
            menuClickListener.onClick(5);
        }

        if (id == R.id.tvExit) {
            menuClickListener.onClick(3);
        }
    }

    /**
     * 显示弹窗
     *
     * @param rootView
     */
    public void show(View rootView) {
        showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }

}
