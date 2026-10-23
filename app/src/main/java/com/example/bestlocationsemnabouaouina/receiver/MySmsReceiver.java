package com.example.bestlocationsemnabouaouina.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bestlocationsemnabouaouina.MapsActivity;
import com.example.bestlocationsemnabouaouina.Utilities;
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
                                    if (location!=null) {
                                        double longitude = location.getLongitude();
                                        double latitude = location.getLatitude();

                                        SmsManager manager = SmsManager.getDefault();
                                        manager.sendTextMessage(phoneNumber,
                                                null,
                                                Utilities.Response_SMS + "#" + longitude + "#" + latitude,
                                                null,
                                                null);
                                    }
                                } else {
                                    Toast.makeText(context, "pas de position actuel", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    if (messageBody.contains(Utilities.REQUEST_SMS))
                    {
                        String[] t=messageBody.split("#");
                        String longitude=t[1];
                        String latitude=t[2];

                        NotificationCompat.Builder mynotif=new NotificationCompat.Builder(context,Utilities.CHANNEL_ID);
                        mynotif.setContentTitle("Position reçu");
                        mynotif.setContentTitle("Appuiyer pour voir surMAP!");
                        mynotif.setAutoCancel(true);
                        mynotif.setSmallIcon(android.R.drawable.ic_dialog_map);

                        //action
                        Intent i=new Intent(context, MapsActivity.class);
                        i.putExtra("longitude",longitude);
                        i.putExtra("latitude",latitude);

                        PendingIntent pi=PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_MUTABLE);
                        mynotif.setContentIntent(pi);

                        //lancemant de la notif

                        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
                        //creation de canal

                        NotificationChannel canal= new NotificationChannel(Utilities.CHANNEL_ID,
                                "canal pour notre app best location",
                                 NotificationManager.IMPORTANCE_DEFAULT);
                                 notificationManager.createNotificationChannel(canal);

                                notificationManager.notify(0, mynotif.build() );


                    }
                }
            }
        }
    }
}