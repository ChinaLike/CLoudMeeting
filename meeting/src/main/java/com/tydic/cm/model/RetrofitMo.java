package com.tydic.cm.model;

import android.content.Context;
import android.text.TextUtils;

import com.tydic.cm.bean.BaseBean;
import com.tydic.cm.bean.UsersBean;
import com.tydic.cm.config.Config;
import com.tydic.cm.constant.Key;
import com.tydic.cm.model.inf.OnRequestListener;
import com.tydic.cm.service.RetrofitService;
import com.tydic.cm.util.T;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 数据请求
 * Created by like on 2017-09-19
 */

public class RetrofitMo {

    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;

    private Context mContext;

    public RetrofitMo(Context mContext) {
        this.mContext = mContext;
    }

    private RetrofitService retrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL_ANYCHAT)
                .addConverterFactory(GsonConverterFactory.create())
                //  .client(getOkHttpClient(mContext))
                .build();
        return retrofit.create(RetrofitService.class);
    }

    /**
     * other
     *
     * @param context
     * @return
     */
    public static OkHttpClient getOkHttpClient(Context context) {
        String token;

        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(chain -> {
//                    Request request = chain.request()
//                            .newBuilder()
//                            .addHeader("Content-Type", "application/json;charset=utf-8")
////                            .addHeader("Authorization", token)
//                            .build();
//
//                    return chain.proceed(request);
//                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json;charset=utf-8")
//                            .addHeader("Authorization", token)
                                .build();
                        return chain.proceed(request);
                    }
                })
//                .addNetworkInterceptor(new ReceivedCookiesInterceptor(context))
//                .addNetworkInterceptor(new AddCookiesInterceptor(context))
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                //.addInterceptor(new AddCookiesInterceptor(context))
                .build();
        return httpClient;
    }

    /**
     * 获取在线人员列表
     *
     * @param roomId 房间ID
     */
    public void onLineUsers(int roomId, final OnRequestListener listener) {
        Call<BaseBean<List<UsersBean>>> call = retrofit().getOnlinePeoPle(roomId + "");
        call.enqueue(new Callback<BaseBean<List<UsersBean>>>() {
            @Override
            public void onResponse(Call<BaseBean<List<UsersBean>>> call, Response<BaseBean<List<UsersBean>>> response) {
                BaseBean<List<UsersBean>> baseBean = response.body();
                if (listener != null) {
                    if (baseBean != null && baseBean.getData() != null) {
                        //返回数据成功
                        listener.onSuccess(Key.ON_LINE_USER, baseBean.getData());
                    } else {
                        //返回数据失败
                        listener.onError(Key.ON_LINE_USER, Key.FAIL);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseBean<List<UsersBean>>> call, Throwable t) {
                if (listener != null) {
                    listener.onError(Key.ON_LINE_USER, Key.OVER_TIME);
                }
            }
        });
    }

    /**
     * 获取用户状态
     *
     * @param roomId   房间id
     * @param feedId
     * @param listener
     */
//    public void userState(int roomId, String feedId, final OnRequestListener listener) {
//        Call<BaseBean<UsersBean>> call = retrofit().getUserState(roomId + "", feedId);
//        call.enqueue(new Callback<BaseBean<UsersBean>>() {
//            @Override
//            public void onResponse(Call<BaseBean<UsersBean>> call, Response<BaseBean<UsersBean>> response) {
//                BaseBean<UsersBean> baseBean = response.body();
//                if (listener != null) {
//                    if (baseBean != null && baseBean.getData() != null) {
//                        listener.onSuccess(Key.USER_STATE, baseBean.getData());
//                    } else {
//                        listener.onError(Key.USER_STATE, Key.FAIL);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseBean<UsersBean>> call, Throwable t) {
//                if (listener != null) {
//                    listener.onError(Key.USER_STATE, Key.OVER_TIME);
//                }
//            }
//        });
//
//    }

    /**
     * 向服务器发送消息
     *
     * @param userId
     * @param character
     * @param listener
     */
    public void sendMsg(String userId, String character, final UsersBean usersBean, final int type, final OnRequestListener listener) {
        Call<BaseBean<String>> call = retrofit().sendMsg(userId, character);
        call.enqueue(new Callback<BaseBean<String>>() {
            @Override
            public void onResponse(Call<BaseBean<String>> call, Response<BaseBean<String>> response) {
                BaseBean<String> baseBean = response.body();
                if (!TextUtils.isEmpty(baseBean.getData())) {
                    if (listener != null) {
                        listener.onSuccess(type, usersBean);
                    }
                } else {
                    T.showShort(baseBean.getData());
                    if (listener != null) {
                        listener.onError(type, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseBean<String>> call, Throwable t) {
                T.showShort("连接超时");
                if (listener != null) {
                    listener.onError(type, Key.OVER_TIME);
                }
            }
        });
    }

}
