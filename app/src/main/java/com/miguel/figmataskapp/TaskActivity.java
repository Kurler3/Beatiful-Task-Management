package com.miguel.figmataskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;


public class TaskActivity extends AppCompatActivity {
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("KK:mm a");

    TextView mTitle, mDescription;
    TextView mDate, mStartTime, mEndTime;
    Boolean mHasReminder;
    Button mEditTaskBtn;
    SwitchMaterial mReminderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        InstantiateViews();

        // Get the Task passed from the adapter and set the texts and hasReminder correctly





    }
    private void InstantiateViews(){
        mTitle = findViewById(R.id.task_activity_title);
        mDescription = findViewById(R.id.task_activity_description);
        mDate = findViewById(R.id.task_date_text);
        mStartTime = findViewById(R.id.task_start_time_text);
        mEndTime = findViewById(R.id.task_end_time_text);
        mEditTaskBtn = findViewById(R.id.edit_task_btn);
        mReminderSwitch = findViewById(R.id.task_reminder_switch);

        // Can't change the reminder in this activity
        mReminderSwitch.setClickable(false);
    }
}