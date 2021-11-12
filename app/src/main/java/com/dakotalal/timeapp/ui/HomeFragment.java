package com.dakotalal.timeapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class HomeFragment extends Fragment {

    // local counts for empty timeslots
    private int totalEmpty, emptyToday, emptyThisWeek;
    private TextView textTotalEmpty, textSpecificEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalEmpty = 0;
        emptyToday = 0;
        emptyThisWeek = 0;
        long dayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long weekStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(7).toEpochSecond();
        textTotalEmpty = view.findViewById(R.id.timeslotsTotal);
        textSpecificEmpty = view.findViewById(R.id.timeslotsSpecific);

        TimeViewModel timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        timeViewModel.getEmptyTimeslotCountSince(0).observe(requireActivity(), count -> {
            totalEmpty = count;
            updateCounts();
        });
        timeViewModel.getEmptyTimeslotCountSince(dayStart).observe(requireActivity(), count -> {
            emptyToday = count;
            updateCounts();
        });
        timeViewModel.getEmptyTimeslotCountSince(weekStart).observe(requireActivity(), count -> {
            emptyThisWeek = count;
            updateCounts();
        });
    }

    public void updateCounts() {
        if (totalEmpty == 0) {
            textTotalEmpty.setText("All timeslots filled :)");
            textSpecificEmpty.setVisibility(View.INVISIBLE);
        } else {
            textTotalEmpty.setText("Unlogged Timeslots: " + totalEmpty);
            String specific = "";
            if (emptyToday > 0) {
                specific = emptyToday + " Today";
            }
            if (emptyThisWeek > 0) {
                specific += "\n" + emptyThisWeek + " This Week";
            }
            if (specific.isEmpty()) {
                specific = totalEmpty + " Before This Week";
            }
            textSpecificEmpty.setText(specific);
            textSpecificEmpty.setVisibility(View.VISIBLE);
        }

    }
}