package com.dakotalal.timeapp.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.dakotalal.timeapp.room.TimeActivityDao;
import com.dakotalal.timeapp.room.TimeRoomDatabase;
import com.dakotalal.timeapp.room.TimeslotDao;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TimeRepository {
    private TimeslotDao timeslotDao;
    private TimeActivityDao timeActivityDao;
    private LiveData<List<TimeActivity>> allTimeActivities;
    private LiveData<List<Timeslot>> activeTimeslots;

    public TimeRepository(Application application) {
        TimeRoomDatabase db = TimeRoomDatabase.getDatabase(application);
        timeslotDao = db.timeSlotDao();
        timeActivityDao = db.timeActivityDao();
        allTimeActivities = timeActivityDao.getAllTimeActivities();
    }

    public LiveData<List<TimeActivity>> getAllTimeActivities() {
        return this.allTimeActivities;
    }

    public LiveData<List<Timeslot>> getActiveTimeslots() {
        return activeTimeslots;
    }

    /*
        Inserts a new activity into the database.
    */
    public void insertTimeActivity(TimeActivity timeActivity) {
        new insertTimeActivityAsyncTask(timeActivityDao).execute(timeActivity);
    }

    private static class insertTimeActivityAsyncTask extends AsyncTask<TimeActivity, Void, Void> {
        private TimeActivityDao taDao;

        insertTimeActivityAsyncTask(TimeActivityDao dao) {
            taDao = dao;
        }

        @Override
        protected Void doInBackground(final TimeActivity... params) {
            taDao.insert(params[0]);
            return null;
        }
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


    /*
    Updates the given activity in the database.
     */
    public void updateTimeActivity(TimeActivity timeActivity) {
        new updateTimeActivityAsyncTask(timeActivityDao).execute(timeActivity);
    }

    private static class updateTimeActivityAsyncTask extends AsyncTask<TimeActivity, Void, Void> {
        private TimeActivityDao taDao;

        updateTimeActivityAsyncTask(TimeActivityDao dao) {
            taDao = dao;
        }

        @Override
        protected Void doInBackground(final TimeActivity... params) {
            taDao.update(params[0]);
            return null;
        }
    }



    public void updateTimeslot(Timeslot timeslot) {
        new updateTimeslotAsyncTask(timeslotDao).execute(timeslot);
    }

    private static class updateTimeslotAsyncTask extends AsyncTask<Timeslot, Void, Void> {
        private TimeslotDao tsDao;

        updateTimeslotAsyncTask(TimeslotDao dao) {
            tsDao = dao;
        }

        @Override
        protected Void doInBackground(final Timeslot... params) {
            tsDao.update(params[0]);
            return null;
        }
    }


    public void deleteAllTimeActivities() {
        new deleteAllTimeActivitiesAsyncTask(timeActivityDao).execute();
    }

    private static class deleteAllTimeActivitiesAsyncTask extends AsyncTask<Integer, Void, Void> {
        private TimeActivityDao taDao;

        deleteAllTimeActivitiesAsyncTask(TimeActivityDao dao) {
            taDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            taDao.deleteAll();
            return null;
        }
    }


    /*
     TODO: Make this async if it's slow
     */
    public LiveData<List<Timeslot>> getTimeslots(long start, long finish) {
        return timeslotDao.getTimeslotsInPeriod(start, finish);
    }


    public LiveData<Integer> getActivityCount(String label, long start, long finish) {
        return timeslotDao.getActivitycount(label, start, finish);
    }

    public LiveData<Integer> getAllActivityCounts(String label, long start, long finish) {
        return timeslotDao.getActivitycount(label, start, finish);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createToday() {
        createDay(LocalDate.now());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> retrieveDay(LocalDate date) {
        createDay(date);
        //Log.d("TimeRepository", "retrieving day...");
        ZoneId zoneId = ZoneId.systemDefault();
        long start = date.atTime(0, 0).atZone(zoneId).toEpochSecond();
        long end = date.atTime(23, 59).atZone(zoneId).toEpochSecond();
        return timeslotDao.getTimeslotsInPeriod(start, end);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Timeslot>> createDay(LocalDate date) {
        //Log.d("TimeRepository", "Creating day");
        LiveData<List<Timeslot>> timeslots = new MutableLiveData<>(new ArrayList<Timeslot>());
        ZoneId zoneId = ZoneId.systemDefault();
        // create timeslots for the day
        long start = date.atTime(0, 0).atZone(zoneId).toEpochSecond();
        for (int i = 0; i < 24; i++) {
            timeslots.getValue().add(new Timeslot(start, start + 3600));
            start += 3600;
        }
        insertDay(new Day(date));
        insertTimeslots(timeslots.getValue());
        return timeslots;
    }

    public LiveData<Day> getDay(LocalDate date) {
        return timeslotDao.getDay(date);
    }

    public LiveData<List<Day>> getAllDays() {
        return timeslotDao.getAllDays();
    }

    public LiveData<Void> insertDay(Day day) {
        timeslotDao.insertDay(day);
        return null;
    }

}
