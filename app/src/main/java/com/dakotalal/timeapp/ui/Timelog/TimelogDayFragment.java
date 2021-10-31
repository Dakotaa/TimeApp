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

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Activity for filling Timeslots
 */
public class TimelogDayFragment extends Fragment implements TimeslotListAdapter.OnTimeslotListener {
    TimeslotListAdapter adapter;
    public static String ARG_DAY_TIMESTAMP = "timestamp";
    public TextView score;
    public TimeViewModel timeViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timelog_day, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        LocalDate date;
        if (args == null) {
            //Log.d("TimelogDayFragment", "args null");
            date = LocalDate.now();
        } else {
            long timestamp = args.getLong(ARG_DAY_TIMESTAMP);
            //Log.d("TimelogDayFragment", "timestamp: "  + timestamp);
            date = LocalDate.ofEpochDay(timestamp);
        }

        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        score = requireView().findViewById(R.id.timelog_day_score);

        // Format the header
        TextView header = requireView().findViewById(R.id.timelog_day_header);
        String dayOfWeek = date.getDayOfWeek().toString();
        String month = date.getMonth().toString();
        String dayNumber = Integer.toString(date.getDayOfMonth());
        String year = Integer.toString(date.getYear());
        String dateText = MessageFormat.format("{0} {1} {2}, {3}", dayOfWeek, month, dayNumber, year);
        header.setText(dateText);

        // Setup the adapter for the recyclerview to display the timeslots for this day
        adapter = new TimeslotListAdapter(getActivity(), TimelogDayFragment.this, timeViewModel);
        RecyclerView recyclerView = requireView().findViewById(R.id.fragment_timeslots);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Observe the activities.
        timeViewModel.getAllTimeActivities().observe(requireActivity(), timeActivities -> adapter.updateTimeActivities());

        // Observe the timeslots for this day, so that they may be updated in real time.
        timeViewModel.getDayTimeslots(date).observe(requireActivity(), timeslots -> {
            adapter.setTimeslots(timeslots);

            double scoreVal = 0;
            for (Timeslot t : timeslots) {
                if (t.getActivityLabel() != null) {
                    scoreVal += timeViewModel.getTimeActivityScore(t.getActivityLabel()) / 2.0;
                }
            }
            TimelogDayFragment.this.score.setText("Score: " + scoreVal);

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