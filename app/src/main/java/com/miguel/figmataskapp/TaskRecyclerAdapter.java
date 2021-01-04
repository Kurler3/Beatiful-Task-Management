package com.miguel.figmataskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {
    ArrayList<Task> mTaskArray;
    Context mContext;
    OnTaskRemovedListener mTaskRemovedListener;

    public TaskRecyclerAdapter(OnTaskRemovedListener listener,Context mContext, ArrayList<Task> tasks) {
        this.mContext = mContext;
        this.mTaskArray = tasks;
        this.mTaskRemovedListener = listener;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView taskItemTitle, taskItemTime;
        ImageView taskItemOptions;
        PopupMenu mPopUpMenu;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskItemTitle = itemView.findViewById(R.id.task_item_title);
            taskItemTime = itemView.findViewById(R.id.task_item_time);
            taskItemOptions = itemView.findViewById(R.id.task_item_options);

            mPopUpMenu = new PopupMenu(itemView.getContext(),taskItemOptions);

            CreateTaskItemOptions();
        }
        private void CreateTaskItemOptions(){
            Menu menu = mPopUpMenu.getMenu();
            mPopUpMenu.getMenuInflater().inflate(R.menu.task_item_options_menu, menu);

            mPopUpMenu.setOnMenuItemClickListener(menuItem -> {
                switch(menuItem.getItemId()){
                    case R.id.task_item_options_menu_edit:
                        // Launch Edit Task activity
                        return true;
                    case R.id.task_item_options_menu_remove:
                        // Notify the Home Screen that need to remove task
                        mTaskRemovedListener.removeTask(mTaskArray.get(getAdapterPosition()));
                        return true;
                    default:
                        return true;
                }
            });

            taskItemOptions.setOnClickListener(view -> {
                mPopUpMenu.show();
            });
        }
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);

        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = mTaskArray.get(position);

        holder.taskItemTitle.setText(task.getTitle());

        holder.taskItemTime.setText(task.getStartTime() + "-" + task.getEndTime());
    }

    @Override
    public int getItemCount() {
        return mTaskArray.size();
    }

    public void setTaskArray(ArrayList<Task> taskArray){
        this.mTaskArray = taskArray;
    }

    public interface OnTaskRemovedListener{
        void removeTask(Task task);
    }
}
