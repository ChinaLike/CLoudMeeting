package com.tydic.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tydic.cloudmeeting.CommonActivity;
import com.tydic.cloudmeeting.OnLiveActivity;
import com.tydic.cloudmeeting.bean.JsParamsBean;
import com.tydic.cloudmeeting.constant.Key;
import com.tydic.cloudmeeting.util.CacheUtil;

public class MainActivity extends AppCompatActivity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.get(MainActivity.this).put(Key.EMPNAME, "张琪_阿里");
                CacheUtil.get(MainActivity.this).put(Key.PASSWORD, "");
                CacheUtil.get(MainActivity.this).put(Key.USER_ID, "1656380ecc41483483f39bd5cd5de1d2");
                JsParamsBean bean = new JsParamsBean();
                bean.setEmpName("张琪_阿里");
                bean.setPassWord("");
                bean.setRoomId(3276);
                bean.setMeetingId("e52fe211fbb64accad4c6abccddbc91e");
                bean.setFeedUserName("张琪_阿里");
                bean.setFeedId("1656380ecc41483483f39bd5cd5de1d2");
                bean.setInitiator("c254e80bae1247c499a08cb2807e447d");
                bean.setCreated_by("c254e80bae1247c499a08cb2807e447d");
                bean.setIsBroadcastMode("0");
                Intent intent = new Intent(MainActivity.this, OnLiveActivity.class);
                intent.putExtra(Key.JS_PARAMS, bean);
                startActivity(intent);
            }
        });

    }
}
