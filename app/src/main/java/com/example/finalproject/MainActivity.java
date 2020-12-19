package com.example.finalproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;
    private NoteListAdapter adapter;
    private ArrayList<Note> notesList;
    private ListView listView;
    private static final int PERMISSIONS_STORAGE = 0;
    private static final int REG_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new SQLiteDatabaseHandler(this);

        listView = findViewById(R.id.listView);
        notesList = db.allNotes();
        adapter = new NoteListAdapter(this, R.layout.adapter_view_layout, notesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //Short click on the list will present the note
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the id of the note from the list by the clicked position
                int noteId = notesList.get(position).getId();
                Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class);
                noteIntent.putExtra("noteId", noteId);
                startActivityForResult(noteIntent, REG_REQUEST_CODE);
            }
        });

        //Long click will delete the note from the DB and cancel the alarm
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the id of the note from the list by the clicked position
                int noteId = notesList.get(position).getId();

                //Delete image from the internal memory of the device
                File deleteFile = new File(db.getNote(noteId).getPath());
                if (deleteFile.exists()) {
                    if (!deleteFile.delete()) {
                        Log.e("FILE","No file to delete");
                    }
                }

                //Check if the alarm should be removed from the service
                if (System.currentTimeMillis() < db.getNote(noteId).getNotificationAt().getTimeInMillis()) {
                    Intent alarmIntent = new Intent(MainActivity.this, AlarmService.class);
                    alarmIntent.putExtra("noteId", noteId);
                    alarmIntent.setAction(AlarmService.ACTION_REMOVE_ALARM);
                    startService(alarmIntent);
                }

                db.deleteOne(noteId);

                notesList = db.allNotes();
                adapter = new NoteListAdapter(MainActivity.this, R.layout.adapter_view_layout, notesList);
                listView.setAdapter(adapter);

                Toast.makeText(MainActivity.this, "The note deleted", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        //Add new note button and open DrawActivity
        ImageButton floatButton;
        floatButton = findViewById(R.id.imageButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drowIntent = new Intent(MainActivity.this, DrawActivity.class);
                startActivityForResult(drowIntent, REG_REQUEST_CODE);
            }
        });

        //Request permission to the storage (Only write permission must be allowed by the user)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                notesList = db.allNotes();
                adapter = new NoteListAdapter(this, R.layout.adapter_view_layout, notesList);
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        notesList = db.allNotes();
        adapter = new NoteListAdapter(this, R.layout.adapter_view_layout, notesList);
        listView.setAdapter(adapter);
    }

    //Check if the user grant the write permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSIONS_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this,"The application can't work with the permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}
