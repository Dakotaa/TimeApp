package com.dakotalal.timeapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.MainActivity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int numEmptyTimeslots = intent.getExtras().getInt("EMPTY_TIMESLOTS");
        deliverNotification(context, numEmptyTimeslots);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deliverNotification(Context context, int numEmptyTimeslots) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_timelog)
                .setContentTitle(numEmptyTimeslots + " Timeslot(s) Empty!")
                .setContentText("Tap to fill out today's empty timeslots.")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

//        if (numEmptyTimeslots == 1) {
//            Timeslot t = emptyTimeslots.get(0);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//            String start = Instant.ofEpochSecond(t.getTimeStart()).atZone(ZoneId.systemDefault()).format(formatter);
//            String end = Instant.ofEpochSecond(t.getTimeEnd()).atZone(ZoneId.systemDefault()).format(formatter);
//            builder.setContentTitle("Timeslot empty: " + start + " - " + end)
//                    .setContentText("Tap to open Timelog");
//            if (!mostCommonActivities.isEmpty()) {
//                for (int i = 1; i < mostCommonActivities.size(); i++) {
//                    if (i <= 3) {
//                        String label = mostCommonActivities.get(i).getLabel();
//                        Intent actionIntent = new Intent(context, NotificationActionService.class).setAction(label);
//                        PendingIntent actionPendingIntent = PendingIntent.getService(context, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        builder.addAction(R.id.nav_timelog, label, actionPendingIntent)
//                                .setContentText("Tap to open Timelog, or press one of the buttons below to enter an activity.");
//                    }
//                }
//            }
//        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}