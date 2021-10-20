package com.dakotalal.timeapp.ui.Timelog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.List;

/**
 * Activity for filling Timeslots
 */
public class TimelogDayFragment extends Fragment implements TimeslotListAdapter.OnTimeslotListener {
    private TimeViewModel timeViewModel;
    private LocalDate date;
    TimeslotListAdapter adapter;
    public static String ARG_DAY_TIMESTAMP = "timestamp";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timelog_day, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            //Log.d("TimelogDayFragment", "args null");
            date = LocalDate.now();
        } else {
            long timestamp = args.getLong(ARG_DAY_TIMESTAMP);
            //Log.d("TimelogDayFragment", "timestamp: "  + timestamp);
            date = LocalDate.ofEpochDay(timestamp);
        }

        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        // Setup the adapater for the recyclerview to display the timeslots for this day
        adapter = new TimeslotListAdapter(getActivity(), TimelogDayFragment.this, timeViewModel);
        RecyclerView recyclerView = getView().findViewById(R.id.fragment_timeslots);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Observe the activities.
        timeViewModel.getAllTimeActivities().observe(getActivity(), new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(List<TimeActivity> timeActivities) {
                adapter.updateTimeActivities();
            }
        });

        // Observe the timeslots for this day, so that they may be updated in real time.
        timeViewModel.getDayTimeslots(date).observe(getActivity(), new Observer<List<Timeslot>>() {
            @Override
            public void onChanged(List<Timeslot> timeslots) {
                adapter.setTimeslots(timeslots);
                Log.d("TimelogActivity", "Got timeslots for day");
            }
        });
    }

    @Override
    public void onTimeslotClick(int position) {
        Timeslot timeslot = adapter.getTimeslotAtPosition(position);
        showTimeActivitySelectorDialog(timeslot);
    }


    public void showTimeActivitySelectorDialog(Timeslot timeslot) {
        TimeActivityChooserDialogFragment fragment = new TimeActivityChooserDialogFragment(timeslot);
        fragment.show(getActivity().getSupportFragmentManager(), "fragment");
    }
}