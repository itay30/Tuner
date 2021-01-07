package com.example.tuner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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


class NearestNoteExpert {
    NearestNoteExpert( double _hertz, char _note) {
        this.hertz = _hertz;
        this.note = _note;
    }
    public double hertz;
    public char note;

    @Override
    public String toString() {
        if (hertz == -1)
            return " ";
        return  "" + note + "\n" + hertz + " Hz";
    }
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
        final TextView tuner_diff_expert = findViewById(R.id.tuner_diff_expert);
        final Button choosebtn = findViewById(R.id.btnShow);
        final Button backtomain = findViewById(R.id.backtomain);

        backtomain.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });


        //Thread provided by the package Tarsos DPS to take sound input and convert it to hertz
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult inputSound, AudioEvent e) {
                final double inputHertz = (double) inputSound.getPitch();   //Method which converts input sound to its pitch in hertz
                if (inputHertz != -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Sets the text boxes to display according to the hertz that was played
                            current_hertz_expert.setText("" + String.format("%.1f", inputHertz));
                            NearestNoteExpert nearestNote = getNearestNoteExpert(inputHertz);
                            int diff = (int) (inputHertz - nearestNote.hertz);
                            expected_note_expert.setText("" + nearestNote.note  +  nearestNote.hertz);
                            tuner_diff_expert.setText("" + diff);
                            if (Math.abs(diff) <= 1) {
                                tuner_diff_expert.setTextColor(Color.GREEN);
                            } else if (Math.abs(diff) <= 3) {
                                tuner_diff_expert.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff_expert.setTextColor(Color.RED);
                            }
                            else{
                                expected_note_expert.setText("");
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
            Toast.makeText(context,"you are allready in this mode", Toast.LENGTH_LONG).show();
        }
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