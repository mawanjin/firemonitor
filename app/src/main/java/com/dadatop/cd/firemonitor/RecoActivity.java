package com.dadatop.cd.firemonitor;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dadatop.cd.firemonitor.core.AutoCheck;
import com.dadatop.cd.firemonitor.core.recog.MyRecognizer;
import com.dadatop.cd.firemonitor.core.recog.listener.IRecogListener;
import com.dadatop.cd.firemonitor.core.recog.listener.MessageStatusRecogListener;
import com.dadatop.cd.firemonitor.params.OfflineRecogParams;
import com.dadatop.cd.firemonitor.socket.SocketUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RecoActivity extends Activity {

    protected Handler handler;
    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /*
     * 本Activity中是否需要调用离线命令词功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
     */
    protected boolean enableOffline = true;

    ImageView btnSpeak,countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);
        MyApplication.addActivity(this);
        btnSpeak = findViewById(R.id.btnSpeak);
        countDown = findViewById(R.id.countDown);
        handler = new Handler() {

            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        initAction();
        initReco();

        IntentFilter filter = new IntentFilter("com.dadatop.cd.firemonitor.operation");
        try {
            registerReceiver(receiver,filter);
        }catch (Exception e){

        }
    }
    private String TAG = "RecoActivity";

    protected void handleMsg(Message msg) {
        if (msg.obj != null) {
            String rs = msg.obj.toString();
            Log.d(TAG,"original="+rs);

//            txtLog.append(msg.obj.toString() + "\n");
            if(rs.contains("{\"sn\":\"\",\"error\":9,\"desc\":\"No recorder permission\",\"sub_error\":9001}")){
                initPermission();
            }else if(rs.contains("错误码：10 ,10005")){
                Toast.makeText(this,"请联网下载授权文件",Toast.LENGTH_LONG).show();
            }else if(rs.contains("results_recognition")){
                int index = rs.indexOf("@");
                rs = rs.substring(index+1,rs.indexOf("@",index+1));
                SocketUtil.getInstance().sendRecMsg(rs);
            }

            Log.d(TAG,rs);

        }
    }

    private void initReco() {
        // DEMO集成步骤 1.1 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
        IRecogListener listener = new MessageStatusRecogListener(handler);
        // DEMO集成步骤 1.2 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例
        myRecognizer = new MyRecognizer(this, listener);
        // 集成步骤 1.3（可选）加载离线资源。offlineParams是固定值，复制到您的代码里即可
        Map<String, Object> offlineParams = OfflineRecogParams.fetchOfflineParams();
        myRecognizer.loadOfflineEngine(offlineParams);
    }


    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    protected void start() {

        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。

//        final Map<String, Object> params = fetchParams();
        final Map<String, Object> params = new HashMap<String, Object>(0);
        params.put("decoder","2");

        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印

        // 复制此段可以自动检测常规错误
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
//                        txtLog.append(message + "\n");
                        ; // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        }, enableOffline)).checkAsr(params);

        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        params.put("vad.endpoint-timeout","3000");
        myRecognizer.start(params);
    }

    boolean pressed = false;
    private int count = 1;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!pressed)return;
            switch (msg.what){
                case 1:
                    countDown.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0004));
                    break;
                case 3:
                    countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0003));
                    break;
                case 4:
                    countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0002));
                    break;
                case 5:
                    countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0001));
                    break;
                case 6:
                    countDown.setVisibility(View.INVISIBLE);
                    countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0005));
                    stop();
                    btnSpeak.setImageDrawable(getResources().getDrawable(R.drawable.mic_1));
                    break;
            }
        }
    };

    /**
     * 开始录音后，手动点击“停止”按钮。
     * SDK会识别不会再识别停止后的录音。
     */
    protected void stop() {
        // DEMO集成步骤4 (可选） 停止录音
        myRecognizer.stop();
    }

    /**
     * 开始录音后，手动点击“取消”按钮。
     * SDK会取消本次识别，回到原始状态。
     */
    protected void cancel() {
        // DEMO集成步骤5 (可选） 取消本次识别
        myRecognizer.cancel();
    }

    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        // DEMO集成步骤3 释放资源
        // 如果之前调用过myRecognizer.loadOfflineEngine()， release()里会自动调用释放离线资源
        myRecognizer.release();
        super.onDestroy();

        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    SpeakOntouchListener speakOntouchListener = new SpeakOntouchListener();

    private void initAction() {
//        btnSpeak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        btnSpeak.setOnTouchListener(speakOntouchListener);
    }

    class SpeakOntouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                btnSpeak.setImageDrawable(getResources().getDrawable(R.drawable.mic_2));
                start();
                count =1;
                pressed = true;
                mHandler.sendEmptyMessage(count);
                try{
                    timer = new Timer();
                    timer.schedule(new MyTask(),0,1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;

            }else if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                btnSpeak.setImageDrawable(getResources().getDrawable(R.drawable.mic_1));
                stop();
                countDown.setVisibility(View.INVISIBLE);
                countDown.setImageDrawable(getResources().getDrawable(R.drawable.time0005));
                pressed = false;
                timer.cancel();
                count =1;
                return true;
            }
//            btnSpeak.setOnTouchListener(speakOntouchListener);
            return false;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    public boolean initPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        initPermission();
    }

    @Override
    public void onBackPressed() {

    }

    Timer timer = new Timer();


    class MyTask extends TimerTask{

        @Override
        public void run() {
            mHandler.sendEmptyMessage(count++);
        }
    }



    OpReceiver receiver = new OpReceiver();

    class OpReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");

            if(action.equals("1")){
                start();
            }else if(action.equals("2")){
                stop();
            }
        }
    }

}
