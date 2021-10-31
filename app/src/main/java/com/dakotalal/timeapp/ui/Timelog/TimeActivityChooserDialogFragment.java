package com.dakotalal.timeapp.ui.Timelog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeActivityChooserDialogFragment extends DialogFragment implements TimeActivityListAdapter.OnTimeActivityListener {
    RecyclerView recyclerView;
    TimeActivityListAdapter adapter;
    TimeViewModel timeViewModel;
    Timeslot timeslot;

    public TimeActivityChooserDialogFragment(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.activity_select_time_activity, container, false);
        //getDialog().setTitle("Select Activity");
        recyclerView = v.findViewById(R.id.activity_list_recyclerview);
        adapter = new TimeActivityListAdapter(getActivity(), this, timeViewModel, false, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        // setup and initialize the view model
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        // Observe the list of activities
        timeViewModel.getMostCommonTimeActivities(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).observe(this, new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(@Nullable final List<TimeActivity> timeActivities) {
                // Update the cached copy of the activities in the adapter.
                adapter.setTimeActivities(timeActivities);
            }
        });
        return v;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeActivityClick(int position) {
        TimeActivity activity = adapter.getTimeActivityAtPosition(position);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String start = Instant.ofEpochSecond(timeslot.getTimeStart()).atZone(ZoneId.systemDefault()).format(formatter);
        timeslot.setActivityLabel(activity.getLabel());
        timeViewModel.updateTimeslot(timeslot);
        this.dismiss();
    }
}
