package com.dadatop.cd.firemonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dadatop.cd.firemonitor.recog.ActivityOfflineRecog;

public class DialActivity extends Activity implements View.OnClickListener {

    EditText inputTxt;
    LinearLayout btnDel;
    ImageView num1,num2,num3,num4,num5,num6,num7,num8,num9,num0,nums,numw,btn_call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        inputTxt = findViewById(R.id.inputTxt);

        initView();
        initAction();
    }

    private void initView() {
        btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);
        num1 = findViewById(R.id.num1);
        num1.setOnClickListener(this);
        num2 = findViewById(R.id.num2);
        num2.setOnClickListener(this);
        num3 = findViewById(R.id.num3);
        num3.setOnClickListener(this);
        num4 = findViewById(R.id.num4);
        num4.setOnClickListener(this);
        num5 = findViewById(R.id.num5);
        num5.setOnClickListener(this);
        num6 = findViewById(R.id.num6);
        num6.setOnClickListener(this);
        num7 = findViewById(R.id.num7);
        num7.setOnClickListener(this);
        num8 = findViewById(R.id.num8);
        num8.setOnClickListener(this);
        num9 = findViewById(R.id.num9);
        num9.setOnClickListener(this);
        num0 = findViewById(R.id.num0);
        num0.setOnClickListener(this);
        nums = findViewById(R.id.nums);
        nums.setOnClickListener(this);
        numw = findViewById(R.id.numw);
        numw.setOnClickListener(this);

        btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
    }

    private void initAction() {

//        //num1
//        findViewById(R.id.num1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                inputTxt.setText(inputTxt.getText().toString()+"1");
//            }
//        });
//        //num2
//        findViewById(R.id.num2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                inputTxt.setText(inputTxt.getText().toString()+"2");
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnDel.getId()){
            String str =  inputTxt.getText().toString();
            if(str!=null&&!str.isEmpty()){
                if(str.length()==1)str = "";
                else {
                    str = str.substring(0,str.length()-1);
                }
            }
            inputTxt.setText(str);
        }else if(view.getId() == num1.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"1");
        }else if(view.getId() == num2.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"2");
        }else if(view.getId() == num3.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"3");
        }else if(view.getId() == num4.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"4");
        }else if(view.getId() == num5.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"5");
        }else if(view.getId() == num6.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"6");
        }else if(view.getId() == num7.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"7");
        }else if(view.getId() == num8.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"8");
        }else if(view.getId() == num9.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"9");
        }else if(view.getId() == num0.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"0");
        }else if(view.getId() == nums.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"*");
        }else if(view.getId() == numw.getId()){
            inputTxt.setText(inputTxt.getText().toString()+"#");
        }else if(view.getId() == btn_call.getId()){
            String str = inputTxt.getText().toString();
            if(!str.isEmpty() && str.equals("119")){
                //todo
                Toast.makeText(DialActivity.this,"good",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DialActivity.this,RecoActivity.class));

            }else {
                Toast.makeText(DialActivity.this,"输入有误",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
