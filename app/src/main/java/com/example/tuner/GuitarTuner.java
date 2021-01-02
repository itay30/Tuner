package com.example.tuner;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

class NearestNote {
    NearestNote( double _hertz, char _note) {
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

//class Helper {
//    public static NearestNote getNearestNote(double inputHertz) {
////        if (inputHertz > 350)
////            return new Ans(0, "BLACK", nearest_note);
////        if (inputHertz >= 0 && inputHertz < 96)
////            nearest_note = new NearestNote(82.4, 'e');
////        else if (inputHertz >= 96 && inputHertz < 128)
////            nearest_note = new NearestNote(110.0, 'A');
////        else if (inputHertz >= 128 && inputHertz < 171)
////            nearest_note = new NearestNote(146.8, 'D');
////        else if (inputHertz >= 171 && inputHertz < 221)
////            nearest_note = new NearestNote(196.0, 'G');
////        else if (inputHertz >= 221 && inputHertz < 287.5)
////            nearest_note = new NearestNote(246.9, 'B');
////        else
////            nearest_note = new NearestNote(329.6, 'E');
//        NearestNote nearest_note;
//        if (inputHertz >= 70 && inputHertz < 96)
//            nearest_note = new NearestNote(82.4, 'e');
//        else if (inputHertz >= 96 && inputHertz < 125)
//            nearest_note = new NearestNote(110.0, 'A');
//        else if (inputHertz >= 130 && inputHertz < 160)
//            nearest_note = new NearestNote(146.8, 'D');
//        else if (inputHertz >= 180 && inputHertz < 210)
//            nearest_note = new NearestNote(196.0, 'G');
//        else if (inputHertz >= 230 && inputHertz < 260)
//            nearest_note = new NearestNote(246.9, 'B');
//        else if (inputHertz >= 315 && inputHertz < 345)
//            nearest_note = new NearestNote(329.6, 'E');
//        else
//            nearest_note = new NearestNote(-1, ' ');
//
//        return nearest_note;
//
////        int diff = (int) (inputHertz - nearest_note.hertz);
////        if (Math.abs(diff) <= 1) {
////            return new Ans(diff, "GREEN", nearest_note);
////        } else if (Math.abs(diff) <= 3) {
////            return new Ans(diff, "YELLOW", nearest_note);
////        } else {
////            return new Ans(diff, "RED", nearest_note);
////        }
//
//    }
//}

public class GuitarTuner extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guitar_tuner);

        //Initializes the microphone for user input and text boxes for output on screen
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
//        final TextView current_hertz = findViewById(R.id.current_hertz);
        final TextView expected_note = findViewById(R.id.expected_note);
        final TextView tuner_diff = findViewById(R.id.tuner_diff);


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
//                            current_hertz.setText("" + String.format("%.1f", inputHertz));
                            NearestNote nearestNote = getNearestNote(inputHertz);
                            int diff = (int) (inputHertz - nearestNote.hertz);
                            expected_note.setText("" + nearestNote.note);
                            tuner_diff.setText("" + diff);
                            if (Math.abs(diff) <= 1) {
                                tuner_diff.setTextColor(Color.GREEN);
                            } else if (Math.abs(diff) <= 3) {
                                tuner_diff.setTextColor(Color.YELLOW);
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
    }

    public static NearestNote getNearestNote(double inputHertz) {
        NearestNote nearest_note;
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

        return nearest_note;
    }
}
