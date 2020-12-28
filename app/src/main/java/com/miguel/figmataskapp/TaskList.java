package com.miguel.figmataskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskList extends AppCompatActivity {
    TextView mCurrentMonthYear;
    ImageView mPreviousDay, mNextDay, mCalendarOpen;
    ImageButton mOptionsBtn;
    FloatingActionButton mAddTaskBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
    }
}