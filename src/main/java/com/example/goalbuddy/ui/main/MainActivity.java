package com.example.goalbuddy.ui.main;


import android.os.Bundle;


import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.goalbuddy.R;
import com.example.goalbuddy.model.Goal;


import java.util.List;


public class MainActivity extends ComponentActivity {
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init ViewModel (Java way)
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RecyclerView rv = findViewById(R.id.rvGoals);
        rv.setLayoutManager(new LinearLayoutManager(this));
        final GoalAdapter adapter = new GoalAdapter();
        rv.setAdapter(adapter);

        viewModel.getAllGoals().observe(this, goals -> {
            adapter.submitList(goals);
        });
    }
}