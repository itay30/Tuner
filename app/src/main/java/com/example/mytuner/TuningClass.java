package com.example.mytuner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class TuningClass extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;
    private final Context context = this;
    BottomNavigationView bottomNavigationView;

    String TuneCase = "guitar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning_class);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, new GuitarFragment());
        ft.addToBackStack(null);
        ft.commit();

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        final TextView current_hertz_small = findViewById(R.id.current_hertz_small);
        final TextView expected_note = findViewById(R.id.expected_note);
        final TextView tuner_diff = findViewById(R.id.tuner_diff);
        final ImageButton btn_info = findViewById(R.id.btn_info);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        btn_info.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });


        //Thread provided by the package Tarsos DPS to take sound input and convert it to hertz
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult inputSound, AudioEvent e) {
                final double inputHertz = (double) inputSound.getPitch();   //Method which converts input sound to its pitch in hertz
                final int ORANGE = Color.rgb(255, 145, 0);
                if (inputHertz != -1 && !(TuneCase.equals("chromatic"))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Sets the text boxes to display according to the hertz that is playing
                            NearestNote nearestNote = getNearestNote(inputHertz, TuneCase);
                            double diff = (inputHertz - nearestNote.hertz);
                            current_hertz_small.setText("" + String.format("%.1f", inputHertz));
                            expected_note.setText("" + nearestNote.note);
                            tuner_diff.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.3) {
                                tuner_diff.setText("0");
                                tuner_diff.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.3) {
                                tuner_diff.setText("-1");
                                tuner_diff.setTextColor(Color.YELLOW);
                            } else if (diff > 0.3 && diff <= 2) {
                                tuner_diff.setText("1");
                                tuner_diff.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff.setTextColor(Color.RED);
                            } else {
                                expected_note.setText("");
                                tuner_diff.setText("");
                            }
                        }
                    });
                }else if(inputHertz != -1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Sets the text boxes to display according to the hertz that was played
                            Note note = new Note(inputHertz);
                            current_hertz_small.setText("" + String.format("%.1f", inputHertz));
                            double diff = (inputHertz - note.getExactFrequency());
                            expected_note.setText("" + note.toString());
                            tuner_diff.setText("" + String.format("%.0f", diff));
                            if (Math.abs(diff) <= 0.5) {
                                tuner_diff.setText("0");
                                tuner_diff.setTextColor(Color.GREEN);
                            } else if (diff >= -2 && diff < -0.5) {
                                tuner_diff.setText("-1");
                                tuner_diff.setTextColor(Color.YELLOW);
                            } else if (diff > 0.5 && diff <= 2) {
                                tuner_diff.setText("1");
                                tuner_diff.setTextColor(Color.YELLOW);
                            } else if (Math.abs(diff) <= 5) {
                                tuner_diff.setTextColor(ORANGE);
                            } else if (Math.abs(diff) <= 15) {
                                tuner_diff.setTextColor(Color.RED);
                            }
                            else{
                                expected_note.setText("");
                                tuner_diff.setText("");
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

        gestureDetector = new GestureDetector(this);
    }


    public static NearestNote getNearestNote(double inputHertz, String TuneCase) {
        NearestNote nearest_note;
        if (TuneCase.equals("guitar")) {
            if (inputHertz >= 70 && inputHertz < 96)
                nearest_note = new NearestNote(82.4, 'e');
            else if (inputHertz >= 96 && inputHertz < 125)
                nearest_note = new NearestNote(110.0, 'A');
            else if (inputHertz >= 130 && inputHertz < 160)
                nearest_note = new NearestNote(146.8, 'D');
            else if (inputHertz >= 180 && inputHertz < 210)
                nearest_note = new NearestNote(196.0, 'G');
            else if (inputHertz >= 230 && inputHertz < 260)
                nearest_note = new NearestNote(246.9, 'B');
            else if (inputHertz >= 315 && inputHertz < 345)
                nearest_note = new NearestNote(329.6, 'E');
            else
                nearest_note = new NearestNote(-1, ' ');
            return nearest_note;}

        else if(TuneCase.equals("ukulele")){
            if (inputHertz >= 377 && inputHertz < 407)
                nearest_note = new NearestNote(392, 'G');
            else if (inputHertz >= 246 && inputHertz < 276)
                nearest_note = new NearestNote(261.6, 'C');
            else if (inputHertz >= 315 && inputHertz < 345)
                nearest_note = new NearestNote(329.6, 'E');
            else if (inputHertz >= 425 && inputHertz < 455)
                nearest_note = new NearestNote(440, 'A');
            else
                nearest_note = new NearestNote(-1, ' ');
            return nearest_note;}

        else if(TuneCase.equals("bass")){
            if (inputHertz >= 26 && inputHertz < 56)
                nearest_note = new NearestNote(41.2, 'E');
            else if (inputHertz >= 40 && inputHertz < 70)
                nearest_note = new NearestNote(55.0, 'A');
            else if (inputHertz >= 58 && inputHertz < 88)
                nearest_note = new NearestNote(73.4, 'D');
            else if (inputHertz >= 83 && inputHertz < 113)
                nearest_note = new NearestNote(98.0, 'G');
            else
                nearest_note = new NearestNote(-1, ' ');
            return nearest_note;}

        else
            nearest_note = new NearestNote(-1, ' ');

        return nearest_note;
    }

    public void setFragment (String freg){
        Fragment fragment;

        switch(freg){
            case("guitar"):
                fragment = new GuitarFragment();
                break;
            case("ukulele"):
                fragment = new UkuleleFragment();
                break;
            case("bass"):
                fragment = new BassFragment();
                break;
            case("chromatic"):
                fragment = new ChromaticFragment();
                break;
            default:
                return;
        }
        TuneCase = freg;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, fragment);
        ft.commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemID = item.getItemId();

                    switch (itemID){
                        case(R.id.guitar):
                            setFragment("guitar");
                            break;
                        case(R.id.ukulele):
                            setFragment("ukulele");
                            break;
                        case(R.id.bass):
                            setFragment("bass");
                            break;
                        case(R.id.chromatic):
                            setFragment("chromatic");
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            };

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                if (diffX > 0 ){
                    onSwipeRight();
                }else{
                    onSwipeLeft();
                }
                result = true;
            }
        }
        return result;
    }

    private void onSwipeRight() {
        int itemID = bottomNavigationView.getSelectedItemId();

        switch (itemID){
            case(R.id.guitar):
                setFragment("chromatic");
                bottomNavigationView.setSelectedItemId(R.id.chromatic);
                break;
            case(R.id.ukulele):
                setFragment("guitar");
                bottomNavigationView.setSelectedItemId(R.id.guitar);
                break;
            case(R.id.bass):
                setFragment("ukulele");
                bottomNavigationView.setSelectedItemId(R.id.ukulele);
                break;
            case(R.id.chromatic):
                setFragment("bass");
                bottomNavigationView.setSelectedItemId(R.id.bass);
                break;
            default:
                return;
        }
    }

    private void onSwipeLeft() {
        int itemID = bottomNavigationView.getSelectedItemId();

        switch (itemID){
            case(R.id.guitar):
                setFragment("ukulele");
                bottomNavigationView.setSelectedItemId(R.id.ukulele);
                break;
            case(R.id.ukulele):
                setFragment("bass");
                bottomNavigationView.setSelectedItemId(R.id.bass);
                break;
            case(R.id.bass):
                setFragment("chromatic");
                bottomNavigationView.setSelectedItemId(R.id.chromatic);
                break;
            case(R.id.chromatic):
                setFragment("guitar");
                bottomNavigationView.setSelectedItemId(R.id.guitar);
                break;
            default:
                return;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

class NearestNote {
    NearestNote( double _hertz, char _note) {
        this.hertz = _hertz;
        this.note = _note;
    }
    public double hertz;
    public char note;
}

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

    public double getExactFrequency() {
        return this._exact_frequency;
    }

    public String toString() {
        return this._note + this._octave + " " + "(" + String.format("%.1f",this._exact_frequency) + ")";
    }
}

