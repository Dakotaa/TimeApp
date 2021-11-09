package com.dakotalal.timeapp.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeslot_table")
public class Timeslot {
    /**
     * the time that this timeslot begins at (unix timestamp)
     */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "start")
    private long timeStart;

    /**
     * The time this timeslot at ends (unix timestamp)
     */
    @ColumnInfo(name = "finish")
    private long timeEnd;

    /**
     * The label of the activity this timeslot represents
     */
    private String activityLabel;

    /**
     * An optional comment for further information for this timeslot
     */
    private String comment;


    public Timeslot(@NonNull long timeStart, long timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.comment = "";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
