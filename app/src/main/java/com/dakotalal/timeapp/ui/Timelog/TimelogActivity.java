package com.dakotalal.timeapp.ui.Timelog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.time.LocalDate;
import java.util.List;

/**
 * Activity for filling Timeslots
 */
public class TimelogActivity extends AppCompatActivity implements TimeslotListAdapter.OnTimeslotListener {
    private TimeViewModel timeViewModel;
    TimeslotListAdapter adapter;
    List<TimeActivity> activities;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelog);
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        adapter = new TimeslotListAdapter(TimelogActivity.this, TimelogActivity.this, timeViewModel);
        RecyclerView recyclerView = findViewById(R.id.timeslot_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimelogActivity.this));

        timeViewModel.getAllTimeActivities().observe(this, new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(List<TimeActivity> timeActivities) {
                adapter.updateTimeActivities();
            }
        });

        timeViewModel.getCurrentDayTimeslots().observe(this, new Observer<List<Timeslot>>() {
            @Override
            public void onChanged(List<Timeslot> timeslots) {
                adapter.setTimeslots(timeslots);
                Log.d("TimelogActivity", "Got timeslots for day");
            }
        });

    }

    public void updateListAdapter() {
        adapter = new TimeslotListAdapter(TimelogActivity.this, TimelogActivity.this, timeViewModel);
        RecyclerView recyclerView = findViewById(R.id.timeslot_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimelogActivity.this));
    }

    @Override
    public void onTimeslotClick(int position) {
        Timeslot timeslot = adapter.getTimeslotAtPosition(position);
        showTimeActivitySelectorDialog(timeslot);
    }


    public void showTimeActivitySelectorDialog(Timeslot timeslot) {
        TimeActivityChooserDialogFragment fragment = new TimeActivityChooserDialogFragment(timeslot);
        fragment.show(getSupportFragmentManager(), "fragment");
    }
}