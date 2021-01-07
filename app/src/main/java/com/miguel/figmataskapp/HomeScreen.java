package com.miguel.figmataskapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeScreen extends AppCompatActivity implements TaskRecyclerAdapter.OnTaskRemovedListener,
        CreateTaskFragment.OnTaskCreatedListener{
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String MAIN_TASKS_FRAG_MANAGER = "mainTasksFrag";
    public static final String CREATE_TASKS_FRAG_MANAGER = "createTasksFrag";
    public static final String CALENDAR_FRAG_MANAGER = "calendarFrag";

    OnMainTaskFragTaskChangedListener mainTasksFragListener;

    TaskViewModel taskViewModel;
    BottomNavigationView mNavigationView;
    FrameLayout mFragContainer;


    String mDate = DATE_FORMAT.format(Calendar.getInstance().getTime());
    ArrayList<Task> mDateTasksList = new ArrayList<>();

    // Countering the re-creation of fragments when the UI in the FrameLayout is changed
    final MainTasksFragment mMainTasksFrag = MainTasksFragment.newInstance(mDate, mDateTasksList);
    final CreateTaskFragment mCreateTaskFrag = new CreateTaskFragment();
    final CalendarFragment mCalendarFrag = new CalendarFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment mActiveFrag = mMainTasksFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_with_bottom_nav);



        // Add the fragments to the fragmentManager
        fragmentManager.beginTransaction().add(R.id.fragment_container, mCreateTaskFrag,
                CREATE_TASKS_FRAG_MANAGER).hide(mCreateTaskFrag).commit();

        fragmentManager.beginTransaction().add(R.id.fragment_container, mCalendarFrag,
                CALENDAR_FRAG_MANAGER).hide(mCalendarFrag).commit();

        // Don't hide this last one because it's going to be the first fragment to show up on the FrameLayout
        fragmentManager.beginTransaction().add(R.id.fragment_container, mMainTasksFrag,
                MAIN_TASKS_FRAG_MANAGER).commit();


        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(this.getApplication()))
                .get(TaskViewModel.class);

        mNavigationView = findViewById(R.id.bottom_navigation);
        mFragContainer = findViewById(R.id.fragment_container);

        // Set the listener to the MainTasksFragment
        setOnDateTaskListChangedListener((OnMainTaskFragTaskChangedListener) mMainTasksFrag);

        // Observe the LiveData and update the MainTasksFrag whenever it changes through the listener
        taskViewModel.getAllTasks().observe(this, tasks -> {
            mDateTasksList = getTasksAtDate(mDate, tasks);
            if(mainTasksFragListener!=null){
                mainTasksFragListener.updateDateTaskList(mDate, mDateTasksList);
            }
        });

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
        switch(item.getItemId()){
            case R.id.task_list_down_btn:

                fragmentManager.beginTransaction().hide(mActiveFrag).show(mMainTasksFrag).commit();
                mActiveFrag = mMainTasksFrag;

                return true;
            case R.id.create_task_bottom_btn:

                fragmentManager.beginTransaction().hide(mActiveFrag).show(mCreateTaskFrag).commit();
                mActiveFrag = mCreateTaskFrag;

                return true;
            case R.id.calendar_bottom_btn:

                fragmentManager.beginTransaction().hide(mActiveFrag).show(mCalendarFrag).commit();
                mActiveFrag = mCalendarFrag;

                return true;
        }

        return false;
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
        // Inserts the task
        taskViewModel.insert(task);


        Snackbar.make(getCurrentFocus(),"Task added", Snackbar.LENGTH_SHORT).show();
    }
    /*
    @Override
    public void deleteTask(Task task) {
        taskViewModel.delete(task);

        //Show a Snackbar asking if want to undo, if want to undo
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), R.string.snack_bar_undo_delete, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_delete, view -> {
            //If the user clicks on the undo button then insert the note back into database
            taskViewModel.insert(task);
        });
        snackbar.show();
    }*/

    public interface OnMainTaskFragTaskChangedListener {
        void updateDateTaskList(String date, List<Task> taskList);
    }
    public void setOnDateTaskListChangedListener(OnMainTaskFragTaskChangedListener listener){
        this.mainTasksFragListener = listener;
    }


}
