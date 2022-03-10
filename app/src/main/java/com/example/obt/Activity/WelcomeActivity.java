package com.example.obt.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.obt.LoginAndRegister.LoginActivity;
import com.example.obt.R;

public class WelcomeActivity extends AppCompatActivity {

    private TextView skip;
    private int TIME = 3;
    private boolean isSkip = false;
    private Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        skip = (TextView)findViewById(R.id.skip);

        // 将欢迎界面系统自带的标题栏隐藏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case -2:
                        skip.setText("跳过( "+TIME+"s )");
                        break;
                    case 1:
                        // 这里记得要判断是否选择跳过，防止重复加载LoginActivity
                        if (!isSkip) {
                            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            isSkip = true;
                            WelcomeActivity.this.finish();
                        }
                        break;
                }
            }
        };

        myThread = new Thread(new Runnable() {  // 开启一个线程倒计时
            @Override
            public void run() {
                for (; TIME>0; TIME--){  // TIME初始化为3
                    handler.sendEmptyMessage(-2);
                    if (TIME<=0)
                        break;
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);
            }
        });

        myThread.start();


        skip.setOnClickListener(new View.OnClickListener() {  // 设置跳过按键的监听器
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                isSkip = true;
                WelcomeActivity.this.finish();
            }
        });
    }
}
