package com.example.finalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NoteListAdapter extends ArrayAdapter<Note> {

    private static final String TAG = "NoteListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    //View of the list
    private static class ViewHolder {
        TextView createdAt;
        TextView notificationAt;
        ImageView alarm;
    }

    //Default constructor
    public NoteListAdapter(Context context, int resource, ArrayList<Note> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get values
        int id = getItem(position).getId();
        Calendar createdAt = getItem(position).getCreatedAt();
        String path = getItem(position).getPath();
        Calendar notificationAt = getItem(position).getNotificationAt();

        //Create a note object
        Note note = new Note (id, createdAt, path, notificationAt);

        //ViewHolder object
        ViewHolder holder;

        //Populate the view
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.createdAt = convertView.findViewById(R.id.textView3);
            holder.notificationAt = convertView.findViewById(R.id.textView4);
            holder.alarm = convertView.findViewById(R.id.alarmImage);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        //Create time view format (How the time will be presented as a text)
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        //The method returns 0 if the time represented by the argument is equal
        int equal = note.getCreatedAt().compareTo(note.getNotificationAt());
        holder.createdAt.setText("The note created at " + format.format(note.getCreatedAt().getTime()));

        //Check if the notification have been set
        if (equal != 0) {

            //If the notification time is passed
            if (System.currentTimeMillis() < note.getNotificationAt().getTimeInMillis())
                holder.notificationAt.setText("The notification at " + format.format(note.getNotificationAt().getTime()));
            else{
                holder.notificationAt.setVisibility(View.INVISIBLE);
                holder.alarm.setVisibility(View.INVISIBLE);
            }
        }

        //The notification not have been set
        else {
            holder.notificationAt.setVisibility(View.INVISIBLE);
            holder.alarm.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
