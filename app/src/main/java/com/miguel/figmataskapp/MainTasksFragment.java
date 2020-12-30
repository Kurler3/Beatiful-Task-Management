package com.miguel.figmataskapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainTasksFragment extends Fragment {
    public static final String TAG = "tasksFragment";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");


    public static final String MAIN_TASKS_FRAGMENT_PASS_DATE = "passDate";

    TaskViewModel taskViewModel;
    TextView mCurrentMonthYear;
    RecyclerView mDaysRecyclerView, mTasksRecyclerView;
    ImageButton mOptionsBtn;
    ImageView mPreviousDayBtn, mNextDayBtn;
    PopupMenu mPopupMenu;
    List<Task> taskList;

    LinearLayoutManager linearLayoutManager;


    public static MainTasksFragment newInstance(String date){
        MainTasksFragment fragment = new MainTasksFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MAIN_TASKS_FRAGMENT_PASS_DATE, date);

        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tasks_list_fragment, container, false);


        //Instantiate views
        InstantiateViews(v);

        // Create the TaskViewModel and get the list of tasks to populate the recyclerview
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if(getArguments().getString(MAIN_TASKS_FRAGMENT_PASS_DATE)!=null){
            // date will be passed in format dd/mm/yyyy
            String datePassed = getArguments().getString(MAIN_TASKS_FRAGMENT_PASS_DATE);
            String[] dateParts = datePassed.split("/");

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateParts[0]));
            c.set(Calendar.MONTH, Integer.valueOf(dateParts[1]));
            c.set(Calendar.YEAR, Integer.valueOf(dateParts[2]));

            StartTitle(c);
            CreateDaysRecyclerView(c);

            // Get the Task List
            taskList = taskViewModel.getTasksAtDate(datePassed);

            Log.d(TAG, "Everything worked well");
        }
        else{
            Calendar c = Calendar.getInstance();
            StartTitle(c);
            CreateDaysRecyclerView(c);

            String date = new SimpleDateFormat("dd/mm/yyyy").format(c.getTime());

            // Get the Task List
            taskList = taskViewModel.getTasksAtDate(date);
        }

        CreateOptionsDropdownMenu();



        return v;
    }

    private void InstantiateViews(View v){
        mCurrentMonthYear = v.findViewById(R.id.currentMonthYear);
        mDaysRecyclerView = v.findViewById(R.id.day_selected_recycler_view);
        mTasksRecyclerView = v.findViewById(R.id.tasks_recycler_view);
        mOptionsBtn = v.findViewById(R.id.options_image_btn);
        mPreviousDayBtn = v.findViewById(R.id.previous_day_btn);
        mNextDayBtn = v.findViewById(R.id.next_day_btn);

        // Experimenting
        mPopupMenu = new PopupMenu(getContext(), mOptionsBtn);
    }
    private void CreateOptionsDropdownMenu(){
        Menu menu = mPopupMenu.getMenu();
        mPopupMenu.getMenuInflater().inflate(R.menu.options_menu_dropdown, menu);

        mPopupMenu.setOnMenuItemClickListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.settings_dropdown_item:
                    // Launch settings activity
                    return true;
                case R.id.about_dropdown_item:
                    // Launch about activity
                    return true;
                default:
                    return true;
            }
        });

        mOptionsBtn.setOnClickListener(view -> {
            mPopupMenu.show();
        });
    }
    private void StartTitle(Calendar c){
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
