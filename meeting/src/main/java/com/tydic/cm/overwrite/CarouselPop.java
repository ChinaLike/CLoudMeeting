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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.tydic.cm.R;
import com.tydic.cm.adapter.CarouselAdapter;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.helper.BannerHelper;
import com.tydic.cm.util.CacheUtil;
import com.tydic.cm.util.ConvertUtil;
import com.tydic.cm.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2017/11/28.
 */

public class CarouselPop extends RelativePopupWindow implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Context mContext;
    private CarouselAdapter mCarouselAdapter;
    private Button mButton;
    private ListView mListView;
    private BannerHelper bannerHelper;
    private RadioButton mRadioButtonFullScreen;
    private RadioButton mRadioButtonNoFullScreen;
    private RadioGroup mRadioGroup;
    private List<UsersBean> mUserBean = new ArrayList<>();
    private int carouselMode = 1;
    private CacheUtil cacheUtil;
    /**
     * 是否轮播
     */
    private boolean isCarousel = false;


    public CarouselPop(Context context) {
        this.mContext = context;
        cacheUtil = CacheUtil.get(mContext);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_meeting_carousel, null);
        setContentView(view);
        mButton = (Button) view.findViewById(R.id.carousel_btn);
        mButton.setOnClickListener(this);
        mListView = (ListView) view.findViewById(R.id.carousel_list);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.carousel_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioButtonFullScreen = (RadioButton) view.findViewById(R.id.carousel_fullscreen);
        mRadioButtonNoFullScreen = (RadioButton) view.findViewById(R.id.carousel_not_fullscreen);
        setWidth(ConvertUtil.dp2px(260));
        setHeight(ConvertUtil.dp2px(190));
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        mCarouselAdapter = new CarouselAdapter(mContext, mUserBean);
        mListView.setAdapter(mCarouselAdapter);
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

    public void setBannerHelper(BannerHelper bannerHelper) {
        this.bannerHelper = bannerHelper;
    }

    public void setUserBean(List<UsersBean> mUserBean) {
        this.mUserBean = mUserBean;
    }

    @Override
    public void onClick(View view) {
        if (isCarousel){
            //在轮播，处理关闭轮播状态
            cacheUtil.put(Key.IS_START, "0");
            isCarousel = false;
            mButton.setText("开始");
            //清除数据
            mCarouselAdapter.clear();
            bannerHelper.banner(-1, null);
            dismiss();
        }else {
            //没有轮播，处理打开轮播状态
            cacheUtil.put(Key.IS_START, "1");
            isCarousel = true;
            mButton.setText("取消");
            List<UsersBean> carouselUserList = mCarouselAdapter.getChooseBean();
            if (carouselUserList.size() > carouselMode) {
                bannerHelper.banner(carouselMode, carouselUserList);
                dismiss();
            }else {
                T.showShort("轮播人数不足!");
            }
        }
    }

    /**
     * 是否在轮播
     * @return
     */
    public boolean isCarousel() {
        return isCarousel;
    }

    /**
     * 显示弹窗
     *
     * @param rootView
     */
    public void show(View rootView) {
        //去除本地视频
        for (int i = 0; i < mUserBean.size(); i++) {
            if (cacheUtil.getAsString(Key.ANYCHAT_USER_ID).equals(mUserBean.get(i).getUserId())) {
                mUserBean.remove(i);
            }
        }
        mCarouselAdapter.notifyDataSetChanged(mUserBean);
        showOnAnchor(rootView, RelativePopupWindow.VerticalPosition.CENTER,
                RelativePopupWindow.HorizontalPosition.CENTER, false);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (mRadioButtonFullScreen.getId() == i) {
            carouselMode = 1;
        } else if (mRadioButtonNoFullScreen.getId() == i) {
            carouselMode = 4;
        }
    }
}