package com.example.tuner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


class NearestNoteUkulele {
    NearestNoteUkulele( double _hertz, char _note) {
        this.hertz = _hertz;
        this.note = _note;
    }
    public double hertz;
    public char note;
}

public class UkuleleTuner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ukulele_tuner);

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        final TextView expected_note_ukulele = findViewById(R.id.expected_note_ukulele);
        final TextView tuner_diff_ukulele = findViewById(R.id.tuner_diff_ukulele);
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
                            NearestNoteUkulele nearestNote = getNearestNoteUkulele(inputHertz);
                            double diff = (inputHertz - nearestNote.hertz);
                            expected_note_ukulele.setText("" + nearestNote.note);
                            tuner_diff_ukulele.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.5) {
                                tuner_diff_ukulele.setText("0");
                                tuner_diff_ukulele.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.5) {
                                tuner_diff_ukulele.setText("-1");
                                tuner_diff_ukulele.setTextColor(Color.YELLOW);
                            } else if (diff > 0.5 && diff <= 2) {
                                tuner_diff_ukulele.setText("1");
                                tuner_diff_ukulele.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff_ukulele.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff_ukulele.setTextColor(Color.RED);
                            }
                            else{
                                expected_note_ukulele.setText("");
                                tuner_diff_ukulele.setText("");
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
    public static NearestNoteUkulele getNearestNoteUkulele(double inputHertz) {
        NearestNoteUkulele nearest_note;
        if (inputHertz >= 377 && inputHertz < 407)
            nearest_note = new NearestNoteUkulele(392, 'G');
        else if (inputHertz >= 246 && inputHertz < 276)
            nearest_note = new NearestNoteUkulele(261.6, 'C');
        else if (inputHertz >= 315 && inputHertz < 345)
            nearest_note = new NearestNoteUkulele(329.6, 'E');
        else if (inputHertz >= 425 && inputHertz < 455)
            nearest_note = new NearestNoteUkulele(440, 'A');
        else
            nearest_note = new NearestNoteUkulele(-1, ' ');

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
                UkuleleTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_expert){
            Intent intent = new Intent(context, GuitarTunerExpert.class);
                UkuleleTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_ukulele){
            Toast.makeText(context,"you are already in this mode", Toast.LENGTH_SHORT).show();}
        else if (itemID == R.id.switch_bass){
            Intent intent = new Intent(context, BassTuner.class);
                UkuleleTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_general){
            Intent intent = new Intent(context, GeneralTuner.class);
                UkuleleTuner.this.startActivity(intent);}
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