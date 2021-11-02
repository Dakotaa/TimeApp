package com.dakotalal.timeapp.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.dakotalal.timeapp.R;

import com.dakotalal.timeapp.notification.Notification_Receiver;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListFragment;
import com.dakotalal.timeapp.ui.Timelog.TimelogFragment;


import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dakotalal.timeapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        NavController navController = Navigation.findNavController(findViewById(R.id.nav_fragment));
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // setup repeating notification for timelog
        Calendar calendar = Calendar.getInstance();
        Log.d("MainActivity", calendar.getTime().toString());
        Intent intent = new Intent(getApplicationContext(), Notification_Receiver.class);
        intent.setAction("TIMELOG_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 55, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),30000,pendingIntent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}