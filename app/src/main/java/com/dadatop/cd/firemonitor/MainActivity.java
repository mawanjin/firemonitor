package com.dadatop.cd.firemonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dadatop.cd.firemonitor.activity.ActivityWakeUp;
import com.dadatop.cd.firemonitor.recog.ActivityOfflineRecog;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ActivityOfflineRecog.class));
            }
        });

        findViewById(R.id.wakeUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ActivityWakeUp.class));
            }
        });

    }
}
