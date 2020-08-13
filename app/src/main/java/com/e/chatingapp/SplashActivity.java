package com.e.chatingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //creating new thread just for demonstration of background tasks
        Thread t=new Thread() {
            public void run() {

                try {
                    //sleep thread for 2 seconds
                    sleep(2000);

                    //Call Main activity
                    Intent i=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(i);

                    //destroying Splash activity
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //start thread
        t.start();
    }
}

