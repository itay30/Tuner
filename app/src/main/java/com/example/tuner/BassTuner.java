package com.example.tuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.content.Context;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import android.widget.Toast;


class NearestNoteBass {
    NearestNoteBass( double _hertz, char _note) {
        this.hertz = _hertz;
        this.note = _note;
    }
    public double hertz;
    public char note;
}

public class BassTuner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bass_tuner);

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        final TextView expected_note_bass = findViewById(R.id.expected_note_bass);
        final TextView tuner_diff_bass = findViewById(R.id.tuner_diff_bass);
        final ImageButton backtomain = findViewById(R.id.backtomain);
        final Context context = this;

        backtomain.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });


        //Thread provided by the package Tarsos DPS to take sound input and convert it to hertz
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult inputSound, AudioEvent e) {
                final double inputHertz = (double) inputSound.getPitch();   //Method which converts input sound to its pitch in hertz
                final int ORANGE = Color.rgb(255,145,0);
                if (inputHertz != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Sets the text boxes to display according to the hertz that was played
                            NearestNoteBass nearestNote = getNearestNoteBass(inputHertz);
                            double diff = (inputHertz - nearestNote.hertz);
                            expected_note_bass.setText("" + nearestNote.note);
                            tuner_diff_bass.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.5) {
                                tuner_diff_bass.setText("0");
                                tuner_diff_bass.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.5) {
                                tuner_diff_bass.setText("-1");
                                tuner_diff_bass.setTextColor(Color.YELLOW);
                            } else if (diff > 0.5 && diff <= 2) {
                                tuner_diff_bass.setText("1");
                                tuner_diff_bass.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff_bass.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff_bass.setTextColor(Color.RED);
                            }
                            else{
                                expected_note_bass.setText("");
                                tuner_diff_bass.setText("");
                            }
                        }
                    });
                }
            }
        };
        //Tells the package which algorithm to use, what the sample size is and what the input buffer size should be
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        //Sets the input of this process to the microphone
        dispatcher.addAudioProcessor(p);
        //Starts the thread
        new Thread(dispatcher).start();
    }
    public static NearestNoteBass getNearestNoteBass(double inputHertz) {
        NearestNoteBass nearest_note;
        if (inputHertz >= 26 && inputHertz < 56)
            nearest_note = new NearestNoteBass(41.2, 'E');
        else if (inputHertz >= 40 && inputHertz < 70)
            nearest_note = new NearestNoteBass(55.0, 'A');
        else if (inputHertz >= 58 && inputHertz < 88)
            nearest_note = new NearestNoteBass(73.4, 'D');
        else if (inputHertz >= 83 && inputHertz < 113)
            nearest_note = new NearestNoteBass(98.0, 'G');
        else
            nearest_note = new NearestNoteBass(-1, ' ');

        return nearest_note;
    }

    final Context context = this;
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.switch_begginer){
            Intent intent = new Intent(context, GuitarTuner.class);
            BassTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_expert){
            Intent intent = new Intent(context, GuitarTunerExpert.class);
            BassTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_ukulele){
            Intent intent = new Intent(context, UkuleleTuner.class);
            BassTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_bass){
            Toast.makeText(context,"you are already in this mode", Toast.LENGTH_SHORT).show();}
        else if (itemID == R.id.switch_general){
            Intent intent = new Intent(context, GeneralTuner.class);
            BassTuner.this.startActivity(intent);}
        return super.onOptionsItemSelected(item);
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
    }

}