package com.dadatop.cd.firemonitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dadatop.cd.firemonitor.recog.ActivityOfflineRecog;
import com.dadatop.cd.firemonitor.socket.SocketUtil;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        findViewById(R.id.setting).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this,ActivityOfflineRecog.class));
                return false;
            }
        });

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SocketUtil.getInstance().isConnected()){
                    SocketUtil.getInstance().sendReady();
                }else {
                    Toast.makeText(MainActivity.this,"服务器未连接",Toast.LENGTH_SHORT).show();
                }
            }
        });

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

//        startActivity(new Intent(MainActivity.this,DialActivity.class));
        initSocket();
        return true;

    }


    private void initSocket() {
        SocketUtil.getInstance().set_activity(this);
        SocketUtil.getInstance().connect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        initPermission();
    }
}
