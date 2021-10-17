package com.dakotalal.timeapp.ui.Timelog;

import android.os.Bundle;

import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TimelogDayCollectionAdapter extends FragmentStateAdapter {

    public List<TimelogDayFragment> days;

    public TimelogDayCollectionAdapter(FragmentActivity fragmentActivity, TimeViewModel viewModel) {
        super(fragmentActivity);
        days = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return days.get(position);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setDayFragments(List<TimelogDayFragment> days) {
        this.days = days;
    }

    void updateTimelogDays() {
        notifyDataSetChanged();
    }
}
