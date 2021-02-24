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
import android.widget.Button;
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


class NearestNoteExpert {
    NearestNoteExpert( double _hertz, char _note) {
        this.hertz = _hertz;
        this.note = _note;
    }
    public double hertz;
    public char note;
}

public class GuitarTunerExpert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_tuner_expert);

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        final TextView current_hertz_expert = findViewById(R.id.current_hertz);
        final TextView expected_note_expert = findViewById(R.id.expected_note_expert);
        final TextView expected_note_hertz_expert = findViewById(R.id.expected_note_hertz_expert);
        final TextView tuner_diff_expert = findViewById(R.id.tuner_diff_expert);
        final Button choosebtn = findViewById(R.id.btnShow);
        final ImageButton backtomain = findViewById(R.id.backtomain);

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
                            current_hertz_expert.setText("" + String.format("%.1f", inputHertz));
                            NearestNoteExpert nearestNote = getNearestNoteExpert(inputHertz);
                            double diff = (inputHertz - nearestNote.hertz);
                            expected_note_expert.setText("" + nearestNote.note);
                            expected_note_hertz_expert.setText("(" + nearestNote.hertz + ")" + "Hz");
                            tuner_diff_expert.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.3) {
                                tuner_diff_expert.setText("0");
                                tuner_diff_expert.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.3) {
                                tuner_diff_expert.setText("-1");
                                tuner_diff_expert.setTextColor(Color.YELLOW);
                            } else if (diff > 0.3 && diff <= 2) {
                                tuner_diff_expert.setText("1");
                                tuner_diff_expert.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff_expert.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff_expert.setTextColor(Color.RED);
                            }
                            else{
                                expected_note_expert.setText("");
                                expected_note_hertz_expert.setText("");
                                tuner_diff_expert.setText("");
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
    public static NearestNoteExpert getNearestNoteExpert(double inputHertz) {
        NearestNoteExpert nearest_note;
        if (inputHertz >= 70 && inputHertz < 96)
            nearest_note = new NearestNoteExpert(82.4, 'e');
        else if (inputHertz >= 96 && inputHertz < 125)
            nearest_note = new NearestNoteExpert(110.0, 'A');
        else if (inputHertz >= 130 && inputHertz < 160)
            nearest_note = new NearestNoteExpert(146.8, 'D');
        else if (inputHertz >= 180 && inputHertz < 210)
            nearest_note = new NearestNoteExpert(196.0, 'G');
        else if (inputHertz >= 230 && inputHertz < 260)
            nearest_note = new NearestNoteExpert(246.9, 'B');
        else if (inputHertz >= 315 && inputHertz < 345)
            nearest_note = new NearestNoteExpert(329.6, 'E');
        else
            nearest_note = new NearestNoteExpert(-1, ' ');

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
                GuitarTunerExpert.this.startActivity(intent);}
        else if (itemID == R.id.switch_expert){
            Toast.makeText(context,"you are already in this mode", Toast.LENGTH_SHORT).show(); }
        else if (itemID == R.id.switch_ukulele){
            Intent intent = new Intent(context, UkuleleTuner.class);
                GuitarTunerExpert.this.startActivity(intent);}
        else if (itemID == R.id.switch_bass){
            Intent intent = new Intent(context, BassTuner.class);
                GuitarTunerExpert.this.startActivity(intent);}
        else if (itemID == R.id.switch_general){
            Intent intent = new Intent(context, GeneralTuner.class);
                GuitarTunerExpert.this.startActivity(intent);}
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
