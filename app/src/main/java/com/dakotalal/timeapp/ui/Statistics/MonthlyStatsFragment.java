package com.dakotalal.timeapp.ui.Statistics;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonthlyStatsFragment extends IntervalStatsFragment {
    ViewPager2 viewPager;
    TimeViewModel viewModel;
    public MonthlyStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interval_stats, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = requireView().findViewById(R.id.daily_stats_view_pager);
        viewPager.setAdapter(createStatsCollectionAdapter());
        viewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        TabLayout tabLayout = requireView().findViewById(R.id.daily_stats_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_AUTO);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();

        // get the days and insert them into the adapter
        viewModel.getAllDays().observe(requireActivity(), new Observer<List<Day>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<Day> days) {
                List<PieStatFragment> fragments = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                Day d = days.get(days.size()-1);
                LocalDate date = d.getDate();
                int currentYear = LocalDate.now().getYear();
                Month currentMonth = date.getMonth();
                fragments.add(PieStatFragment.newInstance(LocalDate.of(date.getYear(), currentMonth, 1).toEpochDay(), date.toEpochDay(), currentMonth.name() + " " + date.getYear()));
                labels.add(currentMonth.name());
                // create a PieStatsFragment for each month that has at least one timelog day
                for (int i = days.size() - 1; i >= 0; i--) {
                    d = days.get(i);
                    date = d.getDate();
                    Month month = date.getMonth();
                    if (!month.equals(currentMonth)) {    // if this day is from an earlier month, add all days of the month
                        currentMonth = month;
                        fragments.add(PieStatFragment.newInstance(LocalDate.of(date.getYear(), currentMonth, 1).toEpochDay(), date.toEpochDay(), currentMonth.name() + " " + date.getYear()));
                        String label;
                        if (date.getYear() == currentYear) {
                            label = currentMonth.name();
                        } else {
                            label = currentMonth.name() + " " + date.getYear();
                        }
                        labels.add(label);
                    }
                }

                Collections.reverse(fragments);
                Collections.reverse(labels);
                adapter.setPieStatFragments(fragments);
                viewPager.setCurrentItem(fragments.size());
                adapter.notifyDataSetChanged();

                for (int i = 0; i < fragments.size(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        tab.setText(labels.get(i));
                    }
                }
            }
        });
    }
}