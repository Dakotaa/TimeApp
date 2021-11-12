package com.dakotalal.timeapp.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import com.dakotalal.timeapp.room.TimeActivityDao;
import com.dakotalal.timeapp.room.TimeRoomDatabase;
import com.dakotalal.timeapp.room.TimeslotDao;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.GoalDao;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.room.entities.goals.ActivityGoal;
import com.dakotalal.timeapp.room.entities.goals.Goal;
import com.dakotalal.timeapp.ui.MainActivity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TimeRepository {
    private TimeslotDao timeslotDao;
    private TimeActivityDao timeActivityDao;
    private GoalDao goalDao;
    private LiveData<List<TimeActivity>> allTimeActivities;
    private LiveData<List<Timeslot>> activeTimeslots;
    private Application application;

    public TimeRepository(Application application) {
        TimeRoomDatabase db = TimeRoomDatabase.getDatabase(application);
        this.application = application;
        // initialized all DAOs
        timeslotDao = db.timeSlotDao();
        timeActivityDao = db.timeActivityDao();
        goalDao = db.goalDao();
        // keep a cached list of all time activities
        allTimeActivities = timeActivityDao.getAllTimeActivities();
    }

    /**
     * Get all of the user's time activities
     * @return a livedata list of time activites
     */
    public LiveData<List<TimeActivity>> getAllTimeActivities() {
        return this.allTimeActivities;
    }

    /**
     * Get an ordered list of the user's most commonly logged time activities since the given time
     * @param since the start of the period to search from, in epoch seconds
     * @return a livedata list of time activties, ordered from most common in the user's timelog
     */
    public LiveData<List<TimeActivity>> getMostCommonTimeActivities(long since) {
        return timeslotDao.getMostCommonTimeActivities();
    }

    /**
     * Insert a new time activity into the database
     * @param timeActivity a newly created time activity
     */
    public void insertTimeActivity(TimeActivity timeActivity) {
        timeActivityDao.insert(timeActivity);
    }

    /**
     * Inserts multiple time slots into the database
     * @param timeslots a list of timeslots to insert
     */
    public void insertTimeslots(List<Timeslot> timeslots) {
        new insertTimeslotsAsyncTask(timeslotDao).execute(timeslots);
    }
    private static class insertTimeslotsAsyncTask extends AsyncTask<List<Timeslot>, Void, Void> {
        private TimeslotDao tsDao;

        insertTimeslotsAsyncTask(TimeslotDao dao) {
            tsDao = dao;
        }

        @Override
        protected Void doInBackground(final List<Timeslot>... params) {
            tsDao.insertMultipleTimeslots(params[0]);
            return null;
        }
    }

    /**
     * Update the given time activity in the database
     * @param timeActivity the time activity to update
     */
    public void updateTimeActivity(TimeActivity timeActivity) {
        timeActivityDao.update(timeActivity);
    }

    /**
     * update the given time activity in the database
     * @param timeslot the time activity to update
     */
    public void updateTimeslot(Timeslot timeslot) {
        timeslotDao.update(timeslot);
    }

    /**
     * deletes the given time activity from the database
     * @param timeActivity the time activity object to delete. Will delete the time activity object
     *                     in the database that has the same label.
     */
    public void deleteTimeActivity(TimeActivity timeActivity) {
        timeActivityDao.deleteTimeActivity(timeActivity.getLabel());
    }

    /*
     TODO: Figure out how to make this async
     */
    /**
     * Get all of the timeslots within the given time period
     * @param start the start of the time period, in epoch seconds
     * @param finish the end of the time period, in epoch seconds
     * @return a livedata list of all timeslots within the given time period
     */
    public LiveData<List<Timeslot>> getTimeslots(long start, long finish) {
        return timeslotDao.getTimeslotsInPeriod(start, finish);
    }

    /**
     * Get the number of times the given time activity (by label) is logged in the user's timelog
     * within the given time period.
     * @param label the label of the activity
     * @param start the start of the time period, in epoch seconds
     * @param finish the end of the time period, in epoch seconds
     * @return a livedata integer value representing the number of times this activity has been
     * logged in the user's timelog within the given time period.
     */
    public LiveData<Integer> getActivityCount(String label, long start, long finish) {
        return timeslotDao.getActivitycount(label, start, finish);
    }

    public LiveData<Integer> getEmptyTimeslotCountSince(long start, long end) {
        return timeslotDao.getEmptyTimeslotCountSince(start, end);
    }


    /**
     * Creates a day in the database representing the current day
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createToday() {
        createDay(LocalDate.now());
    }

    /**
     * Retrieve the timeslots of the given day from the database if it exists. If it doesn't exist,
     * it is created first
     * @param date the date of the day to be retrieved/created
     * @return a livedata list of timeslots occurring within the day
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> retrieveDay(LocalDate date) {
        createDay(date);
        ZoneId zoneId = ZoneId.systemDefault();
        long start = date.atTime(0, 0).atZone(zoneId).toEpochSecond();
        long end = date.atTime(23, 59).atZone(zoneId).toEpochSecond();
        return timeslotDao.getTimeslotsInPeriod(start, end);
    }

    /**
     * Creates the day and timeslots for the given date and inserts into the database
     * @param date the date of the day to create
     * @return a live data list of the timeslots making up the created day
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> createDay(LocalDate date) {
        LiveData<List<Timeslot>> timeslots = new MutableLiveData<>(new ArrayList<Timeslot>());
        ZoneId zoneId = ZoneId.systemDefault();
        // create timeslots for the day
        SharedPreferences prefs = application.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        int intervalLength = prefs.getInt(MainActivity.PREFS_INTERVAL_LENGTH, 30);
        int numIntervals = 1440 / intervalLength;
        int intervalLengthS = intervalLength * 60;
        long start = date.atTime(0, 0).atZone(zoneId).toEpochSecond();
        for (int i = 0; i < numIntervals; i++) {
            if (timeslots.getValue() != null) {
                timeslots.getValue().add(new Timeslot(start, start + intervalLengthS));
            }
            start += intervalLengthS;
        }
        insertDay(new Day(date));
        insertTimeslots(timeslots.getValue());
        return timeslots;
    }

    /**
     * gets all day objects from the database
     * @return a live data list of all existing days
     */
    public LiveData<List<Day>> getAllDays() {
        return timeslotDao.getAllDays();
    }

    /**
     * inserts a new day object into the database
     * @param day the day object to insert
     * @return void
     */
    public LiveData<Void> insertDay(Day day) {
        timeslotDao.insertDay(day);
        return null;
    }

    public void insertGoal(ActivityGoal goal) {
        goalDao.insert(goal);
    }

    public void updateGoal(ActivityGoal goal) {
        goalDao.update(goal);
    }

    public LiveData<List<Goal>> getAllGoals() {
        return goalDao.getAllGoals();
    }

}
