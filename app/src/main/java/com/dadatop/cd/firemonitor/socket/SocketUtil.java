package com.dadatop.cd.firemonitor.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dadatop.cd.firemonitor.DialActivity;
import com.dadatop.cd.firemonitor.Mp3Activity;
import com.dadatop.cd.firemonitor.MyApplication;
import com.dadatop.cd.firemonitor.RecoActivity;
import com.dadatop.cd.firemonitor.core.TimeoutmonitorTask;
import com.dadatop.cd.firemonitor.setting.PrefUtil;
import com.dadatop.cd.firemonitor.socket.data.ReadyMsg;
import com.dadatop.cd.firemonitor.socket.data.ReadyToRecMsg;
import com.dadatop.cd.firemonitor.socket.data.RecMsg;
import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.action.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

import java.nio.charset.Charset;
import java.util.Timer;

public class SocketUtil {

    private static SocketUtil socketUtil;

    private static Activity _activity;

    public static SocketUtil getInstance(){
        if(socketUtil == null){
            socketUtil = new SocketUtil();
        }
        return socketUtil;
    }


    public void set_activity(Activity activity){
        _activity = activity;
    }

    IConnectionManager mManager;
    private PulseData mPulseData = new PulseData();

    int connect_status = 0;

    public int getStatus(){
        return connect_status;
    }

    private long connectTime;
    private long disconnectTime;


    void checkTimeout(){
        Log.d("uuuuuuu","method checkTimeout() called.disconnectTime="+disconnectTime+";currentTime="+System.currentTimeMillis());
        if(connectTime<disconnectTime && System.currentTimeMillis()-disconnectTime>5000){
            backToPortal();
        }
    }

    public  void connect(){

        try{
            timer = new Timer();
            timer.schedule(new TimeoutmonitorTask(mHandler),0,1000);
        }catch (Exception e){
            e.printStackTrace();
        }

//        ConnectionInfo info = new ConnectionInfo("172.20.9.39", 8890);
        String server = PrefUtil.getInstance(_activity).getSocketServer();
        int port = PrefUtil.getInstance(_activity).getSocketPort();

        ConnectionInfo info = new ConnectionInfo(server, port);
        mManager = OkSocket.open(info);
        mManager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketIOThreadStart(Context context, String action) {
                super.onSocketIOThreadStart(context, action);
            }

            @Override
            public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
                connect_status = 0;
//                disconnectTime = System.currentTimeMillis();
//                checkTimeout();
                backToPortal();

            }

            @Override
            public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
                connect_status = 0;
//                disconnectTime = System.currentTimeMillis();
//                checkTimeout();
                backToPortal();
            }

            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                connectTime = System.currentTimeMillis();
                connect_status = 1;
                SocketUtil.getInstance().sendReady();
                OkSocket.open(info).getPulseManager().setPulseSendable(mPulseData).pulse();//Start the heartbeat.
            }

            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
                connect_status = 0;
                super.onSocketConnectionFailed(context, info, action, e);
            }

            @Override
            public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {

                String json_data = new String(data.getBodyBytes(), Charset.forName("utf-8"));
                Log.d("SocketUtil",json_data);
                if(mManager != null && !json_data.isEmpty() && json_data.contains("pulse")){//是否是心跳返回包,需要解析服务器返回的数据才可知道
                    //喂狗操作
                    mManager.getPulseManager().feed();
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":2")){
                    goToDial();
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":4")){
                    goToRec();
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":100")){
                    backToPortal();
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":5")){
                    gotoMp3(json_data);
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":6")){//开始
                    sendStart();
                }else if(!json_data.isEmpty() && json_data.contains("{\"no\":7")){//结束
                    sendEnd();
                }
                connectTime = System.currentTimeMillis();
            }

            @Override
            public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {

            }

            @Override
            public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
                super.onPulseSend(context, info, data);
            }
        });
        mManager.connect();
    }

    private void sendStart() {
        Intent intent = new Intent("com.dadatop.cd.firemonitor.operation");
        intent.putExtra("action","1");
        _activity.sendBroadcast(intent);
    }

    private void sendEnd() {
        Intent intent = new Intent("com.dadatop.cd.firemonitor.operation");
        intent.putExtra("action","2");
        _activity.sendBroadcast(intent);
    }

    private void gotoMp3(String json_data) {
        Intent intent = new Intent(_activity,Mp3Activity.class);
        intent.putExtra("mp3",json_data);
        _activity.startActivity(intent);
    }

    private void backToPortal() {
        MyApplication.clearActivities();
        timer.cancel();
    }

    private void goToDial() {
        Intent intent = new Intent(_activity,DialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        _activity.startActivity(intent);
    }

    private void goToRec() {
        Intent intent = new Intent(_activity,RecoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        _activity.startActivity(intent);

    }


    public void sendReady(){
        if(mManager!=null){
            mManager.send(new ReadyMsg());
        }
    }

    public void sendReadyToRec(){
        if(mManager!=null){
            mManager.send(new ReadyToRecMsg());
        }
    }

    public void sendRecMsg(String msg){
        if(mManager!=null){
            mManager.send(new RecMsg(msg));
        }

    }

    static Timer timer = new Timer();


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            checkTimeout();
        }
    };

}
