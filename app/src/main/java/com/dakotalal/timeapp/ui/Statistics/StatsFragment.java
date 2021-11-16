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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends BaseFragment {

    ViewPager2 viewPager;
    StatsGroupCollectionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = requireView().findViewById(R.id.stats_view_pager);
        viewPager.setAdapter(createStatsGroupCollectionAdapter());
        viewPager.setUserInputEnabled(false);
        TabLayout tabLayout = requireView().findViewById(R.id.stats_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_AUTO);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();

        List<IntervalStatsFragment> fragments = new ArrayList<>();
        fragments.add(new AllTimeStatsFragment());
        fragments.add(new MonthlyStatsFragment());
        fragments.add(new WeeklyStatsFragment());
        fragments.add(new DailyStatsFragment());

        adapter.setIntervalStatsFragments(fragments);


        adapter.notifyDataSetChanged();

        // Set the tab labels to the date
//        for (int i = 0; i < fragments.size(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            PieStatFragment fragment = fragments.get(i);
//            if (tab != null && fragment != null) {
//                Log.d("StatsFragment", "label: " + fragment.getLabel());
//                tab.setText(fragment.getLabel());
//            }
//        }
        tabLayout.getTabAt(0).setText("All");
        tabLayout.getTabAt(1).setText("Monthly");
        tabLayout.getTabAt(2).setText("Weekly");
        tabLayout.getTabAt(3).setText("Daily");
//        viewPager.setCurrentItem(fragments.size());
//
//        adapter.notifyDataSetChanged();

        // showcase tutorial the first time the user opens this fragments
        new ShowcaseView.Builder(requireActivity())
                .setContentTitle("Your Statistics")
                .setContentText("As you use your timelog to record how you spend your time, your personalized statistic model is updated.\n\nYour statistics reflect how much time you spend doing each activity, at different intervals.")
                .setStyle(R.style.CustomShowcaseTheme)
                .singleShot(1)
                .build();
    }

    private StatsGroupCollectionAdapter createStatsGroupCollectionAdapter() {
        adapter = new StatsGroupCollectionAdapter(getActivity());
        return adapter;
    }
}