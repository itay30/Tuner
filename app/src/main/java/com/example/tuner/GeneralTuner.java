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

import java.util.HashMap;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

class Note
{
    // Class' constants
    private static final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"}; // all 12 notes in each octave
    private static final double NOTE_MULTIPLIER = 1.05946309436; // Math.pow(2, (1/12)) or 2^(1/12)
    private static final Map<String, Double> KNOWN_NOTES = new HashMap<String, Double>() {/**
     *
     */
    private static final long serialVersionUID = 1L;

        {
            put("C", 261.63);
            put("C#", 277.18);
            put("D", 293.66);
            put("D#", 311.13);
            put("E", 329.63);
            put("F", 349.23);
            put("F#", 369.99);
            put("G", (double) 392);
            put("G#", 415.3);
            put("A", (double) 440);
            put("A#", 466.16);
            put("B", 493.88);
        }}; // every note in 4th octave

    // Class' members
    private String _note;
    private int _octave;
    private double _exact_frequency;

    // private function to get exact frequency of a note
    private void _setExactFrequency() {
        int octave_difference = this._octave - 4;
        double note_at_known_octave_freq = Note.KNOWN_NOTES.get(this._note);
        double exact_frequency = note_at_known_octave_freq * Math.pow(2, octave_difference);

        this._exact_frequency = exact_frequency;
    }



    public Note(double frequency) {
        // A4 = 440Hz
        final String KNOWN_NOTE_NAME = "A";
        final int KNOWN_NOTE_OCTAVE = 4;
        final int KNOWN_NOTE_FREQUENCY = 440;

        double relative_freq_to_known_note = frequency / KNOWN_NOTE_FREQUENCY;
        double distance_from_known_note = Math.log(relative_freq_to_known_note) / Math.log(NOTE_MULTIPLIER); // take log with base {multiplier}

        distance_from_known_note = Math.round(distance_from_known_note); // round the number

        int known_note_idx_in_octave = java.util.Arrays.asList(NOTES).indexOf(KNOWN_NOTE_NAME);
        int known_note_abs_indx = KNOWN_NOTE_OCTAVE * NOTES.length + known_note_idx_in_octave;
        int note_absolute_index = known_note_abs_indx + (int)distance_from_known_note;

        int note_octave = (int)Math.floor(note_absolute_index / NOTES.length);
        int note_idx_in_octave = note_absolute_index % NOTES.length;
        String note_name = NOTES[note_idx_in_octave];

        this._note = note_name;
        this._octave = note_octave;
        this._setExactFrequency();
    }

        public Note(String note, int octave) {
             this._note = note;
            this._octave = octave;
            this._setExactFrequency();
        }

    public String getNote() {
        return this._note;
    }

    public int getOctave() {
        return this._octave;
    }

    public double getExactFrequency() {
        return this._exact_frequency;
    }

    public String toString() {
        return this._note + this._octave + " " + "(" + String.format("%.1f",this._exact_frequency) + ")";
    }
}

public class GeneralTuner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_tuner);

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        final TextView current_hertz_general = findViewById(R.id.current_hertz_general);
        final TextView expected_note_general = findViewById(R.id.expected_note_general);
        final TextView tuner_diff_general = findViewById(R.id.tuner_diff_general);
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
                            current_hertz_general.setText("" + String.format("%.1f", inputHertz));
                            Note note = new Note(inputHertz);
                            double diff = (inputHertz - note.getExactFrequency());
                            expected_note_general.setText("" +note.toString());
                            tuner_diff_general.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.5) {
                                tuner_diff_general.setText("0");
                                tuner_diff_general.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.5) {
                                tuner_diff_general.setText("-1");
                                tuner_diff_general.setTextColor(Color.YELLOW);
                            } else if (diff > 0.5 && diff <= 2) {
                                tuner_diff_general.setText("1");
                                tuner_diff_general.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff_general.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff_general.setTextColor(Color.RED);
                            }
                            else{
                                expected_note_general.setText("");
                                tuner_diff_general.setText("");
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
            GeneralTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_expert){
            Intent intent = new Intent(context, GuitarTunerExpert.class);
            GeneralTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_ukulele){
            Intent intent = new Intent(context, UkuleleTuner.class);
            GeneralTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_bass){
            Intent intent = new Intent(context, BassTuner.class);
            GeneralTuner.this.startActivity(intent);}
        else if (itemID == R.id.switch_general){
            Toast.makeText(context,"you are already in this mode", Toast.LENGTH_SHORT).show(); }
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