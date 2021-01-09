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

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class CalendarFragment extends Fragment {
    public static final String CALENDAR_CURRENT_DATE = "passDate";

    Button mConfirmBtn;
    CalendarView mCalendar;
    int day, month, year;
    String mDate;
    String mDateChoose;
    OnCalendarDateChangedListener dateChangedListener;

    public static CalendarFragment newInstance(String date){
        CalendarFragment calendarFragment = new CalendarFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CALENDAR_CURRENT_DATE, date);

        calendarFragment.setArguments(bundle);
        return calendarFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_fragment, container, false);

        mCalendar = v.findViewById(R.id.calendar_view);
        mConfirmBtn = v.findViewById(R.id.confirm_btn_calendar);

        // Set the initial selected calendar day
        if(getArguments()!=null){
            mDate = getArguments().getString(CALENDAR_CURRENT_DATE);

            ChangeCalendarViewDate(mDate);
        }else{
            mDate = HomeScreen.DATE_FORMAT.format(Calendar.getInstance().getTime());
        }


        mCalendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            day = i2;
            month = i1;
            year = i;

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, i2);
            c.set(Calendar.MONTH, i1);
            c.set(Calendar.YEAR, i);

            mDateChoose = HomeScreen.DATE_FORMAT.format(c.getTime());
        });

        mConfirmBtn.setOnClickListener(view -> {
            if(mDateChoose !=null){
                dateChangedListener.updateCalendarDate(mDateChoose);
            }
        });

        return v;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
        ChangeCalendarViewDate(mDate);
    }

    public void ChangeCalendarViewDate(String date){
        String parts[] = date.split("/");

        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0]));
        c.set(Calendar.MONTH, Integer.valueOf(parts[1])-1);
        c.set(Calendar.YEAR, Integer.valueOf(parts[2]));

        mCalendar.setDate(c.getTime().getTime());
    }
    public interface OnCalendarDateChangedListener{
        void updateCalendarDate(String newDate);
    }
    public void setDateChangedListener(OnCalendarDateChangedListener listener){
        this.dateChangedListener = listener;
    }
}
