package com.miguel.figmataskapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_TASK_ID_NOTIFICATION = "taskIdNotification";
    public static final String EXTRA_TASK_TITLE_NOTIFICATION = "taskTitleNotification";
    public static final String EXTRA_TASK_DESCRIPTION_NOTIFICATION = "taskDescriptionNotification";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Different for every task
        int id = intent.getIntExtra(EXTRA_TASK_ID_NOTIFICATION, -1);
        String title = intent.getStringExtra(EXTRA_TASK_TITLE_NOTIFICATION);
        String description = intent.getStringExtra(EXTRA_TASK_DESCRIPTION_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getTaskChannelNotification(title, description);
        notificationHelper.getNotificationManager().notify(id, builder.build());
    }
}
