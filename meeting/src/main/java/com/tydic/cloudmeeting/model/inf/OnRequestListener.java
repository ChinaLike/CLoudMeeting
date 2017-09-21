package com.tydic.cloudmeeting.model.inf;

/**
 * 数据请求回调
 * Created by like on 2017-09-19
 */

public interface OnRequestListener {
    void onSuccess(int type, Object obj);

    void onError(int type, int code);
}
