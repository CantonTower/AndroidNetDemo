package com.longrise.androidnetdemo.net;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.PostReceiveBean;
import com.longrise.androidnetdemo.bean.PostSendBean;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpPostUtils implements Handler.Callback {
    private Handler mHandler;

    private static OkhttpPostListener mListener;
    private static final String TAG = "OkhttpPostUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public OkhttpPostUtils() {
    }

    private void okhttpLoad(String url, PostSendBean bean) {
        mHandler = new Handler(this);
        // 要有客户端，就相当于我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        // 创建请求内容
        Gson gson = new Gson();
        String jsonStr = gson.toJson(bean);
        MediaType type = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonStr, type);
        final Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        // 用client去创建请求任务
        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    PostReceiveBean bean = gson.fromJson(response.body().string(), PostReceiveBean.class);
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_SUCCESS:
                PostReceiveBean bean = (PostReceiveBean) msg.obj;
                if (mListener != null) {
                    mListener.okhttpPostResult(bean);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.okhttpPostResult(null);
                }
                break;
        }
        return false;
    }

    public static void setOkhttpPostListener(String url, PostSendBean bean, OkhttpPostListener listener) {
        mListener = listener;
        new OkhttpPostUtils().okhttpLoad(url, bean);
    }

    public interface OkhttpPostListener {
        void okhttpPostResult(PostReceiveBean bean);
    }
}
