package com.miguel.figmataskapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ViewTaskActivity extends AppCompatActivity {
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("KK:mm a");
    public static final String PASS_TASK = "taskPassed";
    public static final String RESULT_TASK = "editedTask";
    public static final String TASK_REMINDER_CHANGED = "taskReminderChanged";
    public static final int TASK_ACTIVITY_REQUEST = 2;

    ImageButton mBackArrowBtn;
    TextView mTitle, mDescription;
    TextView mDate, mStartTime, mEndTime;
    Boolean mHasReminder;
    Button mEditTaskBtn;
    SwitchMaterial mReminderSwitch;
    Task mTaskPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        InstantiateViews();

        // Get the Task passed from the adapter and set the texts and hasReminder correctly
        Intent i = getIntent();
        mTaskPassed = i.getParcelableExtra(PASS_TASK);

        mTitle.setText(mTaskPassed.getTitle());
        mDescription.setText(mTaskPassed.getDescription());

        // format dd/MM/yyyy, want (dd, MMMM, yyyy)
        String date = mTaskPassed.getDateCreation();
        Calendar c = MainTasksFragment.getCalendarFromDate(date);
        mDate.setText(dateTextFormat.format(c.getTime()));
        //-------------------------

        mStartTime.setText(mTaskPassed.getStartTime());
        mEndTime.setText(mTaskPassed.getEndTime());
        mHasReminder = mTaskPassed.isHasReminder();

        mReminderSwitch.setChecked(mHasReminder);

        mBackArrowBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        mEditTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditTaskActivity.class);

            intent.putExtra(EditTaskActivity.EDIT_PASS_TASK, mTaskPassed);

            startActivityForResult(intent, EditTaskActivity.EDIT_TASK_ACTIVITY_REQUEST);
        });
    }
    private void InstantiateViews(){
        mTitle = findViewById(R.id.task_activity_title);
        mDescription = findViewById(R.id.task_activity_description);
        mDate = findViewById(R.id.task_date_text);
        mStartTime = findViewById(R.id.task_start_time_text);
        mEndTime = findViewById(R.id.task_end_time_text);
        mEditTaskBtn = findViewById(R.id.edit_task_btn);
        mReminderSwitch = findViewById(R.id.task_reminder_switch);
        mBackArrowBtn = findViewById(R.id.back_arrow_btn_task_activity);

        // Can't change the reminder in this activity
        mReminderSwitch.setClickable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==EditTaskActivity.EDIT_TASK_ACTIVITY_REQUEST && resultCode==RESULT_OK){
            Task editedTask = data.getParcelableExtra(EditTaskActivity.PASS_EDITED_TASK);

            Intent dataIntent = new Intent();

            // If the HasReminder was changed then send an extra boolean to the homescreen
            if(mTaskPassed.isHasReminder()!=editedTask.isHasReminder()){
                dataIntent.putExtra(TASK_REMINDER_CHANGED, true);
            }else{
                dataIntent.putExtra(TASK_REMINDER_CHANGED, false);
            }

            dataIntent.putExtra(RESULT_TASK, editedTask);

            setResult(RESULT_OK, dataIntent);

            finish();
        }
    }
}