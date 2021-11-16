package com.dakotalal.timeapp.ui.Statistics;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatsGroupCollectionAdapter extends FragmentStateAdapter {

    public List<IntervalStatsFragment> intervalStatsFragments;

    public StatsGroupCollectionAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        intervalStatsFragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return intervalStatsFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return intervalStatsFragments.size();
    }

    public void setIntervalStatsFragments(List<IntervalStatsFragment> intervalStatsFragments) {
        this.intervalStatsFragments = intervalStatsFragments;
    }
}
