package com.example.teambhoj;

import android.app.Application;
import android.content.SharedPreferences;

public class TeamBhojApplication extends Application{
    private static Application application;
    private static final String SHARED_PREFERENCE_NAME = "ETRACK_PREFERENCES";

    public static Application getApp(){
        return application;
    }

    public SharedPreferences getSharePreferences(){
        return application.getSharedPreferences(SHARED_PREFERENCE_NAME,0);
    }

    public void onCreate(){
        super.onCreate();
        application = this;
    }
}
