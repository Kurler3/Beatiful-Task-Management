package com.miguel.figmataskapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
        private TaskRepository taskRepository;
        private List<Task> tasks;


    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        tasks = taskRepository.getAllTasks();
    }
    public void insert(Task task){
        taskRepository.insert(task);
    }
    public void update(Task task){
        taskRepository.update(task);
    }
    public void delete(Task task){
        taskRepository.delete(task);
    }
    public List<Task> getTasksAtDate(String date){
        return taskRepository.getTasksAtDate(date);
    }
    public List<Task> getAllTasks(){
        return tasks;
    }
}
