package com.miguel.figmataskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainTasksFragment extends Fragment {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");

    TextView mCurrentMonthYear;
    RecyclerView mDaysRecyclerView, mTasksRecyclerView;
    ImageButton mOptionsBtn;
    ImageView mPreviousDayBtn, mNextDayBtn;


    LinearLayoutManager linearLayoutManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tasks_list_fragment, container, false);


        //Instantiate views
        InstantiateViews(v);

        if(getArguments()!=null){
            //get the day selected when in the calendar, a string

        }
        StartPositions();
        CreateDaysRecyclerView(Calendar.getInstance());


        return v;
    }

    private void InstantiateViews(View v){
        mCurrentMonthYear = v.findViewById(R.id.currentMonthYear);
        mDaysRecyclerView = v.findViewById(R.id.day_selected_recycler_view);
        mTasksRecyclerView = v.findViewById(R.id.tasks_recycler_view);
        mOptionsBtn = v.findViewById(R.id.options_image_btn);
        mPreviousDayBtn = v.findViewById(R.id.previous_day_btn);
        mNextDayBtn = v.findViewById(R.id.next_day_btn);
    }
    private void StartPositions(){
        Calendar c = Calendar.getInstance();
        mCurrentMonthYear.setText(dateFormat.format(c.getTime()));
    }

    private void CreateDaysRecyclerView(Calendar c){
        // Create HashMap with the current month's days
        DaysRecyclerAdapter adapter = new DaysRecyclerAdapter(getContext(), CreateDayList(c));
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mDaysRecyclerView.setAdapter(adapter);
        mDaysRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private String[] CreateDayList(Calendar c){
        SimpleDateFormat df = new SimpleDateFormat("EE");

        //c = Calendar.getInstance();

        // Get number of days in the current month
        int numberDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] result = new String[numberDays];
        for(int i=0;i<numberDays;i++){
            c.set(Calendar.DAY_OF_MONTH, i);
            result[i] = df.format(c.getTime());
        }
        return result;
    }
}
