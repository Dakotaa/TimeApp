package com.dakotalal.timeapp.ui.Statistics;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatsCollectionAdapter extends FragmentStateAdapter {

    public List<PieStatFragment> pieStatFragments;

    public StatsCollectionAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        pieStatFragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pieStatFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return pieStatFragments.size();
    }

    public void setPieStatFragments(List<PieStatFragment> pieStatFragments) {
        this.pieStatFragments = pieStatFragments;
    }
}
