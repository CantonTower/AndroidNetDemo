package com.longrise.androidnetdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.longrise.androidnetdemo.activity.HttpUrlConnectionActivity;
import com.longrise.androidnetdemo.activity.OkHttpActivity;
import com.longrise.androidnetdemo.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Button mbtnUrlConn;
    private Button mbtnOkhttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mbtnUrlConn = findViewById(R.id.btn_conn);
        mbtnOkhttp = findViewById(R.id.btn_okhttp);
        mbtnUrlConn.setOnClickListener(this);
        mbtnOkhttp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mbtnUrlConn) {
            startActivity(new Intent(this, HttpUrlConnectionActivity.class));
        } else if (v == mbtnOkhttp) {
            startActivity(new Intent(this, OkHttpActivity.class));
        }
    }
}
