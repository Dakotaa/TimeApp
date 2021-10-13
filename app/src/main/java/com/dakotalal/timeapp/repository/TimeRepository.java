package com.dakotalal.timeapp.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.dakotalal.timeapp.room.TimeActivityDao;
import com.dakotalal.timeapp.room.TimeRoomDatabase;
import com.dakotalal.timeapp.room.TimeslotDao;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;

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

    public LiveData<Day> getDay(Date date) {
        return timeslotDao.getDay(date);
    }

    public void insertDay(Day day) {
        timeslotDao.insertDay(day);
    }

}
