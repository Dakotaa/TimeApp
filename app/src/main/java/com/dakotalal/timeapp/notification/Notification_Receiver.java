package com.dakotalal.timeapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dakotalal.timeapp.ui.MainActivity;

import androidx.core.app.NotificationCompat;

public class Notification_Receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,55,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (intent.getAction().equals("TIMELOG_NOTIFICATION")) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Test notification")
                    .setContentText("Notification text")
                    .setAutoCancel(true);
            notificationManager.notify(55,builder.build());
            Log.d("Notify", "Alarm"); //Optional, used for debuging.
        }
    }
}
