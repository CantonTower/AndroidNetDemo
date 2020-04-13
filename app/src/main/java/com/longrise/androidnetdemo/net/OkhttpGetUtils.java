package com.longrise.androidnetdemo.net;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.GetBean;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpGetUtils implements Handler.Callback {
    private Handler mHandler;

    private static OkhttpGetListener mListener;
    private static final String TAG = "OkhttpGetUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public OkhttpGetUtils() {
    }

    private void okhttpLoad(final String url) {
        mHandler = new Handler(this);
        // 要有客户端，就相当于我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        // 创建请求内容
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        // 用client去创建请求任务
        Call task = client.newCall(request);
        // 异步请求
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    GetBean bean = gson.fromJson(response.body().string(), GetBean.class);
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SUCCESS;
                    msg.obj = bean;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                }
            }
        });
    }

    public static void setOkhttpGetListener(String url, OkhttpGetListener listener) {
        mListener = listener;
        new OkhttpGetUtils().okhttpLoad(url);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_SUCCESS:
                GetBean bean = (GetBean) msg.obj;
                if (mListener != null) {
                    mListener.okhttpGetResult(bean);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.okhttpGetResult(null);
                }
                break;
        }
        return false;
    }

    public interface OkhttpGetListener {
        void okhttpGetResult(GetBean bean);
    }
}
