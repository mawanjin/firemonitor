package com.dadatop.cd.firemonitor;

import android.app.Activity;
import android.app.Application;

import com.xuhao.android.libsocket.sdk.OkSocket;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //The primary process needs to be distinguished at the beginning of the primary process.
        OkSocket.initialize(this,true);
        //If you need to open the Socket debug log, configure the following
        //OkSocket.initialize(this,true);
    }

    public static void addActivity(Activity a){
        activities.add(a);
    }

    private static List<Activity> activities = new ArrayList();

    public static void clearActivities(){
        for(Activity a: activities){
            a.finish();
        }
        activities.clear();
    }
}
