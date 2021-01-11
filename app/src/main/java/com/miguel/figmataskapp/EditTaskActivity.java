package com.miguel.figmataskapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditTaskActivity extends AppCompatActivity {
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("KK:mm");
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
    int mTaskPassedID;

    int startHourSelected, startMinutesSelected;
    int endHourSelected, endMinutesSelected;

    boolean timeChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        InstantiateViews();

        // Get the Task passed from the adapter and set the texts and hasReminder correctly
        Intent i = getIntent();
        mTaskPassed = i.getParcelableExtra(EDIT_PASS_TASK);
        mTaskPassedID = mTaskPassed.getId();

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
        HandleTimePicker();

        // If the user presses the back arrow cancel editing and go back to the task activity
        mBackArrowBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Confirmation of changes
        mConfirmChangeBtn.setOnClickListener(v -> {
                if(CheckChanges()) {
                    Intent data = new Intent();


                    Task task = new Task(mTitle.getEditText().getText().toString(),
                            mDescription.getEditText().getText().toString(),
                            mTaskPassed.getDateCreation(),
                            mStartTime.getText().toString(),
                            mEndTime.getText().toString(),
                            mHasReminder);

                    task.setId(mTaskPassedID);

                    data.putExtra(PASS_EDITED_TASK, task);

                    setResult(RESULT_OK, data);

                    finish();
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
        mBackArrowBtn = findViewById(R.id.back_arrow_btn_edit_task_activity);

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
    private void HandleTimePicker(){
        String[] startTime = mStartTime.getText().toString().split(":");

        TimePickerDialog startTimePicker = CreateTimePicker(true, startTime);

        String[] endTime = mEndTime.getText().toString().split(":");

        TimePickerDialog endTimePicker = CreateTimePicker(false, endTime);

        // Show the time pickers when clicked
        mStartTimeLayout.setOnClickListener(view -> startTimePicker.show());
        mEndTimeLayout.setOnClickListener(view -> endTimePicker.show());
    }
    private TimePickerDialog CreateTimePicker(boolean isStartTime, String[] parts){

        TimePickerDialog timePicker = new TimePickerDialog(EditTaskActivity.this,
                (timePicker1, i, i1) -> {
                    if(isStartTime){
                        startHourSelected = i;
                        startMinutesSelected = i1;
                    }else{
                        endHourSelected = i;
                        endMinutesSelected = i1;
                    }

                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, i);
                    selectedTime.set(Calendar.MINUTE, i1);

                    // Checks if the time input is correct and sets the text for the specific TextView
                    timeChanged = CheckTimeSelectedAndSetText(isStartTime, selectedTime);
                },
                Integer.valueOf(parts[0]),
                Integer.valueOf(parts[1]),
                false);
        if(timeChanged){
            if(isStartTime) timePicker.updateTime(startHourSelected, startMinutesSelected);
            else timePicker.updateTime(endHourSelected, endMinutesSelected);
        }
        return timePicker;
    }
    private boolean CheckTimeSelectedAndSetText(boolean isStartTime, Calendar selectedTime){
        if(isStartTime){
            String[] endTime = mEndTime.getText().toString().split(":");
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTime[0]));
            endTimeCalendar.set(Calendar.MINUTE, Integer.valueOf(endTime[1]));

            //Check if selectedTime is after the endTime
            if(selectedTime.getTime().after(endTimeCalendar.getTime())){
                Toast.makeText(EditTaskActivity.this, "Start time needs to be before end time", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                mStartTime.setText(timeIntervalTextFormat.format(selectedTime.getTime()));
            }
        }else{
            String[] startTime = mStartTime.getText().toString().split(":");
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTime[0]));
            startTimeCalendar.set(Calendar.MINUTE, Integer.valueOf(startTime[1]));

            // Check if selectedTime is before start time
            if(selectedTime.getTime().before(startTimeCalendar.getTime())){
                Toast.makeText(EditTaskActivity.this, "End time needs to be after start time", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                mEndTime.setText(timeIntervalTextFormat.format(selectedTime.getTime()));
            }
        }
        return true;
    }
}