package com.example.finalproject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    //Default value
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NotesDB";
    private static final String TABLE_NAME = "Notes";
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_PATH = "path";
    private static final String KEY_NOTIFICATION_AT = "notificationAt";
    private static final String[] COLUMNS = { KEY_ID, KEY_CREATED_AT, KEY_PATH, KEY_NOTIFICATION_AT};

    //Default constructor
    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //On the first install the app will create a DB with the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Notes (id INTEGER PRIMARY KEY AUTOINCREMENT, createdAt INTEGER, path TEXT, notificationAt INTEGER )";

        //Run the command in the string
        db.execSQL(CREATION_TABLE);
    }

    //If the table is not exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    //Delete one note from the table (The search is by id)
    public void deleteOne(int id) {

        //Get write permission on the DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] {Integer.toString(id)});
        db.close();
    }

    //Get one note from the db by the id
    public Note getNote(int id) {

        //Get read permission on the DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, " id = ?", new String[] {Integer.toString(id)}, null,null,null,null);

        if (cursor != null)
            cursor.moveToFirst();

        //Create note, put all values and return it
        Note note = new Note();
        Calendar tempCalendar1 = Calendar.getInstance();
        Calendar tempCalendar2 = Calendar.getInstance();
        note.setId(Integer.parseInt(cursor.getString(0)));
        tempCalendar1.setTimeInMillis(Long.parseLong(cursor.getString(1)));
        note.setCreatedAt(tempCalendar1);
        note.setPath(cursor.getString(2));
        tempCalendar2.setTimeInMillis(Long.parseLong(cursor.getString(3)));
        note.setNotificationAt(tempCalendar2);

        return note;
    }

    // Get the list of all notes
    public ArrayList<Note> allNotes() {

        ArrayList<Note> notes = new ArrayList<Note>();

        //Get read permission on the DB
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                Calendar tempCalendar1 = Calendar.getInstance();
                Calendar tempCalendar2 = Calendar.getInstance();
                note.setId(Integer.parseInt(cursor.getString(0)));
                tempCalendar1.setTimeInMillis(Long.parseLong(cursor.getString(1)));
                note.setCreatedAt(tempCalendar1);
                note.setPath(cursor.getString(2));
                tempCalendar2.setTimeInMillis(Long.parseLong(cursor.getString(3)));
                note.setNotificationAt(tempCalendar2);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        return notes;
    }

    //Add new note to the DB
    public void addNote(Note note) {

        //Get write permission on the DB
        SQLiteDatabase db = this.getWritableDatabase();

        //Value store to insert in to the DB
        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT, note.getCreatedAt().getTimeInMillis());
        values.put(KEY_PATH, note.getPath());
        values.put(KEY_NOTIFICATION_AT, note.getNotificationAt().getTimeInMillis());

        //Insert in the table the values of the note
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    //Update the norificationAt after cancel the alarm
    public void updateNotificationNote(int noteId){

        //Get write permission on the DB
        SQLiteDatabase db = this.getWritableDatabase();

        //Value store to insert in to the DB
        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICATION_AT, System.currentTimeMillis());

        db.update(TABLE_NAME, values, "id = ?", new String[] { String.valueOf(noteId) });
        db.close();
    }
}
