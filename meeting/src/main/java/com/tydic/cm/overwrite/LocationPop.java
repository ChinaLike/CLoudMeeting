package com.tydic.cm.overwrite;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cm.R;
import com.tydic.cm.model.inf.OnLocationListener;
import com.tydic.cm.util.ScreenUtil;
import com.tydic.cm.util.T;

/**
 * Created by like on 2017-09-25
 */

public class LocationPop extends RelativePopupWindow implements View.OnClickListener {


    private Context mContext;
    /**
     * 位置切换监听
     */
    private OnLocationListener onLocationListener;
    /**
     * 控件大小及位置
     */
    private LinearLayout.LayoutParams layoutParams;
    /**
     * 文本显示区域
     */
    private TextView text1, text2, text3, text4, text5, text6, text7, text8;
    /**
     * 切换位置前
     */
    private int oldPos = -1;

    public LocationPop(Context context,int showNum) {
        this.mContext = context;
        init(showNum);
    }

    private void init(int showNum) {
        initView(showNum);
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

    /**
     * 设置原来的位置
     * @param oldPos
     */
    public void setOldPos(int oldPos) {
        this.oldPos = oldPos;
    }

    private void initView(int showNum) {
        View view = new View(mContext);
        if (showNum == 0) {
            T.showShort("没有位置可以设置！");
        } else if (showNum == 1) {
            T.showShort("一个人不能设置位置哦！");
        } else if (showNum > 1 && showNum <= 4) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_four_location, null, false);
            initFour(view);
        } else if (showNum > 4 && showNum <= 6) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_six_location, null, false);
            initFour(view);
            initSix(view);
        } else if (showNum > 6 && showNum <= 8) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_eight_loaction, null, false);
            initFour(view);
            initSix(view);
            initEight(view);
        }
        setContentView(view);
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

    private void initFour(View view) {
        text1 = view.findViewById(R.id.text1);
        text1.setOnClickListener(this);
        text2 = view.findViewById(R.id.text2);
        text2.setOnClickListener(this);
        text3 = view.findViewById(R.id.text3);
        text3.setOnClickListener(this);
        text4 = view.findViewById(R.id.text4);
        text4.setOnClickListener(this);
    }

    private void initSix(View view) {
        text5 = view.findViewById(R.id.text5);
        text5.setOnClickListener(this);
        text6 = view.findViewById(R.id.text6);
        text6.setOnClickListener(this);
    }

    private void initEight(View view) {
        text7 = view.findViewById(R.id.text7);
        text7.setOnClickListener(this);
        text8 = view.findViewById(R.id.text8);
        text8.setOnClickListener(this);
    }



    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    @Override
    public void onClick(View view) {
        if (onLocationListener != null) {
            int id = view.getId();
            if (id == R.id.text1) {
                onLocationListener.loacation(oldPos, 0);
            } else if (id == R.id.text2) {
                onLocationListener.loacation(oldPos, 1);
            } else if (id == R.id.text3) {
                onLocationListener.loacation(oldPos, 2);
            } else if (id == R.id.text4) {
                onLocationListener.loacation(oldPos, 3);
            } else if (id == R.id.text5) {
                onLocationListener.loacation(oldPos, 4);
            } else if (id == R.id.text6) {
                onLocationListener.loacation(oldPos, 5);
            } else if (id == R.id.text7) {
                onLocationListener.loacation(oldPos, 6);
            } else if (id == R.id.text8) {
                onLocationListener.loacation(oldPos, 7);
            }
            dismiss();
        }
    }
}
