package com.dakotalal.timeapp.ui;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.dakotalal.timeapp.R;

import com.dakotalal.timeapp.notification.Notification_Receiver;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dakotalal.timeapp.databinding.ActivityMainBinding;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.util.Log;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NotificationManager notificationManager;
    // shared preference constants
    public static String PREFS = "com.timeapp.prefs";
    public static String PREFS_INTERVAL_LENGTH = "com.timeapp.prefs.interval_length";
    public static String PREFS_NAME = "com.timeapp.prefs.name";
    public static String PREFS_SETUP_COMPLETE = "com.timeapp.prefs.setup_complete";
    // notification constants
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    TimeViewModel viewModel;
    int emptyTimeslots;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        NavController navController = Navigation.findNavController(findViewById(R.id.nav_fragment));
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // if the user hasn't completed the setup, launch the setup activity
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean setupComplete = prefs.getBoolean(PREFS_SETUP_COMPLETE, false);
        if (!setupComplete) {
            launchFirstTimeSetup();
        }

        viewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        createNotificationChannel();

//        long startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//        viewModel.getEmptyTimeslotCountSince(startOfToday).observe(this, i -> {
//            emptyTimeslots = i;
//            deliverNotification(MainActivity.this);
//        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void launchFirstTimeSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Time log reminder",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription
                    ("Reminder every 15 minutes to fill timelog");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void deliverNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_timelog)
                .setContentTitle(emptyTimeslots + " Timeslots Empty!")
                .setContentText("Tap to fill out recent timelog intervals")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        viewModel.getMostCommonTimeActivities().observe(this, new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(List<TimeActivity> timeActivities) {
                if (timeActivities.size() >= 1) {
                    for (int i = 1; i < timeActivities.size(); i++) {
                        if (i <= 3) {
                            builder.addAction(R.id.nav_timelog, timeActivities.get(i).getLabel(), contentPendingIntent);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        }
                    }
                }
            }
        });

        Log.d("MainActivity", "Delivering notifciation");
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}