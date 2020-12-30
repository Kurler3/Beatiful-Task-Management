package com.miguel.figmataskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {
    Button mConfirmBtn;
    CalendarView mCalendar;
    int day, month, year;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_fragment, container, false);

        mCalendar = v.findViewById(R.id.calendar_view);
        mConfirmBtn = v.findViewById(R.id.confirm_btn_calendar);

        mCalendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            day = i2;
            month = i1;
            year = i;
        });

        mConfirmBtn.setOnClickListener(view -> {
            // Somehow send the data chosen back to the tasks list and makes it show the tasks saved for that day.
            // Should force the fragment show in this frame layout to be changed back to the tasks list.
        });

        return v;
    }
}
