package com.miguel.figmataskapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String TASK_CHANNEL_ID = "taskChannel";
    public static final String TASK_CHANNEL_NAME = "Task Reminder";

    NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createTaskChannel();
        }
    }
    public void createTaskChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(TASK_CHANNEL_ID,
                TASK_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);

        getNotificationManager().createNotificationChannel(notificationChannel);
    }
    public NotificationManager getNotificationManager(){
        if(mManager==null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public NotificationCompat.Builder getTaskChannelNotification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(), TASK_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_image);
    }
}
