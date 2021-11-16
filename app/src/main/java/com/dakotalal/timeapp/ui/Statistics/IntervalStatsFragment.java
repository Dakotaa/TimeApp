package com.dakotalal.timeapp.ui.Statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;

import androidx.fragment.app.Fragment;

public class IntervalStatsFragment extends Fragment {

    StatsCollectionAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interval_stats, container, false);
    }

    public StatsCollectionAdapter createStatsCollectionAdapter() {
        adapter = new StatsCollectionAdapter(getActivity());
        return adapter;
    }
}
