package com.miguel.figmataskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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

public class HomeScreen extends AppCompatActivity implements TaskRecyclerAdapter.OnTaskAdapterListener,
        CreateTaskFragment.OnTaskCreatedListener, DaysRecyclerAdapter.OnDayItemSelectedListener,
        CalendarFragment.OnCalendarDateChangedListener{

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String MAIN_TASKS_FRAG_MANAGER = "mainTasksFrag";
    public static final String CREATE_TASKS_FRAG_MANAGER = "createTasksFrag";
    public static final String CALENDAR_FRAG_MANAGER = "calendarFrag";

    OnMainTaskFragTaskChangedListener mainTasksFragListener;

    RelativeLayout mHomeScreenView;
    TaskViewModel taskViewModel;
    BottomNavigationView mNavigationView;
    FrameLayout mFragContainer;

    String mTodayDate;
    String mDate = DATE_FORMAT.format(Calendar.getInstance().getTime());
    ArrayList<Task> mDateTasksList = new ArrayList<>();
    public static ArrayList<Task> mFullTasksList = new ArrayList<>();

    // Countering the re-creation of fragments when the UI in the FrameLayout is changed
    MainTasksFragment mMainTasksFrag = MainTasksFragment.newInstance(mDate, mDateTasksList);
    final CreateTaskFragment mCreateTaskFrag = new CreateTaskFragment();
    final CalendarFragment mCalendarFrag = CalendarFragment.newInstance(mDate);
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment mActiveFrag = mMainTasksFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_with_bottom_nav);

        // Will use this for the "today" button
        mTodayDate = DATE_FORMAT.format(Calendar.getInstance().getTime());

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
        mHomeScreenView = findViewById(R.id.home_screen_layout);

        // Set the listener to the MainTasksFragment
        setOnDateTaskListChangedListener((OnMainTaskFragTaskChangedListener) mMainTasksFrag);
        // Set the listener to the CalendarFragment
        mCalendarFrag.setDateChangedListener((CalendarFragment.OnCalendarDateChangedListener) this );

        // Observe the LiveData and update the MainTasksFrag whenever it changes through the listener
        taskViewModel.getAllTasks().observe(this, tasks -> {
            mFullTasksList = (ArrayList<Task>) tasks;
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
    public static ArrayList<Task> getTasksAtDate(String date, List<Task> fullTaskList){
        ArrayList<Task> filtered = new ArrayList<>();
        for(Task task : fullTaskList){
            if(task.getDateCreation().equals(date)){
                filtered.add(task);
            }
        }
        return filtered;
    }

    @Override
    public void removeTask(Task removedTask, int position) {
        taskViewModel.delete(removedTask);

        // If the task removed had a reminder
        if(removedTask.isHasReminder()){
            HandleAlert(removedTask, false);
        }

        //Show a Snackbar asking if want to undo
        Snackbar snackbar = Snackbar.make(mHomeScreenView, R.string.snack_bar_undo_delete, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_delete, view -> {
            //If the user clicks on the undo button then insert the note back into database
            taskViewModel.insert(removedTask);

            mMainTasksFrag.getTaskAdapter().insertTaskInAdapter(removedTask, position);
            // If the task had a reminder then create it again when removing is undone
            if(removedTask.isHasReminder()){
                HandleAlert(removedTask, true);
            }
        });
        snackbar.show();
    }
    @Override
    public void insertNewTask(Task task) {
        // Inserts the task
        taskViewModel.insert(task);

        // If the task has a reminder then create a notification for the day of creation and start hour of it.
        // Differentiate the notifications by the task's id.
        if(task.isHasReminder()){
            HandleAlert(task, true);
        }

        Snackbar snackbar = Snackbar.make(mHomeScreenView,"Task added", Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }
    private void HandleAlert(Task task, boolean isCreating){
        // If the task has a reminder then create a notification for the day of creation and start hour of it.
        // Differentiate the notifications by the task's id.

        // In format dd/MM/yyyy
        String dateCreation = task.getDateCreation();
        // In format 09:00
        String startTime = task.getStartTime();
        String[] timeParts = startTime.split(":");
        // Set a calendar to use in the alarm manager
        Calendar alarmDate = MainTasksFragment.getCalendarFromDate(dateCreation);
        alarmDate.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeParts[0]));
        alarmDate.set(Calendar.MINUTE, Integer.valueOf(timeParts[1]));
        alarmDate.set(Calendar.SECOND, 0);

        // Data that will be passed to the broadcast receiver
        String title = task.getTitle();
        String description = task.getDescription();
        int taskID = task.getId();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
            // Passing extras
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_ID_NOTIFICATION, taskID);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_TITLE_NOTIFICATION, title);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_DESCRIPTION_NOTIFICATION, description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    taskID,
                    intent,
                    0);
        if(isCreating){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
        }
    }

    private void AlertUpdate(Task task){
        // If the task has a reminder then create a notification for the day of creation and start hour of it.
        // Differentiate the notifications by the task's id.

        // In format dd/MM/yyyy
        String dateCreation = task.getDateCreation();
        // In format 09:00
        String startTime = task.getStartTime();
        String[] timeParts = startTime.split(":");
        // Set a calendar to use in the alarm manager
        Calendar alarmDate = MainTasksFragment.getCalendarFromDate(dateCreation);
        alarmDate.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeParts[0]));
        alarmDate.set(Calendar.MINUTE, Integer.valueOf(timeParts[1]));

        // Data that will be passed to the broadcastreceiver
        String title = task.getTitle();
        String description = task.getDescription();

        int taskID = task.getId();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        // Passing extras
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_ID_NOTIFICATION, taskID);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_TITLE_NOTIFICATION, title);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_DESCRIPTION_NOTIFICATION, description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                taskID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), pendingIntent);
    }
    @Override
    public void changeDay(int dayMonth) {

        Calendar c = MainTasksFragment.getCalendarFromDate(mDate);
        c.set(Calendar.DAY_OF_MONTH, dayMonth+1);

        mDate = DATE_FORMAT.format(c.getTime());

        // Instead get a list of tasks that matches this date from the database
        mDateTasksList = getTasksAtDate(mDate, mFullTasksList);

        mMainTasksFrag.updateFrag(c, mDateTasksList);

        mCalendarFrag.setDate(mDate);
    }

    @Override
    public void updateCalendarDate(String newDate) {
        String[] parts = newDate.split("/");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0]));
        c.set(Calendar.MONTH, Integer.valueOf(parts[1])-1);
        c.set(Calendar.YEAR, Integer.valueOf(parts[2]));

        mDate = newDate;
        mDateTasksList = getTasksAtDate(mDate, mFullTasksList);

        mMainTasksFrag.updateOnCalendarDateChanged(c, mDateTasksList);

        mCalendarFrag.setDate(mDate);

        Snackbar snackbar = Snackbar.make(mHomeScreenView, "Date changed!", Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    public interface OnMainTaskFragTaskChangedListener {
        void updateDateTaskList(String date, List<Task> taskList);
    }
    public void setOnDateTaskListChangedListener(OnMainTaskFragTaskChangedListener listener){
        this.mainTasksFragListener = listener;
    }

    @Override
    public void onTaskClicked(Task task) {
        Intent intent = new Intent(this, ViewTaskActivity.class);

        intent.putExtra(ViewTaskActivity.PASS_TASK, task);

        startActivityForResult(intent, ViewTaskActivity.TASK_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ViewTaskActivity.TASK_ACTIVITY_REQUEST && resultCode==RESULT_OK){
            Task updatedTask = data.getParcelableExtra(ViewTaskActivity.RESULT_TASK);
            boolean reminderChange = data.getBooleanExtra(ViewTaskActivity.TASK_REMINDER_CHANGED, false);

            taskViewModel.update(updatedTask);

            mMainTasksFrag.updateFrag(MainTasksFragment.getCalendarFromDate(mDate),
                    getTasksAtDate(mDate, mFullTasksList));

            //Create or delete the alarm if there was a change in alarm. Update if there was no change in alarm
            if(reminderChange){
                if(updatedTask.isHasReminder()){
                    // Create the alarm
                    HandleAlert(updatedTask, true);
                }else{
                    // Delete the alarm
                    HandleAlert(updatedTask, false);
                }
            }else{
                // If the task had a reminder then update it
                if(updatedTask.isHasReminder()){
                    AlertUpdate(updatedTask);
                }
            }

            Snackbar snackbar = Snackbar.make(mHomeScreenView, "Task Updated!", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Ok", v -> snackbar.dismiss());
            snackbar.show();
        }
    }
}
