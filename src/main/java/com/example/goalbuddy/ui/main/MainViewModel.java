package com.example.goalbuddy.ui.main;


import android.app.Application;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.goalbuddy.model.Goal;
import com.example.goalbuddy.repository.GoalRepository;


import java.util.List;


public class MainViewModel extends AndroidViewModel {
    private final GoalRepository repository;
    private final LiveData<List<Goal>> allGoals;


    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }


    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }


    public void insertGoal(Goal g) { repository.insert(g); }
    public void updateGoal(Goal g) { repository.update(g); }
    public void deleteGoal(Goal g) { repository.delete(g); }
}