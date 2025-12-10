package com.example.goalbuddy.ui.create;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goalbuddy.R;
import com.example.goalbuddy.db.AppDatabase;
import com.example.goalbuddy.model.Goal;
import com.example.goalbuddy.model.Task;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateGoalActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnPickDate, btnGenerate, btnAddTask, btnSaveGoal;
    private Spinner spinnerPriority;
    private Switch switchAutoSplit;
    private LinearLayout llTasksContainer;
    private long pickedDeadline = 0L;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        switchAutoSplit = findViewById(R.id.switchAutoSplit);
        llTasksContainer = findViewById(R.id.llTasksContainer);

        setupPrioritySpinner();
        setupListeners();
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void setupListeners() {
        btnPickDate.setOnClickListener(v -> pickDate());

        btnAddTask.setOnClickListener(v -> addTaskItem(""));

        btnGenerate.setOnClickListener(v -> {
            if (pickedDeadline == 0L) {
                Toast.makeText(this, "Выберите дедлайн прежде чем генерировать.", Toast.LENGTH_SHORT).show();
                return;
            }
            generateAutoTasks();
        });

        btnSaveGoal.setOnClickListener(v -> saveGoal());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year, month, dayOfMonth, 23, 59, 59);
                    pickedDeadline = sel.getTimeInMillis();
                    btnPickDate.setText(dateFormat.format(new Date(pickedDeadline)));
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void addTaskItem(String title) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View item = inflater.inflate(R.layout.item_task_input, llTasksContainer, false);
        EditText etTaskTitle = item.findViewById(R.id.etTaskTitle);
        Button btnTaskDate = item.findViewById(R.id.btnTaskDate);
        ImageButton btnRemove = item.findViewById(R.id.btnRemoveTask);

        etTaskTitle.setText(title);

        // default date: if global deadline set show it
        if (pickedDeadline != 0L) {
            btnTaskDate.setText(dateFormat.format(new Date(pickedDeadline)));
            btnTaskDate.setTag(pickedDeadline);
        } else {
            btnTaskDate.setText("Дата");
            btnTaskDate.setTag(0L);
        }

        btnTaskDate.setOnClickListener(v -> {
            long current = (long) btnTaskDate.getTag();
            Calendar init = Calendar.getInstance();
            if (current != 0L) init.setTimeInMillis(current);
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar sel = Calendar.getInstance();
                        sel.set(year, month, dayOfMonth, 23,59,59);
                        long dt = sel.getTimeInMillis();
                        btnTaskDate.setText(dateFormat.format(new Date(dt)));
                        btnTaskDate.setTag(dt);
                    },
                    init.get(Calendar.YEAR),
                    init.get(Calendar.MONTH),
                    init.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnRemove.setOnClickListener(v -> llTasksContainer.removeView(item));
        llTasksContainer.addView(item);
    }

    private void generateAutoTasks() {
        // Простая логика: решаем число шагов по дистанции до дедлайна
        long now = System.currentTimeMillis();
        long diffDays = Math.max(1, (pickedDeadline - now) / (24L * 60 * 60 * 1000));
        int count;
        if (diffDays <= 7) count = 1;
        else if (diffDays <= 30) count = 4;
        else if (diffDays <= 90) count = 8;
        else count = 12;

        // очищаем текущие поля
        llTasksContainer.removeAllViews();

        // равномерно распределяем дедлайны
        for (int i = 0; i < count; i++) {
            long due = now + ((i + 1) * (pickedDeadline - now) / (long) count);
            String title = "Шаг " + (i + 1);
            addTaskItemWithDate(title, due);
        }
    }

    private void addTaskItemWithDate(String title, long dueAt) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View item = inflater.inflate(R.layout.item_task_input, llTasksContainer, false);
        EditText etTaskTitle = item.findViewById(R.id.etTaskTitle);
        Button btnTaskDate = item.findViewById(R.id.btnTaskDate);
        ImageButton btnRemove = item.findViewById(R.id.btnRemoveTask);

        etTaskTitle.setText(title);
        btnTaskDate.setText(dateFormat.format(new Date(dueAt)));
        btnTaskDate.setTag(dueAt);

        btnTaskDate.setOnClickListener(v -> {
            long current = (long) btnTaskDate.getTag();
            Calendar init = Calendar.getInstance();
            if (current != 0L) init.setTimeInMillis(current);
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar sel = Calendar.getInstance();
                        sel.set(year, month, dayOfMonth, 23,59,59);
                        long dt = sel.getTimeInMillis();
                        btnTaskDate.setText(dateFormat.format(new Date(dt)));
                        btnTaskDate.setTag(dt);
                    },
                    init.get(Calendar.YEAR),
                    init.get(Calendar.MONTH),
                    init.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnRemove.setOnClickListener(v -> llTasksContainer.removeView(item));
        llTasksContainer.addView(item);
    }

    private void saveGoal() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (title.isEmpty()) {
            etTitle.setError("Введите название цели");
            return;
        }
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        int priority = spinnerPriority.getSelectedItemPosition(); // 0-low,1-med,2-high

        long goalDeadline = pickedDeadline;

        // Считаем подзадачи
        List<Task> tasksToSave = new ArrayList<>();
        int childCount = llTasksContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View item = llTasksContainer.getChildAt(i);
            EditText etTaskTitle = item.findViewById(R.id.etTaskTitle);
            Button btnTaskDate = item.findViewById(R.id.btnTaskDate);

            String taskTitle = etTaskTitle.getText() != null ? etTaskTitle.getText().toString().trim() : "";
            long due = 0L;
            Object tag = btnTaskDate.getTag();
            if (tag instanceof Long) due = (Long) tag;

            if (taskTitle.isEmpty()) continue; // skip empty
            // will set goalId after inserting goal
            Task t = new Task(0L, taskTitle, due);
            tasksToSave.add(t);
        }

        // Если нет подзадач и автоскплит выключен — создаем 1 подзадачу с названием цели
        if (tasksToSave.isEmpty()) {
            Task t = new Task(0L, title, goalDeadline != 0L ? goalDeadline : System.currentTimeMillis());
            tasksToSave.add(t);
        }

        // Создаём Goal и сохраняем + tasks в background
        Goal goal = new Goal(title, desc, goalDeadline, priority);

        // background save: вставляем goal, получаем id, вставляем tasks
        executor.execute(() -> {
            try {
                long goalId = AppDatabase.getInstance(getApplicationContext()).goalDao().insert(goal);

                // insert tasks
                for (Task t : tasksToSave) {
                    t.goalId = goalId;
                    AppDatabase.getInstance(getApplicationContext()).taskDao().insert(t);
                }

                runOnUiThread(() -> {
                    Toast.makeText(CreateGoalActivity.this, "Цель сохранена", Toast.LENGTH_SHORT).show();
                    finish(); // закрыть экран
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CreateGoalActivity.this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
