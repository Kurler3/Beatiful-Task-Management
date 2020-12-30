package com.miguel.figmataskapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface TaskDao {
    @Insert
    void insert(Task task);
    @Update
    void update(Task task);
    @Delete
    void delete(Task task);

    // Pass a date in the form day/month/year, and priority is the hour which it starts as an integer
    @Query("SELECT * FROM task_table WHERE dateCreation LIKE :date ORDER BY priority DESC")
    List<Task> getTasksAtDate(String date);

    @Query("SELECT * FROM task_table")
    List<Task> getAllTasks();
}
