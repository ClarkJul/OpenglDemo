package com.clark.gldemo;

import android.app.Application;

/**
 * @author Clark
 * 2022/1/22 22:00
 */
public class OpenglApp extends Application {

    private static Application app;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }

    public static Application getApp() {
        return app;
    }
}
