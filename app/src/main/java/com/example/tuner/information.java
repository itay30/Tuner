package com.example.tuner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;


public class information extends AppCompatActivity {
    private MediaPlayer ring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        final ImageButton backtomain = findViewById(R.id.backtomain);
        final Context context = this;


        backtomain.setOnClickListener(v -> {
            stopMediaPlayer();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

            startService(new Intent(getApplicationContext(), MyService.class));
            ring = MediaPlayer.create(information.this, R.raw.music);
            ring.start();
    }
    //stops the music
    private void stopMediaPlayer() {
        try {
            if (ring != null) {
                if (ring.isPlaying())
                    ring.stop();
                ring.release();
                ring = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        stopMediaPlayer();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}