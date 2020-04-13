package com.longrise.androidnetdemo.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.longrise.androidnetdemo.bean.PostReceiveBean;
import com.longrise.androidnetdemo.bean.PostSendBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class HttpURLConnPostUtils implements Handler.Callback {
    private Handler mHandler;

    private static HttpURLConnPostListener mListener;
    private static final String TAG = "HttpURLConnPostUtils";
    private static final int LOAD_SUCCESS = 100;
    private static final int LOAD_FALSE = 101;

    public HttpURLConnPostUtils() {
    }

    private void httpUrlConn(final String url, final PostSendBean sendBean) {
        mHandler = new Handler(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                OutputStream os = null;
                InputStream is = null;
                try {
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(10000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    Gson json = new Gson();
                    String jsonStr = json.toJson(sendBean);
                    byte[] bytes = jsonStr.getBytes("utf-8");
                    conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                    // 连接
                    conn.connect();
                    // 把数据从客户端通过输出流发送给服务器
                    os = conn.getOutputStream();
                    os.write(bytes);
                    os.flush();

                    // 通过输入流拿结果
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line = br.readLine();
                        StringBuilder result = new StringBuilder();
                        while (line != null) {
                            result.append(line);
                            line = br.readLine();
                        }
                        Gson gson = new Gson();
                        PostReceiveBean bean = gson.fromJson(result.toString(), PostReceiveBean.class);
                        Message msg = new Message();
                        msg.what = LOAD_SUCCESS;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(LOAD_FALSE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "httpUrlConn.Exception=" + e.toString());
                    mHandler.sendEmptyMessage(LOAD_FALSE);
                }finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                            Log.e(TAG, "httpUrlConn.Exception=" + e.toString());
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {
                            Log.e(TAG, "httpUrlConn.Exception=" + e.toString());
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LOAD_SUCCESS:
                if (mListener != null) {
                    PostReceiveBean bean = (PostReceiveBean) msg.obj;
                    mListener.httpUrlConnPostResult(bean);
                }
                break;
            case LOAD_FALSE:
                if (mListener != null) {
                    mListener.httpUrlConnPostResult(null);
                }
                break;
        }
        return false;
    }

    public static void setHttpURLConnPostListener(String url, PostSendBean sendBean, HttpURLConnPostListener listener) {
        mListener = listener;
        new HttpURLConnPostUtils().httpUrlConn(url, sendBean);
    }

    public interface HttpURLConnPostListener {
        void httpUrlConnPostResult(PostReceiveBean receiveBean);
    }
}
