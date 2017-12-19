package com.tydic.cm.helper;

import android.os.Handler;
import android.os.Message;

import com.tydic.cm.bean.UsersBean;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by li on 2017/11/30.
 */

public abstract class TimerHandler extends Handler {
    /**
     * 默认轮播时间
     */
    public static final long TIME = 15 * 1000;

    private Timer mTimer = new Timer();

    private int page = 0;

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            sendEmptyMessage(0x345);
        }
    };

    private int mode;

    private List<UsersBean> mList;

    public TimerHandler(int mode, List<UsersBean> mList) {
        this.mode = mode;
        this.mList = mList;
    }

    public abstract void handleBanner(int page);

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 0x345) {
            page++;
            if (page == handlePage()) {
                page = 0;
            }
            handleBanner(page);
        }
        super.handleMessage(msg);
    }

    /**
     * 总共需要显示的界面
     *
     * @return
     */
    private int handlePage() {
        int size = mList.size();
        int page = 0;
        if (size % mode == 0) {
            page = size / mode;
        } else {
            page = size / mode + 1;
        }
        return page;
    }

    /**
     * 获取滚动的下标
     * @param page
     * @return
     */
    public int getIndex(int page){
        if (mode == 1){
            //一页只显示一个
            return page;
        }else {
            //一页显示多个
            if (page == 0){
                //特殊处理第一页，显示第一个
                return page;
            }else {
                return page*mode + mode/2;
            }
        }
    }

    /**
     * 开始轮播
     *
     * @param time
     */
    public void start(long time) {
        mTimer.schedule(mTimerTask, time, time);
    }

    /**
     * 开始轮播
     */
    public void start() {
        start(TIME);
    }

    public void stop() {
        mTimer.cancel();
    }

}
