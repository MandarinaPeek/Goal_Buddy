package com.example.goalbuddy.ui.main;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goalbuddy.R;
import com.example.goalbuddy.model.Goal;
import com.example.goalbuddy.ui.create.CreateGoalActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends ComponentActivity {

    private MainViewModel viewModel;

    private void showDeleteDialog(Goal goal) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить цель?")
                .setMessage("Вы уверены, что хотите удалить эту цель?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    viewModel.delete(goal);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvGoals);
        rv.setLayoutManager(new LinearLayoutManager(this));
        GoalAdapter adapter = new GoalAdapter();
        rv.setAdapter(adapter);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(200);
        animator.setAddDuration(200);
        rv.setItemAnimator(animator);

        // --- SWIPE TO DELETE ---
        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        int position = viewHolder.getAdapterPosition();
                        Goal goal = adapter.getCurrentList().get(position);

                        showDeleteDialog(goal);

                        // Возврат карточки назад
                        adapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        Paint paint = new Paint();
                        paint.setColor(Color.RED);

                        // Рисуем красный фон
                        c.drawRect(
                                (float) itemView.getRight() + dX,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom(),
                                paint
                        );

                        // Рисуем иконку корзины
                        Drawable icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                        if (icon != null) {
                            int margin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int left = itemView.getRight() - margin - icon.getIntrinsicWidth();
                            int top = itemView.getTop() + margin;
                            int right = itemView.getRight() - margin;
                            int bottom = top + icon.getIntrinsicHeight();
                            icon.setBounds(left, top, right, bottom);
                            icon.draw(c);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rv);

        // Listener для кнопки Delete в адаптере
        adapter.setOnDeleteListener(goal -> showDeleteDialog(goal));

        // Обновление данных
        viewModel.getAllGoals().observe(this, goals -> {
            adapter.submitList(goals);
        });

        // FAB — создать цель
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateGoalActivity.class));
        });
    }
}
