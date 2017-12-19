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
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.model.JsAndroidModule;
import com.tydic.cm.util.ConvertUtil;
import com.tydic.cm.util.T;

/**
 * 功能菜单键
 * Created by like on 2017-09-19
 */

public class MeetingMenuPop extends RelativePopupWindow implements View.OnClickListener {

    private Context mContext;
    private JsParamsBean bean;


    public interface MenuClickListener {
        void onClick(int index);
    }

    private MenuClickListener menuClickListener;

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public MeetingMenuPop(Context context, JsParamsBean bean) {
        this.mContext = context;
        this.bean = bean;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_metting_menu, null);
        setContentView(view);

        view.findViewById(R.id.llSpeak).setOnClickListener(this);
        view.findViewById(R.id.llInvite).setOnClickListener(this);
        view.findViewById(R.id.llShare).setOnClickListener(this);
        view.findViewById(R.id.llExit).setOnClickListener(this);
        view.findViewById(R.id.llPwd).setOnClickListener(this);
        view.findViewById(R.id.carousel).setOnClickListener(this);
//        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ConvertUtil.dp2px(260));
        setHeight(ConvertUtil.dp2px(190));
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

        if (id == R.id.llSpeak) {
            menuClickListener.onClick(0);
        } else if (id == R.id.llInvite) {
            if (isBroadcast(Integer.parseInt(bean.getIsBroadcastMode()))) {
                return;
            }
            menuClickListener.onClick(1);
            if (bean.getFeedId().equals(bean.getCreated_by()) || bean.getFeedId().equals(bean.getInitiator())) {
                JsAndroidModule.sendEvent("InviteMeeting");
                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, "你不是会议管理员，无法操作！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.llShare) {
            if (isBroadcast(Integer.parseInt(bean.getIsBroadcastMode()))) {
                return;
            }
            menuClickListener.onClick(2);
            JsAndroidModule.sendEvent("MaterialMeeting");
            ((Activity) mContext).finish();
        } else if (id == R.id.llPwd) {
            if (isBroadcast(Integer.parseInt(bean.getIsBroadcastMode()))) {
                return;
            }
            menuClickListener.onClick(4);
        } else if (id == R.id.carousel) {
            if (isBroadcast(Integer.parseInt(bean.getIsBroadcastMode()))) {
                T.showShort("当前为直播模式,不可设置视频轮播!");
            } else {
                menuClickListener.onClick(5);
            }
        }

        if (id == R.id.llExit) {
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
