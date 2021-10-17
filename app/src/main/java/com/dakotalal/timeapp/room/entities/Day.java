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
    private LocalDate date;

    public Day(@NonNull LocalDate date) {
        this.date = date;
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    public void setDate(@NonNull LocalDate date) {
        this.date = date;
    }
}
