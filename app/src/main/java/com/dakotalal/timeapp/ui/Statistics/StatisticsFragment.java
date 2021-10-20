package com.dakotalal.timeapp.ui.Statistics;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ViewPager2 viewPager;
    private TimeViewModel timeViewModel;
    private long startTime;
    private long endTime;
    private ArrayList<Integer> colours;
    PieChart chart;
    List<PieEntry> entries;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        viewPager = requireView().findViewById(R.id.timelog_view_pager);

        /*
        TabLayout tabLayout = requireView().findViewById(R.id.timelog_tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(" " + (position + 1))
        ).attach();
         */

        this.startTime = LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        this.endTime = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        colours = new ArrayList<>();

        chart = requireView().findViewById(R.id.stats_chart_view);
        chart.getLegend().setWordWrapEnabled(true);
        entries = new ArrayList<>();

        timeViewModel.getAllTimeActivities().observe(requireActivity(), new Observer<List<TimeActivity>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<TimeActivity> timeActivities) {
                for (TimeActivity a : timeActivities) {
                    if (!a.getLabel().isEmpty()) {
                        timeViewModel.getActivityCount(a.getLabel(), startTime, endTime).observe(requireActivity(), new Observer<Integer>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onChanged(Integer count) {
                                //Log.d("StatisticsFragment", a.getLabel() + " " + count);
                                entries.add(new PieEntry(count, a.getLabel()));
                                colours.add(a.getColour());
                                updateChart();
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateChart() {
        Log.d("StatisticsFragment", "Updating chart");
        PieDataSet dataSet = new PieDataSet(entries, "Activities");
        dataSet.setColors(colours);
        PieData d = new PieData(dataSet);
        d.setValueTextSize(10.0f);
        chart.setData(d);
        chart.invalidate();
    }
}