package com.tydic.cm.bean;

/**
 * EventBus切换位置发布实体
 * Created by like on 2017-10-06
 */

public class LocationEventBus {

    private int oldPos;
    private int newPos;

    public LocationEventBus(int oldPos, int newPos) {
        this.oldPos = oldPos;
        this.newPos = newPos;
    }

    public int getOldPos() {
        return oldPos;
    }

    public int getNewPos() {
        return newPos;
    }
}
