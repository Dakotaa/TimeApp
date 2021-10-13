package com.dakotalal.timeapp.ui.TimeActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TimeActivityListActivity extends AppCompatActivity implements TimeActivityListAdapter.OnTimeActivityListener{

    private AppBarConfiguration appBarConfiguration;
    private TimeViewModel timeViewModel;
    TimeActivityListAdapter adapter;
    public static final int NEW_TIME_ACTIVITY_ACTIVITY_REQUEST_CODE = 1;
    private FloatingActionButton fabCreate;
    private FloatingActionButton fabDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_activity_list);

        // initialize the recycler adapter and view to list the activities
        adapter = new TimeActivityListAdapter(this, this);
        RecyclerView recyclerView = findViewById(R.id.activity_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the FAB to launch the activity to create TimeActivities
        fabCreate = findViewById(R.id.fab_add_activity);
        fabCreate.setOnClickListener(view -> {
            Intent intent = new Intent(TimeActivityListActivity.this, NewTimeActivityActivity.class);
            startActivityForResult(intent, NEW_TIME_ACTIVITY_ACTIVITY_REQUEST_CODE);
        });

        // initialize the FAB to launch the activity to create TimeActivities
        fabDelete= findViewById(R.id.fab_delete_activties);
        fabDelete.setOnClickListener(view -> {
            timeViewModel.deleteAllTimeActivities();
        });

        // setup and initialize the view model
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        // Observe the list of activities
        timeViewModel.getAllTimeActivities().observe(this, new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(@Nullable final List<TimeActivity> timeActivities) {
                // Update the cached copy of the activities in the adapter.
                adapter.setTimeActivities(timeActivities);
            }
        });
    }

    /**
     * Handles the result received from the creation of a new TimeActivity
     * @param requestCode 1 for new TimeActivity
     * @param resultCode only handles RESULT_OK
     * @param data Should contain a string for the activity label, and an int for the colour
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TIME_ACTIVITY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            TimeActivity timeActivity = new TimeActivity(
                    data.getStringExtra(NewTimeActivityActivity.EXTRA_REPLY_LABEL),
                    data.getIntExtra(NewTimeActivityActivity.EXTRA_REPLY_COLOUR, 0));
            timeViewModel.insertTimeActivity(timeActivity);
        } else {
            Toast.makeText(getApplicationContext(), R.string.activity_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTimeActivityClick(int position) {
    }
}