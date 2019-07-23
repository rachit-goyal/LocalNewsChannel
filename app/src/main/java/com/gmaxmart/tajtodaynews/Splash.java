package com.gmaxmart.tajtodaynews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    private static final int SPLASH_SCREEN_MIN_VISIBLE_TIME = 3;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.splashstatuscolor));
        }
        getDisplayWidth(Splash.this);
        getDisplayHeight(Splash.this);
        timer = new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {

                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    Splash.this.startActivity(intent);
                    Splash.this.finish();
                }

        };
        timer.schedule(task, SPLASH_SCREEN_MIN_VISIBLE_TIME * 1000);
    }
    private void getDisplayWidth(Activity a) {
        Display display = a.getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
    }

    private void getDisplayHeight(Activity a) {
        Display display = a.getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
    }
    public void stopThread(){
        if(timer!= null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopThread();
    }

}
