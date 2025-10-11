package com.example.goalbuddy.repository;


import android.app.Application;


import androidx.lifecycle.LiveData;


import com.example.goalbuddy.db.AppDatabase;
import com.example.goalbuddy.db.GoalDao;
import com.example.goalbuddy.model.Goal;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GoalRepository {
    private final GoalDao goalDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public GoalRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        goalDao = db.goalDao();
    }


    public LiveData<List<Goal>> getAllGoals() {
        return goalDao.getAllGoals();
    }


    public LiveData<Goal> getGoalById(long id) {
        return goalDao.getGoalById(id);
    }


    public void insert(Goal goal) {
        executor.execute(() -> goalDao.insert(goal));
    }


    public void update(Goal goal) {
        executor.execute(() -> goalDao.update(goal));
    }


    public void delete(Goal goal) {
        executor.execute(() -> goalDao.delete(goal));
    }
}