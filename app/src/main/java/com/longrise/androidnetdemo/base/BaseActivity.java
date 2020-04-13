package com.longrise.androidnetdemo.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new ProgressDialog(this);
    }

    public void showDialog() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dissmissDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
