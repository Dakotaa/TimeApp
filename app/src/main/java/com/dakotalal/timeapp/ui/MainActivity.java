package com.dakotalal.timeapp.ui;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.dakotalal.timeapp.R;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dakotalal.timeapp.databinding.ActivityMainBinding;
import com.dakotalal.timeapp.notification.NotificationReceiver;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.os.SystemClock;
import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    // shared preference constants
    public static String PREFS = "com.timeapp.prefs";
    public static String PREFS_INTERVAL_LENGTH = "com.timeapp.prefs.interval_length";
    public static String PREFS_NAME = "com.timeapp.prefs.name";
    public static String PREFS_SETUP_COMPLETE = "com.timeapp.prefs.setup_complete";
    // notification constants
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    TimeViewModel viewModel;
    int numEmptyTimeslots;
    private List<Timeslot> emptyTimeslots;
    private List<TimeActivity> mostCommonActivities;

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

        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        notifyIntent.putExtra("EMPTY_TIMESLOTS", numEmptyTimeslots);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        numEmptyTimeslots = 0;

        mostCommonActivities = new ArrayList<>();

        viewModel.getEmptyTimeslotsToday().observe(this, timeslots -> {
            emptyTimeslots = timeslots;
            numEmptyTimeslots = timeslots.size();
            Intent newNotifyIntent = new Intent(this, NotificationReceiver.class);
            newNotifyIntent.putExtra("EMPTY_TIMESLOTS", numEmptyTimeslots);
            PendingIntent newNotifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, newNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            setAlarm(alarmManager, newNotifyPendingIntent);
            Log.d("Main", "empty: " + numEmptyTimeslots);
        });

        viewModel.getMostCommonTimeActivities().observe(this, timeActivities -> {
            this.mostCommonActivities = timeActivities;
        });

        setAlarm(alarmManager, notifyPendingIntent);
    }

    public void setAlarm(AlarmManager alarmManager, PendingIntent notifyPendingIntent) {
        if (alarmManager != null) {
            long repeatInterval = AlarmManager.INTERVAL_HOUR;
            long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime, repeatInterval, notifyPendingIntent);
        } else {
            alarmManager.cancel(notifyPendingIntent);
        }
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
                            "Timelog reminder",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription
                    ("Reminder to fill timelog");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }



    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            Log.d("MainActivity", "Received notification action: " + action);
        }
    }
}