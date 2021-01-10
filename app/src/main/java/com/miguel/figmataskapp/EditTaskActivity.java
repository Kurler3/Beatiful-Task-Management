package com.miguel.figmataskapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditTaskActivity extends AppCompatActivity {
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("KK:mm a");
    public static final String EDIT_PASS_TASK = "editTaskPassed";
    public static final String PASS_EDITED_TASK = "editedTaskFromEditActivity";
    public static final int EDIT_TASK_ACTIVITY_REQUEST = 3;

    RelativeLayout mDateChooseLayout, mStartTimeLayout, mEndTimeLayout;
    ImageButton mBackArrowBtn;
    TextInputLayout mTitle, mDescription;
    TextView mDate, mStartTime, mEndTime;
    Boolean mHasReminder;
    Button mConfirmChangeBtn;
    SwitchMaterial mReminderSwitch;
    Task mTaskPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        InstantiateViews();

        // Get the Task passed from the adapter and set the texts and hasReminder correctly
        Intent i = getIntent();
        mTaskPassed = i.getParcelableExtra(EDIT_PASS_TASK);

        mTitle.getEditText().setText(mTaskPassed.getTitle());
        mDescription.getEditText().setText(mTaskPassed.getDescription());

        // format dd/MM/yyyy, want (dd, MMMM, yyyy)
        String date = mTaskPassed.getDateCreation();
        Calendar c = MainTasksFragment.getCalendarFromDate(date);
        mDate.setText(dateTextFormat.format(c.getTime()));
        //-------------------------

        mStartTime.setText(mTaskPassed.getStartTime());
        mEndTime.setText(mTaskPassed.getEndTime());
        mHasReminder = mTaskPassed.isHasReminder();

        mReminderSwitch.setChecked(mHasReminder);

        // User can change the start and end time


        // If the user presses the back arrow cancel editting and go back to the task activity
        mBackArrowBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        mConfirmChangeBtn.setOnClickListener(v -> {
                if(CheckChanges()) {
                    Intent data = new Intent();

                    Task task = new Task(mTitle.getEditText().getText().toString(),
                            mDescription.getEditText().getText().toString(),
                            // Date is in format dd, MMMM, yyyy. Change to dd/MM/yyyy
                            mDate.getText().toString(),
                            mStartTime.getText().toString(),
                            mEndTime.getText().toString(),
                            mHasReminder);

                    data.putExtra(PASS_EDITED_TASK, task);

                    setResult(RESULT_OK, data);
                }

        });

        mReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mHasReminder = isChecked);
    }
    private void InstantiateViews(){
        mTitle = findViewById(R.id.task_edit_title_input);
        mDescription = findViewById(R.id.task_edit_description_input);
        mDate = findViewById(R.id.task_edit_date_text);
        mStartTime = findViewById(R.id.edit_task_start_time_text);
        mEndTime = findViewById(R.id.edit_task_end_time_text);
        mConfirmChangeBtn = findViewById(R.id.confirm_change_task_btn);
        mReminderSwitch = findViewById(R.id.edit_task_reminder_switch);

        mDateChooseLayout = findViewById(R.id.date_edit_task_layout);
        mStartTimeLayout = findViewById(R.id.edit_task_start_time_layout);
        mEndTimeLayout = findViewById(R.id.edit_task_end_time_layout);
    }
    private boolean CheckChanges(){
        if(!mTitle.getEditText().getText().toString().isEmpty() && !mDescription.getEditText().getText().toString().isEmpty()){
            return true;
        }
        return false;
    }
}