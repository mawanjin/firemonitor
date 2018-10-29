package com.dadatop.cd.firemonitor.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dadatop.cd.firemonitor.DialActivity;
import com.dadatop.cd.firemonitor.MainActivity;
import com.dadatop.cd.firemonitor.MyApplication;
import com.dadatop.cd.firemonitor.RecoActivity;
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
    public  void connect(){

        ConnectionInfo info = new ConnectionInfo("172.20.9.39", 8890);
        mManager = OkSocket.open(info);
        mManager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketIOThreadStart(Context context, String action) {
                super.onSocketIOThreadStart(context, action);
            }

            @Override
            public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
                super.onSocketIOThreadShutdown(context, action, e);
            }

            @Override
            public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(context, info, action, e);
            }

            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                SocketUtil.getInstance().sendReady();
                OkSocket.open(info).getPulseManager().setPulseSendable(mPulseData).pulse();//Start the heartbeat.
            }

            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
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
                }
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

    private void backToPortal() {
        MyApplication.clearActivities();
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


}