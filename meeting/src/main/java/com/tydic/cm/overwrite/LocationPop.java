package com.tydic.cm.overwrite;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cm.R;
import com.tydic.cm.adapter.LocationAdapter;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnLocationListener;
import com.tydic.cm.util.ScreenUtil;
import com.tydic.cm.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by like on 2017-09-25
 */

public class LocationPop extends RelativePopupWindow implements OnItemClickListener {


    private Context mContext;
    /**
     * 位置切换监听
     */
    private OnLocationListener onLocationListener;
    /**
     * 切换位置前
     */
    private int oldPos = -1;

    private UsersBean bean;
    /**
     * 位置显示列表
     */
    private RecyclerView recyclerView;

    private LocationAdapter adapter;

    public LocationPop(Context context, int showNum) {
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
     *
     * @param oldPos
     */
    public void setOldPos(int oldPos) {
        this.oldPos = oldPos;
    }

    /**
     * 需要切换的Bean
     *
     * @param bean
     */
    public void setBean(UsersBean bean) {
        this.bean = bean;
    }

    private void initView(int showNum) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_location, null, false);
        recyclerView = view.findViewById(R.id.location_recy);
        List<String> list = new ArrayList<>();
        int clounm = 0;
        if (showNum == 0) {
            T.showShort("没有位置可以设置！");
        } else if (showNum == 1) {
            T.showShort("一个人不能设置位置哦！");
        } else if (showNum > 1 && showNum <= 4) {
            clounm = 4;
        } else if (showNum > 4 && showNum <= 6) {
            clounm = 6;
        } else if (showNum > 6 && showNum <= 8) {
            clounm = 8;

        }
        setContentView(view);
        for (int i = 0; i < clounm; i++) {
            list.add((i + 1) + "");
        }
        adapter = new LocationAdapter(list, mContext, this);
        GridLayoutManager manager = new GridLayoutManager(mContext, clounm / 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    @Override
    public void onItemClick(int position, UsersBean bean) {
        dismiss();
        if (onLocationListener != null) {
            onLocationListener.loacation(oldPos, position, this.bean);
        }
    }

    /**
     * 显示弹窗
     * @param rootView
     */
    public void show(View rootView){
        showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }
}
