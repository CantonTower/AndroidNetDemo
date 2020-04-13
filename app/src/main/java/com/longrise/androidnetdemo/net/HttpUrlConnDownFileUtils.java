package com.longrise.androidnetdemo.net;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpUrlConnDownFileUtils implements Handler.Callback {
    private Context mContext;
    private Handler mHandler;

    private static HttpUrlConnDownFileListener mListener;
    private static final String TAG = "HttpUrlConnDownFileUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public HttpUrlConnDownFileUtils(Context context) {
        mContext = context;
    }

    private void httpUrlConn(final String url) {
        mHandler = new Handler(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                FileOutputStream os = null;
                InputStream is = null;
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9"); // 设置请求返回数据的语言是中文
                    conn.setRequestProperty("Accept", "*/*");
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String headerField = conn.getHeaderField("Content-disposition");
                        int index = headerField.indexOf("filename=");
                        String filename = headerField.substring(index + "filename=".length());
                        File picPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if (!picPath.exists()) {
                            picPath.mkdirs();
                        }
                        File file = new File(picPath + File.separator + filename);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        os = new FileOutputStream(file);
                        is = conn.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        os.flush();
                        mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                }finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_SUCCESS:
                if (mListener != null) {
                    mListener.HttpUrlConnDownFileResult(true);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.HttpUrlConnDownFileResult(false);
                }
                break;
        }
        return false;
    }

    public static void setHttpUrlConnDownFileListener(Context context, String url, HttpUrlConnDownFileListener listener) {
        mListener = listener;
        new HttpUrlConnDownFileUtils(context).httpUrlConn(url);
    }

    public interface HttpUrlConnDownFileListener {
        void HttpUrlConnDownFileResult(boolean isSuccess);
    }
}
