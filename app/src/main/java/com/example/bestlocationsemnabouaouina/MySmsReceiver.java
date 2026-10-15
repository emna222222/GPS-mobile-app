package com.example.bestlocationsemnabouaouina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MySmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "sms bien reçu", Toast.LENGTH_SHORT).show();
        String messageBody, phoneNumber;

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context,
                                    "Message : " + messageBody + " Reçu de la part de ; " + phoneNumber,
                                    Toast.LENGTH_LONG)
                            .show();

                    if (messageBody.contains(Utilities.REQUEST_SMS)) {
                        // recuperer gps position
                        FusedLocationProviderClient mClient = LocationServices.getFusedLocationProviderClient(context);

                        mClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();

                                    SmsManager manager = SmsManager.getDefault();
                                    manager.sendTextMessage(phoneNumber,
                                            null,
                                            Utilities.Response_SMS + "#" + longitude + "#" + latitude,
                                            null,
                                            null);
                                } else {
                                    Toast.makeText(context, "pas de position actuel", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}