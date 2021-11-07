package com.dakotalal.timeapp.room;

import com.dakotalal.timeapp.room.entities.DateConverter;
import com.dakotalal.timeapp.room.entities.Day;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

@Dao
public interface TimeslotDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTimeslot(Timeslot timeslot);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDay(Day day);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * from day_table WHERE date = :date")
    LiveData<Day> getDay(LocalDate date);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * from day_table")
    LiveData<List<Day>> getAllDays();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultipleTimeslots(List<Timeslot> timeslots);

    @Update
    void update(Timeslot timeslot);


    // Get the TimeSlot that the given time occurs in

    @Query("SELECT * from timeslot_table WHERE start >= :time AND :time < finish")
    LiveData<Timeslot> getTimeslot(long time);

    // Get a LiveData List of all Timeslots

    @Query("SELECT * from timeslot_table")
    LiveData<List<Timeslot>> getAllTimeslots();

    // get time activities ordered by how common they are, since the given time
    @Query("SELECT * FROM activity_table LEFT OUTER JOIN timeslot_table ON label=activityLabel GROUP BY label ORDER BY COUNT(activityLabel) DESC")
    LiveData<List<TimeActivity>> getMostCommonTimeActivities();


    // Get a LiveData List of Timeslots between the given start and end times
    @Query("SELECT * from timeslot_table WHERE start >= :startTime AND start < :endTime " +
            "ORDER BY start ASC")
    LiveData<List<Timeslot>> getTimeslotsInPeriod(long startTime, long endTime);

    @Query("SELECT count(*) from timeslot_table WHERE start >= :startTime AND start < :endTime " +
            "AND activityLabel LIKE '%' || :label || '%'")
    LiveData<Integer> getActivitycount(String label, long startTime, long endTime);
}
