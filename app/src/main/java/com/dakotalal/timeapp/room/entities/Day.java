package com.dakotalal.timeapp.room.entities;

import java.time.LocalDate;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "day_table")
public class Day {
    @PrimaryKey
    @NonNull
    @TypeConverters(DateConverter.class)
    private Date date;

    public Day(@NonNull Date date) {
        this.date = date;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
