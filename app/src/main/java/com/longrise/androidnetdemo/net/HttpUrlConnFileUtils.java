package com.longrise.androidnetdemo.net;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.FileBean;
import com.longrise.androidnetdemo.bean.GetBean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpUrlConnFileUtils implements Handler.Callback {
    private Handler mHandler;

    private static HttpUrlConnFileListener mListener;
    private static final String TAG = "HttpURLConnGetUtils";
    private static final int DOWNLOAD_SUCCESS = 100;
    private static final int DOWNLOAD_FALSE = 101;

    public HttpUrlConnFileUtils() {
    }

    private void httpUrlConn(final String url) {
        mHandler = new Handler(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                OutputStream os = null;
                BufferedInputStream bis = null;
                InputStream in = null;
                try {
                    File file = new File("/storage/emulated/0/Download/1450250/testFile.png");
                    String fileKey = "file";
                    String fileName = file.getName();
                    String fileType = "image/jpeg";
                    String BOUNDARY = "--------------------------954555323792164398227139";
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(10000);
                    conn.setRequestProperty("User-Agent", "Android/" + Build.VERSION.SDK_INT);
                    conn.setRequestProperty("Accept", "*/*");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    // 连接
                    conn.connect();
                    os = conn.getOutputStream();
                    // 准备数据
                    StringBuilder sb = new StringBuilder();
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + fileName + "\"");
                    sb.append("\r\n");
                    sb.append("Content-Type: " + fileType);
                    sb.append("\r\n");
                    sb.append("\r\n");
                    byte[] bytes = sb.toString().getBytes("UTF-8");
                    os.write(bytes);
                    // 文件内容
                    FileInputStream is = new FileInputStream(file);
                    bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    // 写尾部信息
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append("\r\n");
                    sb1.append("--");
                    sb1.append(BOUNDARY);
                    sb1.append("--");
                    sb1.append("\r\n");
                    sb1.append("\r\n");
                    os.write(sb1.toString().getBytes("UTF-8"));
                    os.flush();
                    // 获取返回的结果
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        in = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String result = br.readLine();
                        Gson gson = new Gson();
                        FileBean bean = gson.fromJson(result, FileBean.class);
                        if (result != null) {
                            Message msg = new Message();
                            msg.what = DOWNLOAD_SUCCESS;
                            msg.obj = bean;
                            mHandler.sendMessage(msg);
                        } else {
                            mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                        }
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FALSE);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {

                        }
                    }
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (Exception e) {

                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {

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
                FileBean bean = (FileBean) msg.obj;
                if (mListener != null) {
                    mListener.httpUrlConnFileResult(bean);
                }
                break;
            case DOWNLOAD_FALSE:
                if (mListener != null) {
                    mListener.httpUrlConnFileResult(null);
                }
                break;
        }
        return false;
    }

    public static void setHttpUrlConnFileListener(String url, HttpUrlConnFileListener listener) {
        mListener = listener;
        new HttpUrlConnFileUtils().httpUrlConn(url);
    }

    public interface HttpUrlConnFileListener {
        void httpUrlConnFileResult(FileBean bean);
    }
}
