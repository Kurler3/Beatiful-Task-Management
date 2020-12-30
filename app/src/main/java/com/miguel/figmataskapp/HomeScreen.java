package com.miguel.figmataskapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeScreen extends AppCompatActivity {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy");

    BottomNavigationView mNavigationView;
    FrameLayout mFragContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_with_bottom_nav);

        mNavigationView = findViewById(R.id.bottom_navigation);
        mFragContainer = findViewById(R.id.fragment_container);

        // Temporary
        Calendar c = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd/mm/yyyy").format(c.getTime());
        //------------------

        // Set the tasks list as the display when entering into this activity
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                MainTasksFragment.newInstance(currentDate)).commit();

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
                selectedFrag = MainTasksFragment.newInstance(date);
                break;
            case R.id.create_task_bottom_btn:
                //Create Task frag
                break;
            case R.id.calendar_bottom_btn:
                selectedFrag = new CalendarFragment();
                break;
        }


        //For debugging
        if(selectedFrag==null) return true;
        //---------------------

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFrag).commit();

        return true;
    }
}
