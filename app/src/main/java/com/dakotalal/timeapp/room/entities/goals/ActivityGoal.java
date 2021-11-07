package com.dakotalal.timeapp.room.entities.goals;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_goal_table")
public class ActivityGoal extends Goal {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private int target;
    private long startTime;
    private long endTime;
    private boolean repeating;
    private String activityLabel;

    public ActivityGoal(String name, String activityLabel, int target, long startTime, long endTime, boolean repeating) {
        this.name = name;
        this.activityLabel = activityLabel;
        this.target = target;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeating = repeating;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int goal) {
        this.target = target;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
