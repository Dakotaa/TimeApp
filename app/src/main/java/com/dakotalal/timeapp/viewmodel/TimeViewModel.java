package com.dakotalal.timeapp.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.dakotalal.timeapp.repository.TimeRepository;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.MainActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class TimeViewModel extends AndroidViewModel {
    private TimeRepository timeRepository;
    private LiveData<List<TimeActivity>> allTimeActivities;
    private LiveData<List<Day>> allDays;
    private LiveData<List<Timeslot>> currentDayTimeslots;
    private Hashtable<String, TimeActivity> timeActivitiesByLabel;
    private LiveData<List<TimeActivity>> mostCommonActivities;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TimeViewModel(Application application) {
        super(application);
        timeRepository = new TimeRepository(application);
        getAllDays();
    }

    public Hashtable<String, TimeActivity> getTimeActivitiesByLabel() {
        // hashtable may not be initialized, because it needs to wait on the data for allTimeActivities
        if (timeActivitiesByLabel == null) {
            // keep all the activities in a hashtable, keyed by label, so they can be retrieved from just the label name
            timeActivitiesByLabel = new Hashtable<>();
            if (allTimeActivities.getValue() != null) {
                for (TimeActivity t : allTimeActivities.getValue()) {
                    timeActivitiesByLabel.put(t.getLabel(), t);
                }
            }
        }
        return timeActivitiesByLabel;
    }

    public void insertTimeActivity(TimeActivity timeActivity) {
        timeRepository.insertTimeActivity(timeActivity);
    }

    public void deleteTimeActivity(TimeActivity timeActivity) {
        timeRepository.deleteTimeActivity(timeActivity);
    }

    public void updateTimeActivity(TimeActivity timeActivity) {
        timeRepository.updateTimeActivity(timeActivity);
    }

    public void updateTimeslot(Timeslot timeslot) {
        timeRepository.updateTimeslot(timeslot);
    }

    public LiveData<List<Timeslot>> getTimeslots(long start, long finish) {
        return timeRepository.getTimeslots(start, finish);
    }

    public TimeActivity getTimeActivity(String timeActivityLabel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return timeActivitiesByLabel.getOrDefault(timeActivityLabel, null);
        } else return null;
    }


    public int getTimeActivityScore(String timeActivityLabel) {
        TimeActivity activity = getTimeActivitiesByLabel().get(timeActivityLabel);
        if (activity != null) return activity.getProductivity();
        return -1;
    }

    public int getTimeActivityColour(String timeActivityLabel) {
        TimeActivity activity = getTimeActivitiesByLabel().get(timeActivityLabel);
        if (activity != null) return activity.getColour();
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> getDayTimeslots(LocalDate date) {
        if (currentDayTimeslots == null) {
            currentDayTimeslots = new MutableLiveData<>();
            currentDayTimeslots = timeRepository.retrieveDay(date);
        }
        return currentDayTimeslots;
    }

    public LiveData<List<TimeActivity>> getAllTimeActivities() {
        if (allTimeActivities == null) {
            allTimeActivities = new MutableLiveData<>();
            allTimeActivities = timeRepository.getAllTimeActivities();
        }
        return this.allTimeActivities;
    }

    public LiveData<List<TimeActivity>> getMostCommonTimeActivitiesSince(long since) {
        return timeRepository.getMostCommonTimeActivities(since);
    }

    public LiveData<List<TimeActivity>> getMostCommonTimeActivities() {
        if (mostCommonActivities == null) {
            mostCommonActivities = new MutableLiveData<>();
            mostCommonActivities = timeRepository.getMostCommonTimeActivities(0);
        }
        return mostCommonActivities;
    }

    public LiveData<List<Day>> getAllDays() {
        if (allDays == null) {
            allDays = new MutableLiveData<>();
            allDays = timeRepository.getAllDays();
        }
        return allDays;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createToday() {
        timeRepository.createToday();
    }


    public LiveData<Integer> getActivityCount(String activityLabel, long start, long end) {
        return timeRepository.getActivityCount(activityLabel, start, end);
    }

    public LiveData<Integer> getEmptyTimeslotCountSince(long start) {
        long end = System.currentTimeMillis() / 1000;
        return timeRepository.getEmptyTimeslotCountSince(start, end);
    }


    /**
     * Creates the timeslots for a day and inserts them into the database
     * @param date the date to create the timeslots for
     * @return The created timeslots
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> createDay(LocalDate date) {
        return timeRepository.createDay(date);
    }
}
