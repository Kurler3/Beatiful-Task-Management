package com.miguel.figmataskapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainTasksFragment extends Fragment implements HomeScreen.OnMainTaskFragTaskChangedListener{
    public static final String TAG = "tasksFragment";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");

    public static final String MAIN_TASKS_FRAGMENT_PASS_DATE = "passDate";
    public static final String MAIN_TASKS_FRAG_DATE_TASK_LIST = "passTaskList";

    Button mTodayBtn;
    TextView mCurrentMonthYear;
    RecyclerView mDaysRecyclerView, mTasksRecyclerView;
    ImageButton mOptionsBtn;
    ImageView mPreviousDayBtn, mNextDayBtn;
    PopupMenu mPopupMenu;
    ArrayList<Task> dateTaskList;
    String mDate;
    String mTodayDate;
    LinearLayoutManager linearLayoutManager;
    TaskRecyclerAdapter mTaskAdapter;
    DaysRecyclerAdapter mDaysAdapter;

    public static MainTasksFragment newInstance(String date, ArrayList<Task> taskList){
        MainTasksFragment fragment = new MainTasksFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MAIN_TASKS_FRAGMENT_PASS_DATE, date);
        bundle.putParcelableArrayList(MAIN_TASKS_FRAG_DATE_TASK_LIST, taskList);


        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tasks_list_fragment, container, false);


        //Instantiate views
        InstantiateViews(v);

        // Get arguments and deal with recycler views
        InitializeFrag();

        // Create the options menu
        CreateOptionsDropdownMenu();

        // Going to remove tasks on swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Task taskSwiped = mTaskAdapter.getTaskAt(viewHolder.getAdapterPosition());
                // Communicate with the homescreen and tell it to remove this task
                //mTaskRemovedListener.deleteTask(taskSwiped);
                if(mTaskAdapter.getTaskRemovedListener()!=null){
                    mTaskAdapter.getTaskRemovedListener().removeTask(taskSwiped, viewHolder.getAdapterPosition());
                }
            }
        }).attachToRecyclerView(mTasksRecyclerView);

        // Clicking on the today button will update the adapters and UI
        mTodayBtn.setOnClickListener(v1 -> onTodayBtnClicked());

        // Arrows for changing in between days without having to click on the items of the days recycler view
        mPreviousDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onArrowsClicked(true);
            }
        });
        mNextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onArrowsClicked(false);
            }
        });

        return v;
    }
    private void onArrowsClicked(boolean leftArrow){
        String[] parts = mDate.split("/");
        Calendar current = getCalendarFromDate(mDate);
        if(leftArrow){
            if(Integer.valueOf(parts[0])>1){
                current.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0])-1);
                mDate = HomeScreen.DATE_FORMAT.format(current.getTime());

                ArrayList<Task> todayTaskList = HomeScreen.getTasksAtDate(mDate, HomeScreen.mFullTasksList);
                updateOnCalendarDateChanged(current, todayTaskList);
            }
        }else{
            if(Integer.valueOf(parts[0])<current.getActualMaximum(Calendar.DAY_OF_MONTH)){
                current.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0])+1);
                mDate = HomeScreen.DATE_FORMAT.format(current.getTime());

                ArrayList<Task> todayTaskList = HomeScreen.getTasksAtDate(mDate, HomeScreen.mFullTasksList);
                updateOnCalendarDateChanged(current, todayTaskList);
            }
        }
    }
    private void onTodayBtnClicked(){
        ArrayList<Task> todayTaskList = HomeScreen.getTasksAtDate(mTodayDate, HomeScreen.mFullTasksList);
        updateOnCalendarDateChanged(getCalendarFromDate(mTodayDate), todayTaskList);
    }
    public static Calendar getCalendarFromDate(String date){
        String[] parts = date.split("/");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[0]));
        c.set(Calendar.MONTH, Integer.valueOf(parts[1])-1);
        c.set(Calendar.YEAR, Integer.valueOf(parts[2]));

        return c;
    }
    private void InitializeFrag(){
        mDate = getArguments().getString(MAIN_TASKS_FRAGMENT_PASS_DATE);
        mTodayDate = mDate;
        String[] dateParts = mDate.split("/");

        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateParts[0]));
        c.set(Calendar.MONTH, Integer.valueOf(dateParts[1])-1);
        c.set(Calendar.YEAR, Integer.valueOf(dateParts[2]));

        StartTitle(c);
        CreateDaysRecyclerView(c);


        // Get the Task List
        dateTaskList = getArguments().getParcelableArrayList(MAIN_TASKS_FRAG_DATE_TASK_LIST);

        CreateTaskRecyclerView(dateTaskList);

        Log.d(TAG, "Everything worked well");
    }

    private void InstantiateViews(View v){
        mCurrentMonthYear = v.findViewById(R.id.currentMonthYear);
        mDaysRecyclerView = v.findViewById(R.id.day_selected_recycler_view);
        mTasksRecyclerView = v.findViewById(R.id.tasks_recycler_view);
        mOptionsBtn = v.findViewById(R.id.options_image_btn);
        mPreviousDayBtn = v.findViewById(R.id.previous_day_btn);
        mNextDayBtn = v.findViewById(R.id.next_day_btn);
        mTodayBtn = v.findViewById(R.id.today_btn);

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
        int position = c.get(Calendar.DAY_OF_MONTH)-1;
        // Create HashMap with the current month's days
        mDaysAdapter = new DaysRecyclerAdapter((DaysRecyclerAdapter.OnDayItemSelectedListener) getActivity(), CreateDayList(c),
                position);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mDaysRecyclerView.setAdapter(mDaysAdapter);
        mDaysRecyclerView.setLayoutManager(linearLayoutManager);

        // Recycler view will scroll smoothly to the selected day
        RecyclerView.SmoothScroller smoothScroller = new
                LinearSmoothScroller(getContext()) {
                    @Override protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
        smoothScroller.setTargetPosition(position);

        mDaysRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
    }
    private String[] CreateDayList(Calendar c){
        SimpleDateFormat df = new SimpleDateFormat("EE");

        //c = Calendar.getInstance();

        // Get number of days in the current month
        int numberDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] result = new String[numberDays];
        for(int i=0;i<numberDays;i++){
            c.set(Calendar.DAY_OF_MONTH, i+1);
            result[i] = df.format(c.getTime());
        }



        return result;
    }
    @Override
    public void updateDateTaskList(String date, List<Task> updatedTasks) {
        if(mDate.equals(date)){
            dateTaskList = (ArrayList) updatedTasks;
            //should update the recycler view as well
            if(mTaskAdapter!=null){
                mTaskAdapter.setTaskArray((ArrayList<Task>) updatedTasks);
                mTaskAdapter.notifyDataSetChanged();
            }
        }
    }
    private void CreateTaskRecyclerView(ArrayList<Task> tasks){
        mTaskAdapter = new TaskRecyclerAdapter((TaskRecyclerAdapter.OnTaskAdapterListener) getActivity(), getContext(), tasks);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());

        mTasksRecyclerView.setLayoutManager(llm);
        mTasksRecyclerView.setAdapter(mTaskAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTasksRecyclerView.setAdapter(null);
        mTaskAdapter = null;
        mTasksRecyclerView = null;
    }

    public void updateFrag(Calendar date, ArrayList<Task> tasks){
        this.mDate = HomeScreen.DATE_FORMAT.format(date.getTime());
        this.dateTaskList = tasks;

        mDaysAdapter.updateAdapter(date.get(Calendar.DAY_OF_MONTH)-1, CreateDayList(date));

        mTaskAdapter.setTaskArray(tasks);
        mTaskAdapter.notifyDataSetChanged();
    }
    public void updateOnCalendarDateChanged(Calendar date, ArrayList<Task> tasks){
        this.mDate = HomeScreen.DATE_FORMAT.format(date.getTime());
        this.dateTaskList = tasks;

        // Change the title
        StartTitle(date);

        mDaysAdapter.updateAdapter(date.get(Calendar.DAY_OF_MONTH)-1, CreateDayList(date));
        mDaysAdapter.notifyDataSetChanged();

        mTaskAdapter.setTaskArray(tasks);
        mTaskAdapter.notifyDataSetChanged();

    }
    public TaskRecyclerAdapter getTaskAdapter(){
        return mTaskAdapter;
    }
}
