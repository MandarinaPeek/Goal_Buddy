package com.example.goalbuddy.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.goalbuddy.model.Goal;


import java.util.List;


@Dao
public interface GoalDao {
    @Insert
    long insert(Goal goal);


    @Update
    void update(Goal goal);


    @Delete
    void delete(Goal goal);


    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    LiveData<List<Goal>> getAllGoals();


    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    LiveData<Goal> getGoalById(long id);
}