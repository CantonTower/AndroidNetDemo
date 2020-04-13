package com.longrise.androidnetdemo.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnImgUtils implements Handler.Callback {
    private Context mContext;
    private Handler mHandler;

    private static HttpUrlConnImgListener mListener;
    private static final String TAG = "HttpURLConnImgUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public HttpURLConnImgUtils(Context mContext) {
        this.mContext = mContext;
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
                    conn.connect(); // 开始连接服务端，并获取数据
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Message msg = new Message();
                        msg.what = DOWNLOAD_SUCCESS;
                        msg.obj = bitmap;
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
                Bitmap bitmap = (Bitmap) msg.obj;
                if (mListener != null) {
                    mListener.httpUrlConnImgResult(bitmap);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.httpUrlConnImgResult(null);
                }
                break;
        }
        return false;
    }

    public static void setHttpUrlConnImgListener(Context context, String url, HttpUrlConnImgListener listener) {
        mListener = listener;
        new HttpURLConnImgUtils(context).httpUrlConn(url);
    }

    public interface HttpUrlConnImgListener {
        void httpUrlConnImgResult(Bitmap bitmap);
    }
}
