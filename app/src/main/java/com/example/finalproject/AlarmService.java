package com.example.finalproject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;


public class AlarmService extends Service {

    private SQLiteDatabaseHandler db;

    //List of all notes with alarm
    private ArrayList<Integer> alarmNotes = new ArrayList<>();
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_ALARM = "ACTION_STOP_ALARM";
    public static final String ACTION_REMOVE_ALARM = "ACTION_REMOVE_ALARM";

    public AlarmService() {}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();
            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    int noteId = intent.getIntExtra("noteId", 0);
                    long time = intent.getLongExtra("time", 0);
                    setAlarm(time, noteId);
                    alarmNotes.add(noteId);
                    startForegroundService(noteId);
                    break;
                case ACTION_STOP_ALARM:

                    //Refresh screen when the user stop all alarms
                    Intent refreshIntent = new Intent(this, MainActivity.class);

                    //Makes the refresh of the main activity more user friendly
                    refreshIntent.setFlags(refreshIntent.FLAG_ACTIVITY_NEW_TASK | refreshIntent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(refreshIntent);
                    stopAllAlarms();
                    stopForegroundService();
                    break;
                case ACTION_REMOVE_ALARM:
                    int removeNoteId = intent.getIntExtra("noteId", 0);

                    //Remove by the index of the object due to the fact that both are int
                    alarmNotes.remove(alarmNotes.indexOf(removeNoteId));
                    cancelAlarm(removeNoteId);

                    //If no alarms in the array list then the service it stop
                    if (alarmNotes.isEmpty())
                        stopForegroundService();
                    break;
                default:
                    Log.e("SERVICE_ERROR", "No action set to the service");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //Start foreground service.
    private void startForegroundService(int noteId)
    {
        // Create notification default intent.
        db = new SQLiteDatabaseHandler(this);
        NotificationHelper nh = new NotificationHelper(this);
        Notification notification = nh.createServiceNotification("Draw the note","You set a new alarm", noteId).build();

        // Start foreground service with notification as an object
        startForeground(1, notification);
    }

    //Stop foreground service.
    private void stopForegroundService()
    {
        // Stop foreground service
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    //Create an alarm
    private void setAlarm(long time, int noteId) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, Alarm.class);
        alarmIntent.putExtra("noteId", noteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteId, alarmIntent, 0);
        am.set(AlarmManager.RTC, time, pendingIntent);
    }

    //Stop all run over all alarms and cancel them
    private void stopAllAlarms() {

        //Cancel all the alarms
        for (int i = 0; i < alarmNotes.size(); i++) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, Alarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmNotes.get(i), alarmIntent, 0);
            am.cancel(pendingIntent);

            //Update the notification to remove the notification from the view
            db.updateNotificationNote(alarmNotes.get(i));
        }
        alarmNotes.clear();
    }

    //Cancel only one alarm
    private void cancelAlarm(int noteId) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteId, alarmIntent, 0);
        am.cancel(pendingIntent);

        //Update the notification to remove the notification from the view
        db.updateNotificationNote(noteId);
    }
}
