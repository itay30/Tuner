package com.example.mytuner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private int MIC_PERMISSION_CODE = 1;
    final Context context = this;
//    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton guitarTuner = findViewById(R.id.GuitarTuner);
//        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

//        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        guitarTuner.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, TuningClass.class);
                startActivity(intent);
            } else {
                requestMicPermission();
                Toast.makeText(MainActivity.this,"You must grant this permission!",
                        Toast.LENGTH_LONG).show();
            }
        });


        //fAuth = FirebaseAuth.getInstance();

//        TextView welcomeTitle = findViewById(R.id.welcome_txt);
//            if(fAuth.getCurrentUser() != null)
//                welcomeTitle.setText("Welcome " + fAuth.getCurrentUser().getDisplayName());

//        final ImageButton signOutBtn = findViewById(R.id.sign_out_button);



//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(MainActivity.this,"You must grant this permission!",
//                    Toast.LENGTH_SHORT).show();
//            requestMicPermission();
//        }else{
//            Intent intent = new Intent(context, TuningClass.class);
//            startActivity(intent);
//        }

//        signOutBtn.setOnClickListener(v ->{
//            new AlertDialog.Builder(this)
//                    .setTitle("Sign out")
//                    .setMessage("Do you want to logout from this app?")
//                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if(fAuth.getCurrentUser() != null) {
//                                Toast.makeText(MainActivity.this, "You signed out " + fAuth.getCurrentUser().getDisplayName() ,
//                                        Toast.LENGTH_LONG).show();
//                                fAuth.signOut();
//                                Intent intent = new Intent(context, LoginPage.class);
//                                startActivity(intent);
//                            }
//                            else {
//                                Toast.makeText(MainActivity.this, "There is no users signed in",
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    })
//                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
//                    })
//                    .create().show();
//        });


    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if the user granted mic permission
//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(context, TuningClass.class);
//            startActivity(intent);
//        }else{
//            Toast.makeText(MainActivity.this, "You must grant this permission!",
//                    Toast.LENGTH_SHORT).show();
//            requestMicPermission();
//        }
//    }

//    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    int itemID = item.getItemId();
//
//                    if (itemID == R.id.home){}
////                    else if (itemID == R.id.info){
////                        Intent intent = new Intent(context, Information.class);
////                        startActivity(intent);}
//                    else if (itemID == R.id.tuner && ContextCompat.checkSelfPermission(MainActivity.this,
//                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
//                        Intent intent = new Intent(context, TuningClass.class);
//                        startActivity(intent);}
//                    else{
//                        requestMicPermission();
//                        Toast.makeText(MainActivity.this,"You must grant this permission!",
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MIC_PERMISSION_CODE)  {
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