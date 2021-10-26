package com.dakotalal.timeapp.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_table")
public class TimeActivity {
    @PrimaryKey()
    @NonNull
    // the text label of the activity
    private String label;
    // the colour that the activity will appear as in the timelog
    private int colour;
    // the "productivity" rating of the activity. -1 = unproductive, 0 = neutral, 1 = productive
    private int productivity;

    public TimeActivity(@NonNull String label, int colour, int productivity) {
        this.label = label;
        this.colour = colour;
        this.productivity = productivity;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public int getProductivity() {
        return productivity;
    }

    public void setProductivity(int productivity) {
        this.productivity = productivity;
    }
}
