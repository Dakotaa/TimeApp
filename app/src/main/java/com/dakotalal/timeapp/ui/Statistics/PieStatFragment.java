package com.dakotalal.timeapp.ui.Statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PieStatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieStatFragment extends Fragment {
    private static final String ARG_TIMESTAMP_START = "timestampStart";
    private static final String ARG_TIMESTAMP_END = "timestampEnd";
    private static final String ARG_LABEL = "label";

    private long timestampStart;
    private long timestampEnd;
    private String label;

    ViewPager2 viewPager;
    private TimeViewModel timeViewModel;
    private long startTime;
    private long endTime;
    private ArrayList<Integer> colours;
    private ValueFormatter formatter;
    TextView header;
    PieChart chart;
    List<PieEntry> entries;

    public PieStatFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param timestampStart the first day in the range of stats
     * @param timestampEnd the last day in the range of stats
     * @return A new instance of fragment StatisticsFragment.
     */
    public static PieStatFragment newInstance(long timestampStart, long timestampEnd, String label) {
        PieStatFragment fragment = new PieStatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TIMESTAMP_START, timestampStart);
        args.putLong(ARG_TIMESTAMP_END, timestampEnd);
        args.putString(ARG_LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            timestampStart = getArguments().getLong(ARG_TIMESTAMP_START);
            timestampEnd = getArguments().getLong(ARG_TIMESTAMP_END);
            label = getArguments().getString(ARG_LABEL);
        } else {    // default to today if no args are found
            timestampStart = LocalDate.now().toEpochDay();
            timestampEnd = LocalDate.now().toEpochDay();
            label = "Today";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_piestat, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        viewPager = requireView().findViewById(R.id.timelog_view_pager);

        this.startTime = LocalDate.ofEpochDay(timestampStart).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        // start of day is used, so to include the last day, a day must be added
        this.endTime = LocalDate.ofEpochDay(timestampEnd).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        colours = new ArrayList<>();

        header = requireView().findViewById(R.id.piestats_header);
        header.setText(label);

        chart = requireView().findViewById(R.id.stats_chart_view);
        chart.getLegend().setWordWrapEnabled(true);
        chart.setDrawRoundedSlices(false);
        chart.setEntryLabelColor(Color.BLACK);
        entries = new ArrayList<>();

        formatter = new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String rounded = String.format("%.1f", value);
                return rounded + " hours";

            }
        };

        // This is a horrible approach, but I just wanted to get it working first.
        // TODO Improve timeslot count retrieval
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
                                if (count > 0) {
                                    entries.add(new PieEntry((float) count / 2, a.getLabel()));
                                    colours.add(a.getColour());
                                    updateChart();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public String getLabel() {
        return this.label;
    }

    public void updateChart() {
        PieDataSet dataSet = new PieDataSet(entries, "Activities");
        dataSet.setValueFormatter(formatter);
        dataSet.setColors(colours);
        PieData d = new PieData(dataSet);
        d.setValueFormatter(formatter);
        d.setValueTextSize(10.0f);
        chart.setData(d);
        chart.invalidate();
    }
}