package com.dakotalal.timeapp.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeslot_table")
public class Timeslot {
    // the time that this timeslot begins at (unix timestamp)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "start")
    private long timeStart;

    @ColumnInfo(name = "finish")
    private long timeEnd;

    private String activityLabel;

    public Timeslot(@NonNull long timeStart, long timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

}
