package com.academy.ndvalkov.mediamonitoringapp;

import android.app.Application;

import com.orm.SugarContext;

public class MediaMonitoringApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }
}
