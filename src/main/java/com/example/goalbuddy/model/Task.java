package com.example.goalbuddy.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = Goal.class,
                parentColumns = "id",
                childColumns = "goalId",
                onDelete = ForeignKey.CASCADE))
public class Task {
    @PrimaryKey(autoGenerate = true)
    public long id;


    public long goalId;
    public String title;
    public boolean done;
    public long dueAt;


    public Task(long goalId, String title, long dueAt) {
        this.goalId = goalId;
        this.title = title;
        this.dueAt = dueAt;
        this.done = false;
    }
}