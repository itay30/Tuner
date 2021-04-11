package com.example.tuner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BroadcastReceivers myReceiver = new BroadcastReceivers();
    private int MIC_PERMISSION_CODE = 1;
    private FirebaseAuth fAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton signOutBtn = findViewById(R.id.sign_out_button);
        final ImageButton guitarTuner = findViewById(R.id.GuitarTuner);

        fAuth = FirebaseAuth.getInstance();
        context = this;

        TextView welcomeTitle = findViewById(R.id.welcome_txt);
        if(fAuth.getCurrentUser() != null)
            welcomeTitle.setText("Welcome " + fAuth.getCurrentUser().getDisplayName());

        setBroadcastReceiver();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this,"You must grant this permission!",
                    Toast.LENGTH_SHORT).show();
            requestMicPermission();
        }

        signOutBtn.setOnClickListener(this);
        guitarTuner.setOnClickListener(this);

    }

    private void setBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        // Register the receiver using the activity context.
        this.registerReceiver(myReceiver, filter);
    }

    private void requestMicPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because the tuner needs access to the microphone")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.GuitarTuner:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(context, GuitarTuner.class);
                    startActivity(intent);
                } else {
                    requestMicPermission();
                    Toast.makeText(MainActivity.this,"You must grant this permission!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.sign_out_button:
                new AlertDialog.Builder(this)
                        .setTitle("Sign out")
                        .setMessage("Do you want to logout from this app?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(fAuth.getCurrentUser() != null) {
                                    Toast.makeText(MainActivity.this, "You signed out " + fAuth.getCurrentUser().getDisplayName() ,
                                            Toast.LENGTH_LONG).show();
                                    fAuth.signOut();
                                    Intent intent = new Intent(context, LoginPage.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "There is no users signed in",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        })
                        .create().show();
                break;
            default:
                intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MIC_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}