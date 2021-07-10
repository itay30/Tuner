package com.example.mytuner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Information extends AppCompatActivity {
    private int MIC_PERMISSION_CODE = 1;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                    int itemID = item.getItemId();
//
//                    if (itemID == R.id.home){
//                        Intent intent = new Intent(context, MainActivity.class);
//                        startActivity(intent);}
////                    else if (itemID == R.id.info){}
//                    else if (itemID == R.id.tuner && ContextCompat.checkSelfPermission(Information.this,
//                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
//                        Intent intent = new Intent(context, TuningClass.class);
//                        startActivity(intent);}
//                    else{
//                        requestMicPermission();
//                        Toast.makeText(Information.this,"You must grant this permission!",
//                                Toast.LENGTH_SHORT).show();}
//                    return false;
//                }
//            };

    private void requestMicPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because the tuner needs access to the microphone")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Information.this,
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MIC_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TuningClass.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}