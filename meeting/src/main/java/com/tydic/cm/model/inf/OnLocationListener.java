package com.tydic.cm.model.inf;

import com.tydic.cm.bean.UsersBean;

/**
 * 位置切换监听
 * Created by like on 2017-09-25
 */

public interface OnLocationListener {
    void loacation(int oldPos, int newPos , UsersBean bean);
}
