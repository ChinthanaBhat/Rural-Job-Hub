package com.android.project.activitycontrollers.fieldinspector;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.adapters.AttendanceListItemAdapter;
import com.android.project.model.Attendance;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MarkAttendanceActivity extends AppCompatActivity {
    ArrayList<Job> jobList = null;
    private Spinner jobSP;
    ArrayList<String> jobTitles = null;
    Button updateButton;
    private ListView listView = null;
    AttendanceListItemAdapter customAdapter;
    private TextView mNoLabourerView;
    ArrayList<Labourer> labourerList = null;
    ArrayList<String> labourerIdList = null;
    int completedCount = 0; // counter for completed requests

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markattendance);
        initializeUIComponents();
        labourerIdList = new ArrayList<>();
        labourerList = new ArrayList<>();
        jobList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Jobs");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Job job = dataSnapshot.getValue(Job.class);// creating User object
                        job.setId(dataSnapshot.getKey()); //set the id to the user object
                        jobList.add(job); //Adding the user object to the arraylist
                    }
                    populateSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MarkAttendanceActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void populateSpinner()
    {
        jobTitles = new ArrayList<>();
        for (Job job : jobList) {
            jobTitles.add(job.getTitle());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, jobTitles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSP.setAdapter(dataAdapter);

        jobSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Job job = jobList.get(position);
                labourerIdList.clear();
                labourerList.clear();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference labourersRef = databaseReference.child("LabourerToJob");

                // Query for workers who are allocated to a job (assuming "allottedForJob" is the key in Firebase).
                Query query = labourersRef.orderByChild("Job_Id").equalTo(job.getId());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String labourer_id = snapshot.child("Labourer_Id").getValue(String.class);
                            labourerIdList.add(labourer_id);
                        }
                        fetchLabourers();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                        Log.e("Firebase", "Error retrieving data", databaseError.toException());
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void initializeUIComponents() {
        // final AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
        jobSP = findViewById(R.id.joblist);
        updateButton = findViewById(R.id.button);
    }

    public void fetchLabourers()
    {
        final int totalRequests = labourerIdList.size(); // total Firebase queries to wait for
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Labourers");

        for (String labourerId : labourerIdList) {
            ref.child(labourerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Labourer labourer = snapshot.getValue(Labourer.class);
                    labourer.setLabourerID(snapshot.getKey());

                                labourerList.add(labourer);
                                completedCount++;



                    // After all the queries are done, update the UI
                    if (completedCount == totalRequests) {
                        populateListView(); // All queries completed
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle cancellation if needed
                    if (completedCount == totalRequests) {
                        populateListView(); // All queries completed
                    }
                }
            });
        }

    }


    public void populateListView() {
        mNoLabourerView = findViewById(R.id.no_labourer_text);
        mNoLabourerView.setText(Constants.NO_LABOURER_DESCRIPTION);

        if (labourerList.size() > 0) {
            mNoLabourerView.setVisibility(View.GONE);
            customAdapter = new AttendanceListItemAdapter(MarkAttendanceActivity.this, R.layout.attendancelist_item);
            customAdapter.setLabourerList(labourerList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);
            updateButton.setVisibility(View.VISIBLE);

        } else {
            mNoLabourerView.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);

            if (customAdapter != null) {
                reloadData();
            }
        }
    }

    public void reloadData() {
        labourerList.clear();
        customAdapter.setLabourerList(labourerList);
        customAdapter.notifyDataSetChanged();
    }

    public void update(View view) {

        if (jobList.size() > 0) {
            Attendance attendance = new Attendance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar date = Calendar.getInstance();
            String stringDate = dateFormatter.format(date.getTime());
            attendance.setDate(stringDate);

            Job job = jobList.get(jobSP.getSelectedItemPosition());
            attendance.setJobID(job.getId());
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            CheckBox cb;
            for (int x = 0; x < listView.getChildCount(); x++) {
                cb = listView.getChildAt(x).findViewById(R.id.present);
                if (true == cb.isChecked()) {
                    attendance.setLabourerID(labourerList.get(x).getLabourerID());

                    // Get a reference to the Firebase Realtime Database

                    // Create an attendance object

                    // Generate a new unique ID for the attendance record
                    String attendanceID = database.child("Attendance").push().getKey();

                    // Set the attendance data under the generated ID
                    database.child("Attendance").child(attendanceID).setValue(attendance)
                            .addOnSuccessListener(aVoid -> {
                                // Record was added successfully
                                Log.d("Firebase", "Attendance added successfully");
                            })
                            .addOnFailureListener(e -> {
                                // Failed to add the record
                                Log.e("Firebase", "Failed to add attendance", e);
                            });

                    // Return the unique ID of the new attendance record
                    //return attendanceID != null ? Long.parseLong(attendanceID) : -1;
                }
                Toast.makeText(getApplicationContext(), Constants.ATTENDANCE_MARKED_SUCCESSFULLY, Toast.LENGTH_LONG).show();

            }

        }



        else {
            Toast.makeText(getApplicationContext(), Constants.NO_JOBS, Toast.LENGTH_LONG).show();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentRozgarSewak(null);
                Intent i = new Intent(MarkAttendanceActivity.this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MarkAttendanceActivity.this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;
            case R.id.changePassword:
                Intent intent = new Intent(MarkAttendanceActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                android.app.AlertDialog.Builder helpDialogBuilder = new android.app.AlertDialog.Builder(MarkAttendanceActivity.this);
                helpDialogBuilder.setIcon(R.drawable.applogo);
                helpDialogBuilder.setTitle(R.string.app_name);
                helpDialogBuilder.setMessage(Constants.HELP_MESSAGE);
                helpDialogBuilder.create();
                helpDialogBuilder.show();
                return true;
            case R.id.feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.FEEDBACK_SUBJECT);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.FEEDBACK_MAILID});

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MarkAttendanceActivity.this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                }
                return true;

        }

        return false;
    }
}

