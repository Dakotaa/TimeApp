package com.dakotalal.timeapp.ui.Timelog;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeActivityChooserDialogFragment extends BottomSheetDialogFragment implements TimeActivityListAdapter.OnTimeActivityListener {
    RecyclerView recyclerView;
    TimeActivityListAdapter adapter;
    TimeViewModel timeViewModel;
    List<Timeslot> timeslots;

    public TimeActivityChooserDialogFragment(ArrayList<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.dialog_select_time_activity, container, false);
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
        for (Timeslot t : timeslots) {
            t.setActivityLabel(activity.getLabel());
            timeViewModel.updateTimeslot(t);
        }
        this.dismiss();
    }
}
