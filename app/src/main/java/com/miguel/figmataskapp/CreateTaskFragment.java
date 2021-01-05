package com.miguel.figmataskapp;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateTaskFragment extends Fragment {
    public static final String DATE_PICKER = "datePicker";
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("kk:mm");


    RelativeLayout mStartTimeLayout, mEndTimeLayout, mDatePickLayout;
    SwitchMaterial mReminderSwitch;
    Button mCreateTaskBtn;
    TextView mDateText, mStartTimeText, mEndTimeText;
    TextInputLayout mTitleInput, mDescriptionInput;
    Calendar currentCalendar;
    Boolean mTaskHasReminder;

    int startHourSelected, startMinutesSelected;
    int endHourSelected, endMinutesSelected;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_task_layout, container, false);

        currentCalendar = Calendar.getInstance();

        InstantiateViews(v);

        // Deals with the initial texts and date
        StartFrag();

        HandleDatePicker();

        HandleTimePicker();
        return v;
    }
    private void InstantiateViews(View v){
        mStartTimeLayout = v.findViewById(R.id.start_time_btn);
        mEndTimeLayout = v.findViewById(R.id.end_time_btn);
        mDatePickLayout = v.findViewById(R.id.date_btn);
        mReminderSwitch = v.findViewById(R.id.create_task_reminder_switch);
        mCreateTaskBtn = v.findViewById(R.id.create_task_btn);
        mDateText = v.findViewById(R.id.create_task_date_text);
        mStartTimeText = v.findViewById(R.id.create_task_start_time_text);
        mEndTimeText = v.findViewById(R.id.create_task_end_time_text);
        mTitleInput = v.findViewById(R.id.task_new_title_input);
        mDescriptionInput = v.findViewById(R.id.task_new_description_input);

        mTaskHasReminder = true;
    }
    private void StartFrag(){
        currentCalendar = Calendar.getInstance();

        mDateText.setText(dateTextFormat.format(currentCalendar.getTime()));

        mStartTimeText.setText(timeIntervalTextFormat.format(currentCalendar.getTime()));

        currentCalendar.set(Calendar.HOUR, currentCalendar.get(Calendar.HOUR) + 1);

        mEndTimeText.setText(timeIntervalTextFormat.format(currentCalendar.getTime()));
    }
    private void HandleDatePicker(){
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choose a Date").build();

        mDatePickLayout.setOnClickListener(view -> {
            datePicker.show(getParentFragmentManager(), DATE_PICKER);
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String dateSelected = datePicker.getHeaderText();
            String[] parts = dateSelected.split(" ");
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0]));
            selectedCalendar.set(Calendar.MONTH, Integer.valueOf(parts[1])+1);
            selectedCalendar.set(Calendar.YEAR, Integer.valueOf(parts[2]));

            // Checks if the date selected is before the current Date
            if(CheckDateSelected(selectedCalendar)){
                mDateText.setText(dateTextFormat.format(selectedCalendar.getTime()));
            }
        });
    }
    private boolean CheckDateSelected(Calendar selectedCalendar){
        currentCalendar = Calendar.getInstance();

        if(currentCalendar.getTime().after(selectedCalendar.getTime())){
            return false;
        }
        return true;
    }
    private void HandleTimePicker(){
        String[] startTime = mStartTimeText.getText().toString().split(":");

        TimePickerDialog startTimePicker = CreateTimePicker(true, startTime);

        String[] endTime = mEndTimeText.getText().toString().split(":");

        TimePickerDialog endTimePicker = CreateTimePicker(false, endTime);

        // Show the time pickers when clicked
        mStartTimeLayout.setOnClickListener(view -> startTimePicker.show());
        mEndTimeLayout.setOnClickListener(view -> endTimePicker.show());
    }
    private TimePickerDialog CreateTimePicker(boolean isStartTime, String[] parts){

        TimePickerDialog timePicker = new TimePickerDialog(getContext(),
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

                    if(CheckTimeSelected(isStartTime, selectedTime)){
                        mStartTimeText.setText(timeIntervalTextFormat.format(selectedTime.getTime()));
                    }
                },
                Integer.valueOf(parts[0]),
                Integer.valueOf(parts[1]),
                false);
        if(isStartTime) timePicker.updateTime(startHourSelected, startMinutesSelected);
        else timePicker.updateTime(endHourSelected, endMinutesSelected);

        return timePicker;
    }
    private boolean CheckTimeSelected(boolean isStartTime, Calendar selectedTime){
        if(isStartTime){
            String[] endTime = mEndTimeText.getText().toString().split(":");
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTime[0]));
            endTimeCalendar.set(Calendar.MINUTE, Integer.valueOf(endTime[1]));

            //Check if selectedTime is after the endTime
            if(selectedTime.getTime().after(endTimeCalendar.getTime())){
                Toast.makeText(getContext(), "Start time needs to be before end time", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            String[] startTime = mStartTimeText.getText().toString().split(":");
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTime[0]));
            startTimeCalendar.set(Calendar.MINUTE, Integer.valueOf(startTime[1]));

            // Check if selectedTime is before start time
            if(selectedTime.getTime().before(startTimeCalendar.getTime())){
                Toast.makeText(getContext(), "End time needs to be after start time", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    public interface OnTaskCreatedListener{
        void insertNewTask(Task task);
    }
}
