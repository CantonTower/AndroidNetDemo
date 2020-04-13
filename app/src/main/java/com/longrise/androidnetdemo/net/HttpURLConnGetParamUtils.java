package com.longrise.androidnetdemo.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.GetBean;
import com.longrise.androidnetdemo.bean.GetParamBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnGetParamUtils implements Handler.Callback {
    private Handler mHandler;

    private static HttpUrlConnGetParamListener mListener;
    private static final String TAG = "HttpURLConnGetUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public HttpURLConnGetParamUtils() {
    }

    private void httpUrlConn(final String url) {
        mHandler = new Handler(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(10000); // 设置连接超时时间
                    conn.setRequestMethod("GET"); // get请求
                    conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9"); // 设置请求返回数据的语言是中文
                    conn.connect(); // 开始连接服务端，并获取数据
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line = br.readLine();
                        StringBuilder result = new StringBuilder();
                        while (line != null) {
                            result.append(line);
                            line = br.readLine();
                        }
                        Gson gson = new Gson();
                        GetParamBean bean = gson.fromJson(result.toString(), GetParamBean.class);
                        Message msg = new Message();
                        msg.what = DOWNLOAD_SUCCESS;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception=" + e.toString());
                    mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                }
            }
        }.start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_SUCCESS:
                GetParamBean bean = (GetParamBean) msg.obj;
                if (mListener != null) {
                    mListener.httpUrlConnGetParamResult(bean);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.httpUrlConnGetParamResult(null);
                }
                break;
        }
        return false;
    }

    public static void setHttpUrlConnGetParamListener(String url, HttpUrlConnGetParamListener listener) {
        mListener = listener;
        new HttpURLConnGetParamUtils().httpUrlConn(url);
    }

    public interface HttpUrlConnGetParamListener {
        void httpUrlConnGetParamResult(GetParamBean bean);
    }
}
