package com.dadatop.cd.firemonitor.core;

import android.os.Handler;

import java.util.TimerTask;

public class TimeoutmonitorTask extends TimerTask {

    private Handler handler;

    public TimeoutmonitorTask(Handler _handler){
        handler = _handler;
    }

    @Override
    public void run() {
        if(handler!=null) handler.sendEmptyMessage(0);
    }


}
