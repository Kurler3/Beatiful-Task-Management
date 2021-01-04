package com.miguel.figmataskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateTaskFragment extends Fragment {
    private static final SimpleDateFormat dateTextFormat = new SimpleDateFormat("dd, MMMM, yyyy");
    private static final SimpleDateFormat timeIntervalTextFormat = new SimpleDateFormat("KK:mm a");


    RelativeLayout mStartTimeLayout, mEndTimeLayout, mDatePickLayout;
    SwitchMaterial mReminderSwitch;
    Button mCreateTaskBtn;
    TextView mDateText, mStartTimeText, mEndTimeText;
    TextInputLayout mTitleInput, mDescriptionInput;

    Boolean mTaskHasReminder;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_task_layout, container, false);


        InstantiateViews(v);

        // Deals with the initial texts and date
        StartFrag();





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
        Calendar c = Calendar.getInstance();

        mDateText.setText(dateTextFormat.format(c.getTime()));

        mStartTimeText.setText(timeIntervalTextFormat.format(c.getTime()));

        c.set(Calendar.HOUR, c.get(Calendar.HOUR) + 1);

        mEndTimeText.setText(timeIntervalTextFormat.format(c.getTime()));
    }

    public interface OnTaskCreatedListener{
        void insertNewTask(Task task);
    }
}
