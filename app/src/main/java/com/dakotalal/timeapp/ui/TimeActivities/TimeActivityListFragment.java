package com.dakotalal.timeapp.ui.TimeActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TimeActivityListFragment extends Fragment implements TimeActivityListAdapter.OnTimeActivityListener{

    private AppBarConfiguration appBarConfiguration;
    private TimeViewModel timeViewModel;
    TimeActivityListAdapter adapter;
    public static final int CREATOR_FRAGMENT = 1;

    private FloatingActionButton fabCreate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_activity_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // setup and initialize the view model
        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);
        // initialize the recycler adapter and view to list the activities
        adapter = new TimeActivityListAdapter(getActivity(), this, timeViewModel, true);

        RecyclerView recyclerView = getView().findViewById(R.id.activity_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initialize the FAB to launch the activity to create TimeActivities
        fabCreate = getView().findViewById(R.id.fab_add_activity);
        fabCreate.setOnClickListener(view1 -> {
            DialogFragment frag = new CreateTimeActivityDialogFragment();
            frag.setTargetFragment(this, CREATOR_FRAGMENT);
            frag.show(getFragmentManager().beginTransaction(), "CreateActivityDialog");
        });

        // Observe the list of activities
        timeViewModel.getAllTimeActivities().observe(getActivity(), new Observer<List<TimeActivity>>() {
            @Override
            public void onChanged(@Nullable final List<TimeActivity> timeActivities) {
                // Update the cached copy of the activities in the adapter.
                adapter.setTimeActivities(timeActivities);
            }
        });
    }

    /**
     * Handles the result received from the creation of a new TimeActivity
     * @param requestCode 1 for new TimeActivity
     * @param resultCode only handles RESULT_OK
     * @param data Should contain a string for the activity label, and an int for the colour
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATOR_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            TimeActivity timeActivity = new TimeActivity(bundle.getString("label", " "), bundle.getInt("colour", 0), bundle.getInt("score"));
            timeViewModel.insertTimeActivity(timeActivity);
        } else {
            Toast.makeText(getContext(), R.string.activity_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTimeActivityClick(int position) {
    }
}