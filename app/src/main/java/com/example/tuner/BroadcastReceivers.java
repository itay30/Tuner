package com.example.tuner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class BroadcastReceivers extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String toastMessage = "";
            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    toastMessage = "Power connected!";
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    toastMessage = "Power disconnected!";
                    break;
                case Intent.ACTION_BATTERY_LOW:
                    toastMessage = "Battery percentage low!";
                    simpleAlert(context, toastMessage);
                    break;
                case Intent.ACTION_HEADSET_PLUG:
                    toastMessage = "audio is plugged or unplugged from the device!";
                    break;
            }
            if(!toastMessage.isEmpty())
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void simpleAlert(Context context, String massage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(massage);
        builder.setMessage("Your Battery percentage is EXTREMELY LOW,\nplease connect your phone to a power source ASAP");
        builder.setCancelable(false);
        builder.setNeutralButton("OK", (dialog, which) ->
        {dialog.dismiss();});
        builder.create();
        builder.show();
    }
}
