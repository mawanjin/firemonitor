package com.dadatop.cd.firemonitor.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dadatop.cd.firemonitor.R;
import com.dadatop.cd.firemonitor.core.inputstream.InFileStream;
import com.dadatop.cd.firemonitor.core.util.MyLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by fujiayi on 2017/6/20.
 */

public abstract class ActivityCommon extends AppCompatActivity {
    protected TextView txtLog;
    protected Button btn;
    protected Button setting,serverSetting;
    protected TextView txtResult;

    protected Handler handler;

    protected final int layout;
    private final int textId;

    public ActivityCommon(int textId) {
        this(textId, R.layout.common);
    }

    public ActivityCommon(int textId, int layout) {
        super();
        this.textId = textId;
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStrictMode();
        InFileStream.setContext(this);
        setContentView(layout);
        initView();
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
        MyLogger.setHandler(handler);
        initPermission();
    }

    protected void handleMsg(Message msg) {
        if (txtLog != null && msg.obj != null) {
            String rs = msg.obj.toString();
            txtLog.append(msg.obj.toString() + "\n");
            if(rs.contains("{\"sn\":\"\",\"error\":9,\"desc\":\"No recorder permission\",\"sub_error\":9001}")){
                initPermission();
            }else if(rs.contains("\"sub_error\":10005")){
                Toast.makeText(this,"请联网下载授权文件",Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void initView() {
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (Button) findViewById(R.id.btn);
        setting = (Button) findViewById(R.id.setting);
        serverSetting = (Button) findViewById(R.id.serverSetting);

        String descText = "";
        try {
            InputStream is = getResources().openRawResource(textId);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            descText = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtLog.setText(descText);
        txtLog.append("\n");
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
    }

    private void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

    }
}
