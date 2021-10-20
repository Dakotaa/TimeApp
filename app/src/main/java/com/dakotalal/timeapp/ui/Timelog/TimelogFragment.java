package com.dakotalal.timeapp.ui.Timelog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.ui.BaseFragment;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelogFragment extends BaseFragment {

    ViewPager2 viewPager;
    TimelogDayCollectionAdapter adapter;
    private TimeViewModel timeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timelog, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        viewPager = requireView().findViewById(R.id.timelog_view_pager);
        viewPager.setAdapter(createTimelogDayCollectionAdapter());

        TabLayout tabLayout = requireView().findViewById(R.id.timelog_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();

        // Ensure there's a day created for today
        timeViewModel.createToday();
        timeViewModel.createDay(LocalDate.now().minusDays(1));
        // TODO: Create previous days that were missed

        timeViewModel.getAllDays().observe(requireActivity(), new Observer<List<Day>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<Day> days) {
                List<TimelogDayFragment> fragments = new ArrayList<>();
                for (int i = 0; i < days.size(); i++) {
                    TimelogDayFragment f = new TimelogDayFragment();
                    Bundle args = new Bundle();
                    Day day = days.get(i);
                    args.putLong(TimelogDayFragment.ARG_DAY_TIMESTAMP, day.getDate().toEpochDay());
                    f.setArguments(args);
                    fragments.add(f);
                }
                adapter.setDayFragments(fragments);
                adapter.notifyDataSetChanged();

                // Set the tab labels to the date
                LocalDate now = LocalDate.now();
                for (int i = 0; i < days.size(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    Day date = days.get(i);
                    if (tab != null && date != null) {
                        LocalDate d = date.getDate();
                        String title;
                        String year = Integer.toString(d.getYear());
                        String month = Integer.toString(d.getMonthValue());
                        String day = Integer.toString(d.getDayOfMonth());
                        // Show the day of the week for the past 7 days
                        if (d.isAfter(LocalDate.now().minusDays(7))) {
                            String dayOfWeek = d.getDayOfWeek().toString();
                            if (d.equals(LocalDate.now())) {
                                title = MessageFormat.format("Today ({0}/{1}/{2})", day, month, year);
                            } else if (d.equals(LocalDate.now().minusDays(1))) {
                                title = MessageFormat.format("Yesterday ({0}/{1}/{2})", day, month, year);
                            } else {
                                title = MessageFormat.format("{0} ({1}/{2}/{3})", dayOfWeek, day, month, year);
                            }
                        } else { // show only the date for anything further than a week back
                            title = MessageFormat.format("{0}/{1}/{2}", day, month, year);
                        }
                        tab.setText(title);
                    }
                }
                viewPager.setCurrentItem(fragments.size());

            }
        });
    }

    private TimelogDayCollectionAdapter createTimelogDayCollectionAdapter() {
        adapter = new TimelogDayCollectionAdapter(getActivity(), timeViewModel);
        return adapter;
    }
}