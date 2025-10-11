package com.example.goalbuddy.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.goalbuddy.model.Task;


import java.util.List;


@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);


    @Update
    void update(Task task);


    @Delete
    void delete(Task task);


    @Query("SELECT * FROM tasks WHERE goalId = :goalId ORDER BY dueAt ASC")
    LiveData<List<Task>> getTasksForGoal(long goalId);
}