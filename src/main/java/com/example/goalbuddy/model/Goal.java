package com.example.goalbuddy.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public long id;


    public String title;
    public String description;
    public long deadline; // unix millis
    public int priority; // 0-low,1-med,2-high
    public boolean completed;


    public long createdAt;


    public Goal(String title, String description, long deadline, int priority) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = false;
        this.createdAt = System.currentTimeMillis();
    }
}