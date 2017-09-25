package com.tydic.cm.bean;

import java.io.Serializable;

/**
 * 基类
 * Created by like on 2017-09-19
 */

public class BaseBean<T> implements Serializable {

    private String errorCode;
    private String errorMessage;
    private T data;

    public String getErrorCode() {
        return errorMessage;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                '}';
    }
}
