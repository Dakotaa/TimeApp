package com.dakotalal.timeapp.room;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Timeslot.class, TimeActivity.class, Day.class}, version = 5, exportSchema = false)
public abstract class TimeRoomDatabase extends RoomDatabase {

    public abstract TimeslotDao timeSlotDao();
    public abstract TimeActivityDao timeActivityDao();

    private static TimeRoomDatabase INSTANCE;

    public static TimeRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TimeRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create the room database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TimeRoomDatabase.class, "room_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TimeActivityDao aDao;

        // default activity labels, colours, and productivities
        String[] labels = {"Work", "Exercise", "Cooking", "Sleep", "Downtime", "Gaming", "School", "Wasted Time"};
        int[] colours = {Color.RED, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.GREEN, Color.YELLOW, Color.BLUE, Color.LTGRAY};
        int[] productivities = {2, 2, 1, 0, -1, -1, 2, -2};

        PopulateDbAsync(TimeRoomDatabase db) {
            aDao = db.timeActivityDao();
        }

        // If there are no activites in the datbase, add the defaults
        @Override
        protected Void doInBackground(final Void... params) {
            if (aDao.getAnyTimeActivity().length < 1) {
                for (int i = 0; i <= labels.length - 1; i++) {
                    TimeActivity activity = new TimeActivity(labels[i], colours[i], productivities[i]);
                    aDao.insert(activity);
                }
            }
            return null;
        }
    }
}
