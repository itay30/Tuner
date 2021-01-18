package com.example.tuner;

import  android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Background music service.
 */
public class MyService extends Service {
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public  void onCreate() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        mMediaPlayer.setLooping(true);
    }

    public void onStart(Intent intent, int startId) { mMediaPlayer.start(); }

    public  void onDestroy() { mMediaPlayer.stop(); }
}
