package com.longrise.androidnetdemo.net;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.FileBean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpFileUtils implements Handler.Callback {
    private Handler mHandler;

    private static OkhttpFileListener mListener;
    private static final String TAG = "OkhttpFileUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public OkhttpFileUtils() {
    }

    private void okhttpLoad(String url) {
        mHandler = new Handler(this);
        // 要有客户端，就相当于我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        File file = new File("/storage/emulated/0/Download/1450250/testFile.png");
        MediaType fileType = MediaType.parse("image/png");
        RequestBody fileBody = RequestBody.create(file, fileType);
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
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
                    FileBean bean = gson.fromJson(response.body().string(), FileBean.class);
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
                FileBean bean = (FileBean) msg.obj;
                if (mListener != null) {
                    mListener.okhttpFileResult(bean);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.okhttpFileResult(null);
                }
                break;
        }
        return false;
    }

    public static void setOkhttpFileListener(String url, OkhttpFileListener listener) {
        mListener = listener;
        new OkhttpFileUtils().okhttpLoad(url);
    }

    public interface OkhttpFileListener {
        void okhttpFileResult(FileBean bean);
    }
}
