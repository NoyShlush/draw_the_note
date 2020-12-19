package com.example.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//class extending the Broadcast Receiver
public class Alarm extends BroadcastReceiver {

    //the method will be triggered when the alarm is triggered
    @Override
    public void onReceive(Context context, Intent intent) {
        int noteId = intent.getIntExtra("noteId", 0);

        //Stop the alarm because the alarm already happened
        Intent stopAlarmIntent = new Intent(context, AlarmService.class);
        stopAlarmIntent.setAction(AlarmService.ACTION_REMOVE_ALARM);
        stopAlarmIntent.putExtra("noteId", noteId);
        context.startService(stopAlarmIntent);

        //Create notification
        NotificationHelper nh = new NotificationHelper(context);
        nh.createNotification("Draw the note","You have notification", noteId);

        Intent refreshIntent = new Intent(context, MainActivity.class);

        //Makes the refresh of the main activity more user friendly
        refreshIntent.setFlags(refreshIntent.FLAG_ACTIVITY_NEW_TASK | refreshIntent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(refreshIntent);
    }

}

