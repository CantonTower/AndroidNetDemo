package com.longrise.androidnetdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.longrise.androidnetdemo.R;
import com.longrise.androidnetdemo.base.BaseActivity;
import com.longrise.androidnetdemo.base.BaseBean;
import com.longrise.androidnetdemo.bean.FileBean;
import com.longrise.androidnetdemo.bean.GetBean;
import com.longrise.androidnetdemo.bean.PostReceiveBean;
import com.longrise.androidnetdemo.bean.PostSendBean;
import com.longrise.androidnetdemo.net.OkhttpFileUtils;
import com.longrise.androidnetdemo.net.OkhttpGetUtils;
import com.longrise.androidnetdemo.net.OkhttpPostUtils;

public class OkHttpActivity extends BaseActivity implements View.OnClickListener,
        OkhttpGetUtils.OkhttpGetListener, OkhttpPostUtils.OkhttpPostListener,
        OkhttpFileUtils.OkhttpFileListener {
    private Button mbtnOkhttpGet;
    private Button mbtnOkhttpPost;
    private Button mbtnOkhttpFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);
        initView();
    }

    private void initView() {
        mbtnOkhttpGet = findViewById(R.id.btn_okhttp_get);
        mbtnOkhttpPost = findViewById(R.id.btn_okhttp_post);
        mbtnOkhttpFile = findViewById(R.id.btn_okhttp_file);
        mbtnOkhttpGet.setOnClickListener(this);
        mbtnOkhttpPost.setOnClickListener(this);
        mbtnOkhttpFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnOkhttpGet) {
            String url = BaseBean.BaseUrl + ":9102/get/text";
            showDialog();
            // 回调至okhttpGetResult
            OkhttpGetUtils.setOkhttpGetListener(url, this);
        } else if (v == mbtnOkhttpPost) {
            PostSendBean bean = new PostSendBean("美股熔断", "本月美股已熔断四次");
            String url = BaseBean.BaseUrl + ":9102/post/comment";
            // 回调至okhttpPostResult
            OkhttpPostUtils.setOkhttpPostListener(url, bean, this);
        } else if (v == mbtnOkhttpFile) {
            String url = BaseBean.BaseUrl + ":9102/file/upload";
            showDialog();
            // 回调至okhttpFileResult
            OkhttpFileUtils.setOkhttpFileListener(url, this);
        }
    }

    @Override
    public void okhttpGetResult(GetBean bean) {
        dissmissDialog();
        if (bean != null) {
            Toast.makeText(this, bean.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void okhttpPostResult(PostReceiveBean bean) {
        if (bean != null) {
            Toast.makeText(this, bean.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void okhttpFileResult(FileBean bean) {
        dissmissDialog();
        if (bean != null) {
            Toast.makeText(this, bean + "", Toast.LENGTH_SHORT).show();
        }
    }
}
