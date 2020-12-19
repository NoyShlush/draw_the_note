package com.example.finalproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import static com.example.finalproject.AlarmService.ACTION_STOP_ALARM;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final String NOTIFICATION_CHANNEL_ID = "FinalProject";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void createNotification(String title, String message, int noteId) {

        Intent actionIntent = new Intent(mContext, NoteActivity.class);
        actionIntent.putExtra("noteId", noteId);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent actionPendingIntent = PendingIntent.getActivity(mContext, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create the basic notification
        mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(actionPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //Check if the version of the Android is higher than O (8.0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "DRAW_THE_NOTE", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());
    }

    //This notification for service with stop all button
    public NotificationCompat.Builder createServiceNotification(String title, String message, int noteId){
        Intent resultIntent = new Intent(mContext , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0 , resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create stop button in the notification
        Intent stopAlarmIntent = new Intent(mContext, AlarmService.class);
        stopAlarmIntent.putExtra("noteId", noteId);
        stopAlarmIntent.setAction(ACTION_STOP_ALARM);
        PendingIntent pendingStopAlarmIntent = PendingIntent.getService(mContext, 0, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //The icon of the button will not appear in the O version
        NotificationCompat.Action stopAlarmAction = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_off_black_24dp, "Stop all alarms", pendingStopAlarmIntent).build();

        //Create the basic notification
        mBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)
                .addAction(stopAlarmAction);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //Check if the version of the Android is higher than O (8.0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "DRAW_THE_NOTE", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        return mBuilder;
    }
}

