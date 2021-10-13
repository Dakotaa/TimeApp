package com.dakotalal.timeapp.room;

import android.app.Activity;

import com.dakotalal.timeapp.room.entities.TimeActivity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TimeActivityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TimeActivity timeActivity);

    @Update
    void update(TimeActivity timeActivity);

    @Query("DELETE FROM activity_table")
    void deleteAll();

    @Query("SELECT * from activity_table ORDER BY label ASC")
    LiveData<List<TimeActivity>> getAllTimeActivities();

    @Query("SELECT * from activity_table LIMIT 1")
    TimeActivity[] getAnyTimeActivity();

    @Delete
    void deleteTimeActivity(TimeActivity timeActivity);

}
