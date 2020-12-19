package com.example.finalproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DrawActivity extends AppCompatActivity {

    private PaintView paintView;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private SQLiteDatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drow);
        //Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new SQLiteDatabaseHandler(this);
        paintView = findViewById(R.id.paintView);

        //Deceleration of the paintView
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }

    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Select item in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(mainIntent, 0);
                return true;
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
            case R.id.thickness:

                //Create the dialog
                final AlertDialog.Builder builderThickness = new AlertDialog.Builder(DrawActivity.this);
                builderThickness.setTitle("Set a thickness of the brash");
                final View customView = getLayoutInflater().inflate(R.layout.thickness_seekbar, null);
                builderThickness.setView(customView)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SeekBar seekBar =  customView.findViewById(R.id.seekBar);
                                int size = seekBar.getProgress();
                                paintView.thickness(size);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialogThickness = builderThickness.create();
                dialogThickness.show();
                return true;
            case R.id.color:

                //Create the dialog
                AlertDialog.Builder builderColor = new AlertDialog.Builder(DrawActivity.this);
                builderColor.setTitle("Set a color of the brash");
                final String[] colors = new String[]{
                        "Red",
                        "Green",
                        "Blue",
                        "Yellow",
                        "Black"
                };

                builderColor.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedColor = Arrays.asList(colors).get(which);
                        paintView.color(getColorInt(selectedColor));
                    }
                });

                AlertDialog dialogColor = builderColor.create();
                dialogColor.show();
                return true;
            case R.id.save:
                final Calendar calendar = Calendar.getInstance();
                final Calendar datetime = Calendar.getInstance();

                //Create the dialog
                final AlertDialog.Builder builderSave = new AlertDialog.Builder(DrawActivity.this);
                builderSave.setTitle("Do you want to save?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final AlertDialog.Builder builderNotification = new AlertDialog.Builder(DrawActivity.this);
                                builderNotification.setTitle("Do you want set notification?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //Select the time
                                                TimePickerDialog timePickerDialog = new TimePickerDialog(DrawActivity.this, new TimePickerDialog.OnTimeSetListener() {

                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                        datetime.set(Calendar.MINUTE, minute);
                                                        datetime.set(Calendar.SECOND, 0);

                                                        //Check if the user set pass time
                                                        if(datetime.getTimeInMillis() >= calendar.getTimeInMillis()){


                                                            //Add the note to the db
                                                            String path = paintView.saveImage(DrawActivity.this);
                                                            Note newNote = new Note(0, calendar, path, datetime);
                                                            db.addNote(newNote);

                                                            //Get the id of the note (it will be the last)
                                                            ArrayList <Note> last = db.allNotes();
                                                            int lastId = last.get(last.size() - 1).getId();

                                                            Intent alarmIntent = new Intent(DrawActivity.this, AlarmService.class);
                                                            alarmIntent.putExtra("noteId", lastId);
                                                            alarmIntent.putExtra("time", newNote.getNotificationAt().getTimeInMillis());
                                                            alarmIntent.setAction(AlarmService.ACTION_START_FOREGROUND_SERVICE);
                                                            startService(alarmIntent);

                                                            Intent resultIntent = new Intent();
                                                            setResult(Activity.RESULT_OK, resultIntent);
                                                            finish();
                                                        }else{
                                                            Toast.makeText(DrawActivity.this,"You set pass time\nThe notification is not saved", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }, mHour, mMinute, true);

                                                //Disable cancel button
                                                timePickerDialog.setButton(timePickerDialog.BUTTON_NEGATIVE, null, (DialogInterface.OnClickListener) null);
                                                timePickerDialog.show();

                                                DatePickerDialog datePickerDialog = new DatePickerDialog(DrawActivity.this, new DatePickerDialog.OnDateSetListener() {

                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                                        datetime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                        datetime.set(Calendar.MONDAY, monthOfYear);
                                                        datetime.set(Calendar.YEAR, year);

                                                    }
                                                }, mYear, mMonth, mDay);

                                                //Disable cancel button
                                                datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, null, (DialogInterface.OnClickListener) null);

                                                //Disable passed date
                                                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                                datePickerDialog.show();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //Add the note to the db
                                                String path = paintView.saveImage(DrawActivity.this);
                                                Note newNote = new Note(0, calendar, path, calendar);
                                                db.addNote(newNote);

                                                Intent resultIntent = new Intent();
                                                setResult(Activity.RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        });
                                AlertDialog dialogNotification = builderNotification.create();
                                dialogNotification.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialogSave = builderSave.create();
                dialogSave.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getColorInt (String name){
        switch(name) {
            case "Red":
                return Color.RED;
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            case "Yellow":
                return Color.YELLOW;
            case "Black":
                return Color.BLACK;
        }
        return 0;
    }
}