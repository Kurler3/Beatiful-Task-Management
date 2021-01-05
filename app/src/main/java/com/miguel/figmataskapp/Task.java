package com.miguel.figmataskapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String dateCreation;
    private String startTime;
    private String endTime;
    private boolean hasReminder;

    public Task(String title, String description, String dateCreation, String startTime, String endTime, boolean hasReminder) {
        this.title = title;
        this.description = description;
        this.dateCreation = dateCreation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hasReminder = hasReminder;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        dateCreation = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        hasReminder = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(dateCreation);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeByte((byte) (hasReminder ? 1 : 0));
    }
}
