package com.android.project.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.project.model.Attendance;
import com.android.project.R;
//import com.android.project.database.AppDatabaseHelper;
//import com.android.project.model.Attendance;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
import com.android.project.utility.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewAttendanceListItemAdapter extends ArrayAdapter<Attendance> {
    private Activity context;
    List<Attendance> attendanceList;
    LayoutInflater inflater;
    Labourer labourer;

    public ViewAttendanceListItemAdapter(Activity context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder
    {
        TextView dateTV;
        TextView presentTV;
        TextView jobTV;
        TextView labourerTV;

        ImageView iconIV;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.viewattendancelist_item, null);
            holder.dateTV = view.findViewById(R.id.date);
            holder.presentTV = view.findViewById(R.id.present);
            holder.jobTV = view.findViewById(R.id.job);
            holder.labourerTV = view.findViewById(R.id.labourer);

            holder.iconIV = view.findViewById(R.id.profile);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }
        holder.presentTV.setText("Attendance:PRESENT");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference jobRef = database.child("Jobs").child(attendanceList.get(position).getJobID());
        // Fetch the job data once
        //String imagePath;
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the job exists
                if (dataSnapshot.exists()) {

                    Job job = dataSnapshot.getValue(Job.class);// creating User object
                    job.setId(dataSnapshot.getKey()); //set the id to the user object

                    // Create a Job object and set its fields


                    holder.jobTV.setText("Job:"+job.getTitle());

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Labourers");
        // Reference to the labourer node using the labourerID
        DatabaseReference labourerRef = dbRef.child(attendanceList.get(position).getLabourerID());

        // Use ValueEventListener to fetch the data
        labourerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the labourer with the specified ID exists
                if (dataSnapshot.exists()) {
                    // Extract the data from the snapshot
                    labourer = dataSnapshot.getValue(Labourer.class);// creating User object
                    labourer.setLabourerID(dataSnapshot.getKey()); //set the id to the user object
                    holder.labourerTV.setText("Labourer:"+labourer.getName());

                    String imagePath = labourer.getProfilePath();
                    Glide.with(context)
                            .load(imagePath)
                            .into(holder.iconIV);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read errors (e.g., permission issues)
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });

        holder.dateTV.setText("Date:"+attendanceList.get(position).getDate());
        return view;
    }

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList)
    {
        this.attendanceList = attendanceList;
    }

    @Override
    public int getCount() {
        return attendanceList.size();
    }

    @Override
    public Attendance getItem(int position) {
        return attendanceList.get(position);
    }

}


