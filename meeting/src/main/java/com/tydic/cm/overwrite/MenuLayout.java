package com.tydic.cm.overwrite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tydic.cm.R;
import com.tydic.cm.util.ConvertUtil;
import com.tydic.cm.util.ScreenUtil;

/**
 * 功能菜单键，
 * Created by li on 2017/11/29.
 */

public class MenuLayout extends RelativeLayout implements View.OnClickListener {

    /**
     * 点击类型
     */
    public static final int TYPE_TRANSCRIBE = 0;
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_MICROPHONE = 2;
    public static final int TYPE_SOUND = 3;
    public static final int TYPE_FUN = 4;

    /**
     * 按钮：摄像头开关，摄像头切换，麦克风开关，声音开关，底部会议功能键
     */
    private ImageView meeting_transcribe, meeting_camera, meeting_microphone, meeting_sound, meeting_menu;
    /**
     * 底部、顶部父控件
     */
    private RelativeLayout bottom_menu, top_menu;
    /**
     * 按键回调
     */
    private MenuClickListener menuClickListener;
    /**
     * 是否退出
     */
    private boolean isExit = false;

    private Context mContext;

    public MenuLayout(Context context) {
        this(context, null);
    }

    public MenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        ConvertUtil.init(context);
        isExit = true;
        init(context);

    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_menu, null, false);
        meeting_transcribe = view.findViewById(R.id.meeting_transcribe);
        meeting_transcribe.setOnClickListener(this);
        meeting_camera = view.findViewById(R.id.meeting_camera);
        meeting_camera.setOnClickListener(this);
        meeting_microphone = view.findViewById(R.id.meeting_microphone);
        meeting_microphone.setOnClickListener(this);
        meeting_sound = view.findViewById(R.id.meeting_sound);
        meeting_sound.setOnClickListener(this);
        meeting_menu = view.findViewById(R.id.meeting_menu);
        meeting_menu.setOnClickListener(this);
        bottom_menu = view.findViewById(R.id.bottom_menu);
        top_menu = view.findViewById(R.id.top_menu);
        addView(view);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isExit) {
            startAnimation();
        } else {
            endAnimation();
        }
        return false;
    }

    /**
     * 进入动画
     */
    public void startAnimation() {
        isExit = false;
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(top_menu, "translationY", -ConvertUtil.dp2px(60), 0)
               // ,ObjectAnimator.ofFloat(bottom_menu, "translationY", ScreenUtil.getScreenHeight(mContext), ScreenUtil.getScreenHeight(mContext) - ConvertUtil.dp2px(60))
        );
        set.setDuration(200).start();
    }

    /**
     * 退出动画
     */
    public void endAnimation() {
        isExit = true;
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(top_menu, "translationY", 0, -ConvertUtil.dp2px(60))
               // ,ObjectAnimator.ofFloat(bottom_menu, "translationY", ScreenUtil.getScreenHeight(mContext) - ConvertUtil.dp2px(60), ScreenUtil.getScreenHeight(mContext))
        );
        set.setDuration(200).start();
    }

    @Override
    public void onClick(View view) {
        if (menuClickListener != null) {
            int id = view.getId();
            if (id == R.id.meeting_transcribe) {
                //摄像头的状态，打开，关闭，无摄像头  0-无摄像头，1-有摄像头关闭，2-有摄像头打开
                menuClickListener.onMenuClick(TYPE_TRANSCRIBE, meeting_transcribe);
            } else if (id == R.id.meeting_camera) {
                //摄像头的前后转换
                menuClickListener.onMenuClick(TYPE_CAMERA, meeting_camera);
            } else if (id == R.id.meeting_microphone) {
                //本地声音
                menuClickListener.onMenuClick(TYPE_MICROPHONE, meeting_microphone);
            } else if (id == R.id.meeting_sound) {
                //远程声音
                menuClickListener.onMenuClick(TYPE_SOUND, meeting_sound);
            }
            if (id == R.id.meeting_menu) {
                //菜单键
                menuClickListener.onMenuClick(TYPE_FUN, meeting_menu);
            }
        }
    }

    /**
     * 按钮手动调用点击
     *
     * @param type 点击的哪一个类型
     */
    public void performClick(int type) {
        switch (type) {
            case TYPE_TRANSCRIBE:
                meeting_transcribe.performClick();
                break;
            case TYPE_CAMERA:
                meeting_camera.performClick();
                break;
            case TYPE_MICROPHONE:
                meeting_microphone.performClick();
                break;
            case TYPE_SOUND:
                meeting_sound.performClick();
                break;
            case TYPE_FUN:
                meeting_menu.performClick();
                break;
        }
    }

    public ImageView getTranscribe() {
        return meeting_transcribe;
    }

    public ImageView getCamera() {
        return meeting_camera;
    }

    public ImageView getMicrophone() {
        return meeting_microphone;
    }

    public ImageView getSound() {
        return meeting_sound;
    }

    public ImageView getMenu() {
        return meeting_menu;
    }

    public void setMenuClickListener(MenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    /**
     * 菜单键点击监听
     */
    public interface MenuClickListener {
        /**
         * @param type
         */
        void onMenuClick(int type, ImageView imageView);
    }

}
