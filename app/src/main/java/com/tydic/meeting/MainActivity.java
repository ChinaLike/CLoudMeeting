package com.tydic.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.tydic.cm.CommonActivity;
import com.tydic.cm.OnLiveActivity;
import com.tydic.cm.bean.JsParamsBean;
import com.tydic.cm.constant.Key;
import com.tydic.cm.util.CacheUtil;

public class MainActivity extends AppCompatActivity {


    private EditText userName, roomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.user_name);
        roomId = (EditText) findViewById(R.id.room_id);

//3317
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String ID_1 = "1656380ecc41483483f39bd5cd5de1d2";//张琪_阿里的
//                String ID_2 = "c254e80bae1247c499a08cb2807e447d";//洛桑旦增_阿里的
//
//                CacheUtil.get(MainActivity.this).put(Key.EMPNAME, userName.getText().toString());
//                CacheUtil.get(MainActivity.this).put(Key.PASSWORD, "");
//                CacheUtil.get(MainActivity.this).put(Key.USER_ID, "1656380ecc41483483f39bd5cd5de1d2");
//                JsParamsBean bean = new JsParamsBean();
//                //洛桑旦增_阿里   张琪_阿里
//                bean.setEmpName(userName.getText().toString());
//                bean.setPassWord("");
//                bean.setRoomId(Integer.parseInt(roomId.getText().toString()));
//                bean.setMeetingId("6ad9f56e355e46c9b345c34ec157607b");
//                bean.setFeedUserName(userName.getText().toString());
//                //c254e80bae1247c499a08cb2807e447d     1656380ecc41483483f39bd5cd5de1d2
//                bean.setFeedId("1656380ecc41483483f39bd5cd5de1d2");
//                bean.setInitiator("4ae8faff7913469688c46bca814a965e");
//                bean.setCreated_by("4ae8faff7913469688c46bca814a965e");
//                bean.setIsBroadcastMode("1");
//                Intent intent = new Intent(MainActivity.this, OnLiveActivity.class);
//                intent.putExtra(Key.JS_PARAMS, bean);
//                startActivity(intent);
//            }
//        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID_1 = "1656380ecc41483483f39bd5cd5de1d2";//张琪_阿里的
                String ID_2 = "c254e80bae1247c499a08cb2807e447d";//洛桑旦增_阿里的

                CacheUtil.get(MainActivity.this).put(Key.EMPNAME, userName.getText().toString());
                CacheUtil.get(MainActivity.this).put(Key.PASSWORD, "");
                CacheUtil.get(MainActivity.this).put(Key.USER_ID, "1656380ecc41483483f39bd5cd5de1d2");
                JsParamsBean bean = new JsParamsBean();
                //洛桑旦增_阿里   张琪_阿里
                bean.setEmpName(userName.getText().toString());
                bean.setPassWord("");
                bean.setRoomId(Integer.parseInt(roomId.getText().toString()));
                bean.setMeetingId("6ad9f56e355e46c9b345c34ec157607b");
                bean.setFeedUserName(userName.getText().toString());
                //c254e80bae1247c499a08cb2807e447d     1656380ecc41483483f39bd5cd5de1d2
                bean.setFeedId("c254e80bae1247c499a08cb2807e447d");
                bean.setInitiator("c254e80bae1247c499a08cb2807e447d");
                bean.setCreated_by("c254e80bae1247c499a08cb2807e447d");
                bean.setIsBroadcastMode("0");
                Intent intent = new Intent(MainActivity.this, CommonActivity.class);
                intent.putExtra(Key.JS_PARAMS, bean);
                startActivity(intent);
            }
        });

    }
}
