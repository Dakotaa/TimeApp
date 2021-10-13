package com.dakotalal.timeapp.room;

import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TimeslotDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Timeslot timeslot);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiple(List<Timeslot> timeslots);

    @Update
    void update(Timeslot timeslot);


    // Get the TimeSlot that the given time occurs in
    @Query("SELECT * from timeslot_table WHERE start >= :time AND :time < finish")
    LiveData<Timeslot> getTimeslot(long time);


    // Get a LiveData List of all Timeslots
    @Query("SELECT * from timeslot_table")
    LiveData<List<Timeslot>> getAllTimeslots();


    // Get a LiveData List of Timeslots between the given start and end times
    @Query("SELECT * from timeslot_table WHERE start >= :startTime AND start < :endTime " +
            "ORDER BY start ASC")
    LiveData<List<Timeslot>> getTimeslotsInPeriod(long startTime, long endTime);
}
