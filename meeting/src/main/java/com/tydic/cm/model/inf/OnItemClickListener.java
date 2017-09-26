package com.tydic.cm.model.inf;

import com.tydic.cm.bean.UsersBean;

/**
 * 在线用户列表Item被点击
 * Created by like on 2017-09-25
 */

public interface OnItemClickListener {
    void onItemClick(int position , UsersBean bean);
}
