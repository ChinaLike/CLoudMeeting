package com.tydic.cm.overwrite;

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
import com.tydic.cm.R;
import com.tydic.cm.adapter.OnLinePeopleAdapter;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.RetrofitMo;
import com.tydic.cm.model.inf.OnItemClickListener;
import com.tydic.cm.model.inf.OnRequestListener;
import com.tydic.cm.util.ScreenUtil;
import com.tydic.cm.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧边弹窗
 * Created by like on 2017-09-19
 */

public class OnlinePop extends RelativePopupWindow implements OnRequestListener {

    private TextView tvNum;

    private RecyclerView recyclerView;
    private List<UsersBean> list = new ArrayList<>();
    private OnLinePeopleAdapter adapter;
    private RetrofitMo mRetrofitMo;//数据获取

    private JsParamsBean bean;//RN传递过来的数据

    public OnlinePop(Activity context, JsParamsBean bean) {
        this.bean = bean;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_online_list, null);
        setContentView(view);
        tvNum = (TextView) view.findViewById(R.id.tvNum);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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
        adapter.setBean(bean);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        onLineUser();
    }

    /**
     * 设置Item点击监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 获取在线人员列表
     */
    public void onLineUser() {
        if (mRetrofitMo != null) {
            mRetrofitMo.onLineUsers(bean.getRoomId(), this);
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

    @Override
    public void onSuccess(int type, Object obj) {
        if (type == Key.ON_LINE_USER) {
            List<UsersBean> mList = (List<UsersBean>) obj;
            tvNum.setText("参会人员(" + mList.size() + ")");
            list.clear();
            list.addAll(mList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(int type, int code) {
        T.showShort("获取在线人员列表数据异常");
        dismiss();
    }

    /**
     * 显示弹窗
     *
     * @param rootView
     */
    public void show(View rootView) {
        onLineUser();
        showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.ALIGN_TOP,
                RelativePopupWindow.HorizontalPosition.RIGHT, true);
    }
}
