package com.dakotalal.timeapp.ui.Timelog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelogActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    TimelogDayCollectionAdapter adapter;
    private TimeViewModel timeViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelog);
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        viewPager = findViewById(R.id.timelog_view_pager);
        viewPager.setAdapter(createTimelogDayCollectionAdapter());

        TabLayout tabLayout = findViewById(R.id.timelog_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();

        // Ensure there's a day created for today
        timeViewModel.createToday();

        timeViewModel.getAllDays().observe(this, new Observer<List<Day>>() {
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

                    if (tabLayout.getTabAt(i) != null) {
                        tabLayout.getTabAt(i).setText(day.getDate().toString());
                    }
                }
                adapter.setDayFragments(fragments);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(fragments.size());

            }
        });
    }

    private TimelogDayCollectionAdapter createTimelogDayCollectionAdapter() {
        adapter = new TimelogDayCollectionAdapter(this, timeViewModel);
        return adapter;
    }
}