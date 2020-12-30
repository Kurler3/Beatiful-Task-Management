package com.miguel.figmataskapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String dateCreation;
    private String startTime;
    private String endTime;
    private int priority;
    private boolean hasReminder;

    public Task(String title, String description, String dateCreation, String startTime, String endTime, boolean hasReminder, int priority) {
        this.title = title;
        this.description = description;
        this.dateCreation = dateCreation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hasReminder = hasReminder;
        this.priority = priority;
    }
    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPriority(){
        return priority;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }
}
