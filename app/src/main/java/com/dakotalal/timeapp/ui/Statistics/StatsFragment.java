package com.dakotalal.timeapp.ui.Statistics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.ui.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends BaseFragment {

    ViewPager2 viewPager;
    StatsCollectionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = requireView().findViewById(R.id.stats_view_pager);
        viewPager.setAdapter(createStatsCollectionAdapter());

        TabLayout tabLayout = requireView().findViewById(R.id.stats_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_AUTO);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();

        List<PieStatFragment> fragments = new ArrayList<>();
        fragments.add(PieStatFragment.newInstance(1L, LocalDate.now().toEpochDay(), "All Time"));
        fragments.add(PieStatFragment.newInstance(LocalDate.now().minusDays(30).toEpochDay(), LocalDate.now().toEpochDay(), "Last 30 Days"));
        fragments.add(PieStatFragment.newInstance(LocalDate.now().minusDays(7).toEpochDay(), LocalDate.now().toEpochDay(), "Last 7 Days"));
        fragments.add(PieStatFragment.newInstance(LocalDate.now().toEpochDay(), LocalDate.now().toEpochDay(), "Today"));

        adapter.setPieStatFragments(fragments);


        adapter.notifyDataSetChanged();

        // Set the tab labels to the date
        for (int i = 0; i < fragments.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            PieStatFragment fragment = fragments.get(i);
            if (tab != null && fragment != null) {
                Log.d("StatsFragment", "label: " + fragment.getLabel());
                tab.setText(fragment.getLabel());
            }
        }

        viewPager.setCurrentItem(fragments.size());

        adapter.notifyDataSetChanged();
    }

    private StatsCollectionAdapter createStatsCollectionAdapter() {
        adapter = new StatsCollectionAdapter(getActivity());
        return adapter;
    }
}