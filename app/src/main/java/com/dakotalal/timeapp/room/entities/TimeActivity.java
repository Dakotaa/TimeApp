package com.dakotalal.timeapp.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_table")
public class TimeActivity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    // the text label of the activity
    private String label;
    // the colour that the activity will appear as in the timelog
    private int colour;

    public TimeActivity(@NonNull String label, int colour) {
        this.label = label;
        this.colour = colour;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }
}
