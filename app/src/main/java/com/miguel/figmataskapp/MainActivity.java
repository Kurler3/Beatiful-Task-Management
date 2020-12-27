package com.miguel.figmataskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button mSkipBtn, mContinueBtn;
    ImageButton mBackBtn;
    ImageView mBubblesImage;
    TextView mTitleChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InstantiateViews();
    }

    private void InstantiateViews(){
        mSkipBtn = findViewById(R.id.skip_btn);
        mContinueBtn = findViewById(R.id.continue_btn);
        mBackBtn = findViewById(R.id.back_arrow_btn);
        mBubblesImage = findViewById(R.id.bubbles_first_screen);
        mTitleChange = findViewById(R.id.title_to_change);

        // In the first screen there's no back button
        mBackBtn.setVisibility(View.INVISIBLE);
    }

    private void SetOnButtonsClickListeners(){
        // Skip Button
        mSkipBtn.setOnClickListener(view -> {
            //TODO
            //Start an intent to the tasks screen
        });

        // Continue Button
        mContinueBtn.setOnClickListener(view -> {
            //Change the bubbles image and the title
            ChangeScreen(GetCurrentScreen(), true);
        });

        // Back Button
        mBackBtn.setOnClickListener(view -> {
            ChangeScreen(GetCurrentScreen(), false);
        });
    }
    private int GetCurrentScreen(){
        String currentTitle = mTitleChange.getText().toString();

        if(currentTitle==getResources().getString(R.string.first_screen_title_1))
            return 1;
        else if(currentTitle==getResources().getString(R.string.first_screen_title_2))
            return 2;
        else
            return 3;
    }
    private void ChangeScreen(int currentScreen, boolean goingForward){
        if(goingForward) {
            if (currentScreen == 1) {
                mTitleChange.setText(getResources().getString(R.string.first_screen_title_2));
                mBubblesImage.setBackgroundResource(R.drawable.continue_bubbles_2);
                mBackBtn.setVisibility(View.VISIBLE);
            } else if (currentScreen == 2) {
                mTitleChange.setText(getResources().getString(R.string.first_screen_title_3));
                mBubblesImage.setBackgroundResource(R.drawable.continue_bubbles_3);
            } else {
                // If the current screen is the third, then clicking the continue button
                // Will make the user enter the tasks screen

                //TODO
                // Start an intent to the Tasks screen
            }
        }else{
            if (currentScreen == 2) {
                mTitleChange.setText(getResources().getString(R.string.first_screen_title_1));
                mBubblesImage.setBackgroundResource(R.drawable.continue_bubbles_1);
                mBackBtn.setVisibility(View.INVISIBLE);
            } else if (currentScreen == 3) {
                mTitleChange.setText(getResources().getString(R.string.first_screen_title_2));
                mBubblesImage.setBackgroundResource(R.drawable.continue_bubbles_2);
            }
        }
    }
}