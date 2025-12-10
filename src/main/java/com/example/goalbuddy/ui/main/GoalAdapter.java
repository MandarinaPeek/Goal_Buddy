package com.example.goalbuddy.ui.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.example.goalbuddy.R;
import com.example.goalbuddy.model.Goal;


public class GoalAdapter extends ListAdapter<Goal, GoalAdapter.GoalViewHolder> {
    protected GoalAdapter() {
        super(DIFF_CALLBACK);
    }


    private static final DiffUtil.ItemCallback<Goal> DIFF_CALLBACK = new DiffUtil.ItemCallback<Goal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.id == newItem.id;
        }


        @Override
        public boolean areContentsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.title.equals(newItem.title) && oldItem.completed == newItem.completed;
        }
    };


    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal g = getItem(position);
        holder.title.setText(g.title);
        holder.subtitle.setText(g.description != null ? g.description : "");

        holder.btnDeleteGoal.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(g);
            }
        });
    }

    public interface OnGoalDeleteListener {
        void onDelete(Goal goal);
    }

    private OnGoalDeleteListener deleteListener;

    public void setOnDeleteListener(OnGoalDeleteListener listener){
        this.deleteListener = listener;
    }



    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        ImageButton btnDeleteGoal;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.goalTitle);
            subtitle = itemView.findViewById(R.id.goalSubtitle);

            btnDeleteGoal = itemView.findViewById(R.id.btnDeleteGoal);
        }
    }
}