package com.example.tuner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int MIC_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        final ImageButton googlebtn = findViewById(R.id.googlebtn);
        final ImageButton info = findViewById(R.id.info);
        final ImageButton guitarTuner = findViewById(R.id.GuitarTuner);
        final Context context = this;
=======
        findViewById(R.id.sign_in_button).setOnClickListener(v ->{ signIn(); });
        final ImageButton info = findViewById(R.id.info);
        final ImageButton guitarTuner = findViewById(R.id.GuitarTuner);
        final Context context = this;
        mAuth = FirebaseAuth.getInstance();
>>>>>>> parent of 7045264 (deleted LoginPage.java and activity_login_page.xml)

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicPermission();
            Toast.makeText(MainActivity.this,"You must grant this permission!",
                    Toast.LENGTH_LONG).show();
        }


        info.setOnClickListener(v ->{
            Intent intent = new Intent(context, information.class);
            startActivity(intent);
        });

<<<<<<< HEAD
        googlebtn.setOnClickListener(v ->{
                Intent intent = new Intent(context, LoginPage.class);
                startActivity(intent);
            });
=======
//        sign_in_button.setOnClickListener(v ->{
//            signIn();
//        });
>>>>>>> parent of 7045264 (deleted LoginPage.java and activity_login_page.xml)



        guitarTuner.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, GuitarTuner.class);
                startActivity(intent);
            } else {
                requestMicPermission();
                Toast.makeText(MainActivity.this,"You must grant this permission!",
                        Toast.LENGTH_LONG).show();
            }
        });
}
<<<<<<< HEAD
=======
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_in_button:
//                signIn();
//                break;
//            // ...
//        }
//    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

>>>>>>> parent of 7045264 (deleted LoginPage.java and activity_login_page.xml)

//    #### need to update strings for messages
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
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
