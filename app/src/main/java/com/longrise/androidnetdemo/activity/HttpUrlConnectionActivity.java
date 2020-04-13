package com.longrise.androidnetdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.longrise.androidnetdemo.R;
import com.longrise.androidnetdemo.base.BaseActivity;
import com.longrise.androidnetdemo.base.BaseBean;
import com.longrise.androidnetdemo.bean.FileBean;
import com.longrise.androidnetdemo.bean.GetBean;
import com.longrise.androidnetdemo.bean.GetParamBean;
import com.longrise.androidnetdemo.bean.PostReceiveBean;
import com.longrise.androidnetdemo.bean.PostSendBean;
import com.longrise.androidnetdemo.net.HttpURLConnGetParamUtils;
import com.longrise.androidnetdemo.net.HttpURLConnGetUtils;
import com.longrise.androidnetdemo.net.HttpURLConnImgUtils;
import com.longrise.androidnetdemo.net.HttpURLConnPostUtils;
import com.longrise.androidnetdemo.net.HttpUrlConnDownFileUtils;
import com.longrise.androidnetdemo.net.HttpUrlConnFileUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUrlConnectionActivity extends BaseActivity implements View.OnClickListener,
        HttpURLConnGetUtils.HttpUrlConnGetListener, HttpURLConnImgUtils.HttpUrlConnImgListener,
        HttpURLConnPostUtils.HttpURLConnPostListener, HttpURLConnGetParamUtils.HttpUrlConnGetParamListener,
        HttpUrlConnFileUtils.HttpUrlConnFileListener, HttpUrlConnDownFileUtils.HttpUrlConnDownFileListener {
    private static final String TAG = "HttpUrlConnectionActivity";
    private Button mbtnConnGet;
    private Button mbtnConnImg;
    private Button mbtnConnPost;
    private Button mbtnConnParams;
    private Button mbtnConnFile;
    private Button mbtnConnDownFile;
    private ImageView mivConnImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_url_connection);
        initView();
    }

    private void initView() {
        mbtnConnGet = findViewById(R.id.btn_conn_get);
        mbtnConnImg = findViewById(R.id.btn_conn_img);
        mivConnImg = findViewById(R.id.iv_conn_img);
        mbtnConnPost = findViewById(R.id.btn_conn_post);
        mbtnConnParams = findViewById(R.id.btn_conn_params);
        mbtnConnFile = findViewById(R.id.btn_conn_file);
        mbtnConnDownFile = findViewById(R.id.btn_conn_down_file);
        mbtnConnGet.setOnClickListener(this);
        mbtnConnImg.setOnClickListener(this);
        mbtnConnPost.setOnClickListener(this);
        mbtnConnParams.setOnClickListener(this);
        mbtnConnFile.setOnClickListener(this);
        mbtnConnDownFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnConnGet) {
            showDialog();
            // 回调至httpUrlGetConnResult
            HttpURLConnGetUtils.setHttpUrlConnGetListener(this, BaseBean.BaseUrl + ":9102/get/text", this);
        } else if (v == mbtnConnImg) {
            showDialog();
            // 回调至httpUrlConnImgResult
            HttpURLConnImgUtils.setHttpUrlConnImgListener(this, BaseBean.BaseUrl + ":9102/imgs/1.png", this);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_size);
//            mivConnImg.setImageBitmap(bitmap);
        } else if (v == mbtnConnPost) {
            PostSendBean bean = new PostSendBean("美股熔断", "本月美股已熔断四次");
            showDialog();
            // 回调至httpUrlConnPostResult
            HttpURLConnPostUtils.setHttpURLConnPostListener(BaseBean.BaseUrl + ":9102/post/comment", bean, this);
        } else if (v == mbtnConnParams) {
            String url = createUrl();
            showDialog();
            // 回调至httpUrlConnGetParamResult
            HttpURLConnGetParamUtils.setHttpUrlConnGetParamListener(url, this);
        } else if (v == mbtnConnFile) {
            String url = BaseBean.BaseUrl + ":9102/file/upload";
            showDialog();
            // 回调至httpUrlConnFileResult
            HttpUrlConnFileUtils.setHttpUrlConnFileListener(url, this);
        } else if (v == mbtnConnDownFile) {
            String url = BaseBean.BaseUrl + ":9102/download/10";
            showDialog();
            // 回调至HttpUrlConnDownFileResult
            HttpUrlConnDownFileUtils.setHttpUrlConnDownFileListener(this, url, this);
        }
    }

    private String createUrl() {
        String url;
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "keyword");
        params.put("page", "3");
        params.put("order", "0");
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            sb.append(next.getKey());
            sb.append("=");
            sb.append(next.getValue());
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        url = BaseBean.BaseUrl + ":9102/get/param" + sb.toString();
        return url;
    }

    @Override
    public void httpUrlConnGetResult(GetBean data) {
        dissmissDialog();
        if (data != null) {
            Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void httpUrlConnImgResult(Bitmap bitmap) {
        dissmissDialog();
        mivConnImg.setImageBitmap(bitmap);
    }

    @Override
    public void httpUrlConnPostResult(PostReceiveBean receiveBean) {
        dissmissDialog();
        if (receiveBean != null) {
            Toast.makeText(this, receiveBean.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void httpUrlConnGetParamResult(GetParamBean bean) {
        dissmissDialog();
        if (bean != null) {
            Toast.makeText(this, bean.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void httpUrlConnFileResult(FileBean bean) {
        dissmissDialog();
        if (bean != null) {
            Toast.makeText(this, bean.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void HttpUrlConnDownFileResult(boolean isSuccess) {
        dissmissDialog();
        if (isSuccess) {
            Toast.makeText(this, "下载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
        } 
    }
}
