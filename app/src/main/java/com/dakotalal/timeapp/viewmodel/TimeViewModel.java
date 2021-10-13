package com.dakotalal.timeapp.viewmodel;

import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.dakotalal.timeapp.repository.TimeRepository;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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
    private LiveData<List<Timeslot>> dayTimeslots;
    private Hashtable<String, TimeActivity> timeActivitiesByLabel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TimeViewModel(Application application) {
        super(application);
        timeRepository = new TimeRepository(application);
        allTimeActivities = timeRepository.getAllTimeActivities();
    }

    public LiveData<List<TimeActivity>> getAllTimeActivities() {
        return this.allTimeActivities;
    }

    public LiveData<List<Timeslot>> getDayTimeslots() {
        return this.dayTimeslots;
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

    public int getTimeActivityColour(String timeActivityLabel) {
        TimeActivity activity = getTimeActivitiesByLabel().get(timeActivityLabel);
        //Log.d("TimeViewModel", "activity: " + activity.toString());
        if (activity != null) return activity.getColour();
        return -1;
    }

    public void deleteAllTimeActivities() {
        timeRepository.deleteAllTimeActivities();
    }

    /**
     * Gets a list of the Timeslots that make up the given day, in the user's timezone
     * There are no timeslots for the given day, the day is created
     * @param date a localdate representing the day to retrieve
     * @return a list of all Timeslots that fall within the date, in the user's timezone
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> changeDay(LocalDate date) {
        Log.d("TimeViewModel", "getting day");
        ZoneId zoneId = ZoneId.systemDefault();
        long start = date.atTime(0, 1).atZone(zoneId).toEpochSecond();
        long end = date.atTime(23, 59).atZone(zoneId).toEpochSecond();
        Log.d("TimeViewModel", "retrieving slots...");
        dayTimeslots = timeRepository.getTimeslots(start, end);
//        if (dayTimeslots.getValue() == null) {
//            Log.d("TimeViewModel", "slots not retrieved");
//            dayTimeslots = createDay(date);
//        }
        return dayTimeslots;
    }

    /**
     * Creates the timeslots for a day and inserts them into the database
     * @param date the date to create the timeslots for
     * @return The created timeslots
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> createDay(LocalDate date) {
        Log.d("TimeViewmodel", "Creating day");
        LiveData<List<Timeslot>> timeslots = new MutableLiveData<>(new ArrayList<Timeslot>());
        ZoneId zoneId = ZoneId.systemDefault();
        long start = date.atTime(0, 0).atZone(zoneId).toEpochSecond();
        for (int i = 0; i < 24; i++) {
            // add a new timeslot, lasting one hour
            timeslots.getValue().add(new Timeslot(start, start + 3600));
            // increase the start time to the start time of the next interval
            start += 3600;
        }
        timeRepository.insertTimeslots(timeslots.getValue());
        Log.d("TimeViewModel", "Inserting timeslots");
        return timeslots;
    }
}