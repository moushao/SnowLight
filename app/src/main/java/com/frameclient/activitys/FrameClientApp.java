package com.frameclient.activitys;

import android.app.Application;
import android.util.Log;

import com.frameclient.utils.Constants;
import com.frameclient.utils.CrashHandler;
import com.frameclient.utils.SharedPreferencesHelper;


public class FrameClientApp extends Application {
    private static FrameClientApp myInstance;

    public static FrameClientApp getInstance() {
        return myInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myInstance = this;
        CrashHandler.getInstance().init(myInstance);
        initData();
    }

    private void initData() {
        Constants.IP_ADDRESS = SharedPreferencesHelper.getString(this, "IP_ADDRESS", Constants.IP_ADDRESS);
        Constants.BASE_URL = SharedPreferencesHelper.getString(this, "BASE_URL", Constants.BASE_URL);
        Log.e("com.", "视频服务地址：" + Constants.IP_ADDRESS + "  报警地址：" + Constants.BASE_URL);
    }
}