package com.readinsite.ranchlife.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.readinsite.ranchlife.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                goToLoginctivity();

                finish();
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 1500);
    }

    private void goToLoginctivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }
}
