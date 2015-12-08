package com.shehi.regi.androidinvaders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                    Intent i = new Intent(getBaseContext(), AndroidInvaders.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {

                }
            }
        };
        background.start();
    }
}
