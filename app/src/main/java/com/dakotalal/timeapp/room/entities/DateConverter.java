package com.dakotalal.timeapp.room.entities;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.util.Date;

public class DateConverter {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static LocalDate toDate(Long timestamp) {
        return timestamp == null ? null : LocalDate.ofEpochDay(timestamp);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static Long toTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
