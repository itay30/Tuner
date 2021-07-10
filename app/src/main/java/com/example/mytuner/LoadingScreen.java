package com.example.mytuner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingScreen extends AppCompatActivity {

    private Timer timer;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView percentage = findViewById(R.id.tv_percent);


        timer = new Timer();
        int Period = 20;
        int delay = 0;
        timer.schedule( new TimerTask() {
            @SuppressLint("SetTextI18n")
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void run() {
                if(i < 100)
                {
                    runOnUiThread( () -> percentage.setText( i + "%") );
                    progressBar.setProgress(i, true);
                    i++;
                }
                else if (ContextCompat.checkSelfPermission(LoadingScreen.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    timer.cancel();
                    startActivity(new Intent(LoadingScreen.this, TuningClass.class));
                    finish();
                }else{
                    timer.cancel();
                    startActivity(new Intent(LoadingScreen.this, MainActivity.class));
                    finish();
                }
            }
        }, delay, Period );
    }
}
