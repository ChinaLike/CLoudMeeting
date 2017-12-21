package com.tydic.cm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.bean.VideoLayoutParamsBean;
import com.tydic.cm.constant.Key;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yufeng on 2017/12/7.
 */

public class InitializeSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView microPhone;
    private TextView camera;
    private boolean isOpenCamera = true;
    private boolean isOpenSound = true;
    private Button initBtn;
    private int isBroadcastMode;
    private String getVideoStatus = "1";
    private String getAudioStatus = "0";
    protected JsParamsBean mJsParamsBean;//RN-传递过来的数据
    private ImageView ivBack;

    private Drawable cameraDrawableClose;
    private Drawable cameraDrawableOpen;

    private Drawable micDrawableClose;
    private Drawable micDrawableOpen;
    private VideoLayoutParamsBean videoLayoutParamsBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        setContentView(R.layout.activity_initialize_layout);
        isBroadcastMode = getIntent().getIntExtra("isBroadcastMode", 0);
        mJsParamsBean = (JsParamsBean) getIntent().getSerializableExtra(Key.JS_PARAMS);
        microPhone = (TextView) findViewById(R.id.init_sound);
        microPhone.setOnClickListener(this);
        camera = (TextView) findViewById(R.id.init_camera);
        camera.setOnClickListener(this);
        initBtn = (Button) findViewById(R.id.init_btn);
        initBtn.setOnClickListener(this);
        ivBack = (ImageView) findViewById(R.id.custom_actionbar_back);
        ivBack.setOnClickListener(this);
        initImage();
        isOpenCamera();
        isOpenSound();
        getLayoutJsonFileFromAssets();
    }

    /**
     * 初始化图片参数
     */
    private void initImage() {
        cameraDrawableClose = getResources().getDrawable(R.mipmap.img_meeting_camera_close);
        cameraDrawableOpen = getResources().getDrawable(R.mipmap.img_meeting_camera_open);
        micDrawableClose = getResources().getDrawable(R.mipmap.meeting_microphone_disable);
        micDrawableOpen = getResources().getDrawable(R.mipmap.img_meeting_microphone);
        int width = cameraDrawableClose.getIntrinsicWidth();
        int height = cameraDrawableClose.getIntrinsicHeight();
        cameraDrawableClose.setBounds(0, 0, width, height);
        cameraDrawableOpen.setBounds(0, 0, width, height);
        micDrawableClose.setBounds(0, 0, width, height);
        micDrawableOpen.setBounds(0, 0, width, height);
    }

    /**
     * 初始化Activity的显示模式
     */
    private void initStatus() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void isOpenCamera() {
        if (isOpenCamera) {
            //执行关闭摄像头
            camera.setCompoundDrawables(null, cameraDrawableClose
                    , null, null);
            camera.setText("关闭摄像头");
            getVideoStatus = "1";
        } else {
            //执行打开摄像头
            camera.setCompoundDrawables(null, cameraDrawableOpen
                    , null, null);
            camera.setText("开启摄像头");
            getVideoStatus = "2";
        }
        isOpenCamera = !isOpenCamera;
    }

    private void isOpenSound() {
        if (isOpenSound) {
            //执行关闭摄像头
            microPhone.setCompoundDrawables(null, micDrawableClose
                    , null, null);
            microPhone.setText("开启麦克风");
            getAudioStatus = "0";
        } else {
            //执行打开摄像头
            microPhone.setCompoundDrawables(null, micDrawableOpen
                    , null, null);
            microPhone.setText("关闭麦克风");
            getAudioStatus = "1";
        }
        isOpenSound = !isOpenSound;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.init_camera) {
            isOpenCamera();
        } else if (i == R.id.init_sound) {
            isOpenSound();
        }
        if (i == R.id.init_btn) {
            Intent intent;
            if (isBroadcastMode == 0) {
                intent = new Intent(this, CommonActivity.class);
            } else {
                intent = new Intent(this, OnLiveActivity.class);
            }
            intent.putExtra("getVideoStatus", getVideoStatus);
            intent.putExtra("getAudioStatus", getAudioStatus);
            intent.putExtra(Key.JS_PARAMS, mJsParamsBean);
            startActivity(intent);
            finish();
        } else if (i == R.id.custom_actionbar_back) {
            finish();
        }
    }


    private void getLayoutJsonFileFromAssets() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream("assets/" + "VideoConfig.json"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String sLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((sLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(sLine);
            }
            Gson gson = new Gson();
            videoLayoutParamsBean = gson.fromJson(stringBuilder.toString(), VideoLayoutParamsBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
