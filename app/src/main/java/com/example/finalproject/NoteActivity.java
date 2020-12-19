package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private SQLiteDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView image = findViewById(R.id.imageView1);
        int noteId = getIntent().getIntExtra("noteId", 0);
        db = new SQLiteDatabaseHandler(this);
        Note note = db.getNote(noteId);

        //Load the image into the memory by the path
        Bitmap bmp = BitmapFactory.decodeFile(note.getPath());

        //Set the image
        image.setImageBitmap(bmp);
    }

    //Click on the back button and return to the main activity
    public boolean onOptionsItemSelected(MenuItem item){
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(mainIntent, 0);
        return true;
    }
}
