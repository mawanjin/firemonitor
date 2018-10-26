package com.dadatop.cd.firemonitor.activity;

import android.os.Bundle;
import android.view.View;

import com.baidu.speech.asr.SpeechConstant;
import com.dadatop.cd.firemonitor.R;
import com.dadatop.cd.firemonitor.core.recog.IStatus;
import com.dadatop.cd.firemonitor.core.wakeup.IWakeupListener;
import com.dadatop.cd.firemonitor.core.wakeup.MyWakeup;
import com.dadatop.cd.firemonitor.core.wakeup.RecogWakeupListener;

import java.util.HashMap;
import java.util.Map;

public class ActivityWakeUp extends ActivityCommon implements IStatus {

    protected MyWakeup myWakeup;
    private int status = STATUS_NONE;
    private static final String TAG = "ActivityWakeUp";

    public ActivityWakeUp() {
        this(R.raw.normal_wakeup);
    }

    public ActivityWakeUp(int textId) {
        super(textId, R.layout.common_without_setting);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        IWakeupListener listener = new RecogWakeupListener(handler);
        myWakeup = new MyWakeup(this, listener);
    }


    // 点击“开始识别”按钮
    private void start() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        myWakeup.start(params);
    }


    protected void stop() {
        myWakeup.stop();
    }

    @Override
    protected void initView() {
        super.initView();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE:
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtLog.setText("");
                        txtResult.setText("");
                        break;
                    case STATUS_WAITING_READY:
                        stop();
                        status = STATUS_NONE;
                        updateBtnTextByStatus();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("启动唤醒");
                break;
            case STATUS_WAITING_READY:
                btn.setText("停止唤醒");
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        myWakeup.release();
        super.onDestroy();
    }
}
