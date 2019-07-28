package com.dadatop.cd.firemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dadatop.cd.firemonitor.setting.PrefUtil;
import com.dadatop.cd.firemonitor.socket.SocketUtil;

import org.w3c.dom.Text;

import java.net.SocketImpl;

public class SettingServerActivity extends Activity{

    EditText txtServer,txtPort;
    Button btnSubmit,btnConnectServer;
    TextView server_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_server);
        txtServer = findViewById(R.id.txtServer);
        txtPort = findViewById(R.id.txtPort);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnConnectServer = findViewById(R.id.btnConnectServer);
        server_status = findViewById(R.id.server_status);

        txtServer.setText(PrefUtil.getInstance(this).getSocketServer());
        txtPort.setText(PrefUtil.getInstance(this).getSocketPort()+"");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtil.getInstance(SettingServerActivity.this).setSocketServer(txtServer.getText().toString());
                PrefUtil.getInstance(SettingServerActivity.this).setSocketPort(txtPort.getText().toString());
                Toast.makeText(SettingServerActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
        });

        btnConnectServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil.getInstance().connect();
            }
        });

        mHandler.sendEmptyMessage(1);

        server_status.setText("当前状态:"+SocketUtil.getInstance().getStatus()+"");

    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            server_status.setText("当前状态:"+SocketUtil.getInstance().getStatus()+"");
            int s = SocketUtil.getInstance().getStatus();
            if(s==0){
                mHandler.sendEmptyMessageDelayed(1,2000);
            }

        }
    };
}
