package com.miguel.figmataskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "firstScreen";

    Button mSkipBtn, mContinueBtn;
    ImageButton mBackBtn;
    ImageView mBubblesImage, mMainImageChange;
    TextView mTitleChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InstantiateViews();
        SetOnButtonsClickListeners();
    }

    private void InstantiateViews(){
        mSkipBtn = findViewById(R.id.skip_btn);
        mContinueBtn = findViewById(R.id.continue_btn);
        mBackBtn = findViewById(R.id.back_arrow_btn);
        mBubblesImage = findViewById(R.id.bubbles_first_screen);
        mTitleChange = findViewById(R.id.title_to_change);
        mMainImageChange = findViewById(R.id.main_image_change);

        // In the first screen there's no back button
        mBackBtn.setVisibility(View.INVISIBLE);
        mBackBtn.setClickable(false);
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

                MakeChanges(R.string.first_screen_title_2,R.drawable.continue_bubbles_2,
                        R.drawable.first_screen_image_2);

                mBackBtn.setVisibility(View.VISIBLE);
                mBackBtn.setClickable(true);
            } else if (currentScreen == 2) {

                MakeChanges(R.string.first_screen_title_3, R.drawable.continue_bubbles_3,
                        R.drawable.first_screen_image_3);

            } else {
                // If the current screen is the third, then clicking the continue button
                // Will make the user enter the tasks screen

                //TODO
                // Start an intent to the Tasks screen
            }
        }else{
            if (currentScreen == 2) {

                MakeChanges(R.string.first_screen_title_1, R.drawable.continue_bubbles_1,
                        R.drawable.first_screen_image_1);

                mBackBtn.setVisibility(View.INVISIBLE);
                mBackBtn.setClickable(false);
            } else if (currentScreen == 3) {

                MakeChanges(R.string.first_screen_title_2, R.drawable.continue_bubbles_2,
                        R.drawable.first_screen_image_2);

            }
        }
    }

    private  static Bitmap ConvertImageResourceToBitmap(Context context, int drawableResource){
        Bitmap icon;
        try {
             icon = BitmapFactory.decodeResource(context.getResources(),
                     drawableResource);
         }catch(Exception e){
             //if resource provided is invalid
             Log.d(TAG, "resource doesn't exist");
             return null;
         }
         return icon;
    }
    public static void ImageViewAnimatedChange(Context c, final ImageView v, final int newResource) {
        Bitmap new_image = ConvertImageResourceToBitmap(c, newResource);

        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
    private void MakeChanges(int stringResource, int nextBubbles, int nextMainImage){
        mTitleChange.setText(getResources().getString(stringResource));
        mBubblesImage.setImageResource(nextBubbles);

        //-------------------------------------------------
        ImageViewAnimatedChange(this, mMainImageChange, nextMainImage);
        //-----------------------------------------------
    }
}