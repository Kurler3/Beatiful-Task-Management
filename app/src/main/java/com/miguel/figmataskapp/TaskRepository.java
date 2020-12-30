package com.miguel.figmataskapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private List<Task> taskList;

    public TaskRepository(Application application){
        TaskDatabase noteDatabase = TaskDatabase.getInstance(application);
        taskDao = noteDatabase.taskDao();
        taskList = taskDao.getAllTasks();
    }
    public void insert(Task note){
        new InsertAsyncTask(taskDao).execute(note);
    }
    public void update(Task note){
        new UpdateAsyncTask(taskDao).execute(note);
    }
    public void delete(Task note){
        new DeleteAsyncTask(taskDao).execute(note);
    }
    public List<Task> getTasksAtDate(String date){
      List<Task> filtered = new ArrayList<>();
      for(Task task : taskList){
          if(task.getDateCreation().equals(date)){
              filtered.add(task);
          }
      }
      return filtered;
    }
    public List<Task> getAllTasks(){
        return taskList;
    }

    public static class InsertAsyncTask extends AsyncTask<Task, Void, Void>{
        private TaskDao taskDao;
        private InsertAsyncTask(TaskDao dao) {
            this.taskDao = dao;
        }
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }
    public static class UpdateAsyncTask extends AsyncTask<Task, Void, Void>{
        private TaskDao taskDao;
        private UpdateAsyncTask(TaskDao dao){
            this.taskDao = dao;
        }
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }
    public static class DeleteAsyncTask extends AsyncTask<Task, Void, Void>{
        private TaskDao taskDao;
        private DeleteAsyncTask(TaskDao dao){
            this.taskDao = dao;
        }
        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

}
