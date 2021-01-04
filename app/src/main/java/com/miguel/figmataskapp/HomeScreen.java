package com.miguel.figmataskapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeScreen extends AppCompatActivity implements TaskRecyclerAdapter.OnTaskRemovedListener,
        CreateTaskFragment.OnTaskCreatedListener {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy");

    OnDateTaskListChanged mainTasksFragListener;

    TaskViewModel taskViewModel;
    BottomNavigationView mNavigationView;
    FrameLayout mFragContainer;

    String mDate;
    ArrayList<Task> mDateTasksList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_with_bottom_nav);

        // Temporary
        mDateTasksList = new ArrayList<>();
       // mDateTasksList.add(new Task("Title","Description",mDate,"11:22 AM", "12:00 AM", false,1));
        Calendar c = Calendar.getInstance();
        mDate = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
        //------------------

        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(this.getApplication()))
                .get(TaskViewModel.class);

        taskViewModel.getAllTasks().observe(this, tasks -> {
            mDateTasksList = getTasksAtDate(mDate, tasks);
            if(mainTasksFragListener!=null){
                mainTasksFragListener.updateDateTaskList(mDate, mDateTasksList);
            }
        });

        mNavigationView = findViewById(R.id.bottom_navigation);
        mFragContainer = findViewById(R.id.fragment_container);


        // Set the tasks list as the display when entering into this activity
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                MainTasksFragment.newInstance(mDate, mDateTasksList)).commit();

        mNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId()==mNavigationView.getSelectedItemId()) return false;
                    else return ChangeFragment(item);
                }
            };
    private boolean ChangeFragment(MenuItem item){
        Fragment selectedFrag = null;
        switch(item.getItemId()){
            case R.id.task_list_down_btn:
                // Temporary
                Calendar c = Calendar.getInstance();
                String date = new SimpleDateFormat("dd/mm/yyyy").format(c.getTime());
                //-------------------------


                selectedFrag = MainTasksFragment.newInstance(date,mDateTasksList);
                setOnDateTaskListChangedListener((OnDateTaskListChanged) selectedFrag);
                break;
            case R.id.create_task_bottom_btn:
                //Create Task frag
                selectedFrag = new CreateTaskFragment();
                break;
            case R.id.calendar_bottom_btn:
                selectedFrag = new CalendarFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFrag).commit();

        return true;
    }
    public ArrayList<Task> getTasksAtDate(String date, List<Task> fullTaskList){
        ArrayList<Task> filtered = new ArrayList<>();
        for(Task task : fullTaskList){
            if(task.getDateCreation().equals(date)){
                filtered.add(task);
            }
        }
        return filtered;
    }

    @Override
    public void removeTask(Task removedTask) {
        taskViewModel.delete(removedTask);

        //Show a Snackbar asking if want to undo, if want to undo
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), R.string.snack_bar_undo_delete, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_delete, view -> {
            //If the user clicks on the undo button then insert the note back into database
            taskViewModel.insert(removedTask);
        });
        snackbar.show();
    }

    @Override
    public void insertNewTask(Task task) {
        taskViewModel.insert(task);
    }

    public interface OnDateTaskListChanged{
        void updateDateTaskList(String date, List<Task> taskList);
    }
    public void setOnDateTaskListChangedListener(OnDateTaskListChanged listener){
        this.mainTasksFragListener = listener;
    }
}
