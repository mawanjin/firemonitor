package com.dadatop.cd.firemonitor.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {
    SharedPreferences mSharedPreferences;
    static PrefUtil prefUtil;
    Context context;

    private PrefUtil(Context context) {
        this.context = context;
        mSharedPreferences = context.getSharedPreferences("firemonitor_stat_sharedPreferences", 0);
    }

    public static PrefUtil getInstance(Context context) {
        if (prefUtil == null) {
            prefUtil = new PrefUtil(context);
        }
        return prefUtil;
    }

    public String getSocketServer() {
//        return mSharedPreferences.getString("socket_server", "172.20.9.39");
        return mSharedPreferences.getString("socket_server", "192.168.1.7");
    }

    public void setSocketServer(String listStr) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("socket_server", listStr);
        mEditor.commit();
    }

    public int getSocketPort() {

        int port = 8890;
        try {
            port = Integer.parseInt(mSharedPreferences.getString("socket_port", "8890"));
        }catch (Exception e){

        }
        if(port<1000){
            port = 8890;
        }
        return port;

    }

    public void setSocketPort(String listStr) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("socket_port", listStr);
        mEditor.commit();
    }

}
