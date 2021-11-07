package com.dakotalal.timeapp.room;

import com.dakotalal.timeapp.room.entities.goals.ActivityGoal;
import com.dakotalal.timeapp.room.entities.goals.Goal;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActivityGoal goal);

    @Update
    void update(ActivityGoal goal);

    @Query("DELETE FROM activity_goal_table WHERE id = :id")
    void deleteActivityGoal(int id);

    @Query("SELECT * FROM activity_goal_table")
    LiveData<List<Goal>> getAllGoals();
}
