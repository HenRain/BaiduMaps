package com.example.administrator.baidumaps;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BaiduApplication  extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());

    }
}
