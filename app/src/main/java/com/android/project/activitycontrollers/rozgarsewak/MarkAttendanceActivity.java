package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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

//import com.android.project.Model.Job;
import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.adapters.AttendanceListItemAdapter;
import com.android.project.model.Attendance;
//import com.android.project.model.Labourer;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MarkAttendanceActivity extends AppCompatActivity {
    ArrayList<Job> jobList = null;

    private Spinner jobSP;
    ArrayList<String> jobTitles = null;
    Button updateButton;


    private ListView listView = null;

    AttendanceListItemAdapter customAdapter;
    private TextView mNoLabourerView;
    ArrayList<Labourer> labourerList = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markattendance);
        initializeUIComponents();
    }

    public void initializeUIComponents() {
        // final AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
        jobSP = findViewById(R.id.joblist);
        updateButton = findViewById(R.id.button);

//        jobList = databaseHelper.getJobList();
//        jobTitles = new ArrayList<>();
//        for (Job job : jobList) {
//            jobTitles.add(job.getTitle());
//        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, jobTitles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSP.setAdapter(dataAdapter);


        jobSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Job job = jobList.get(position);
                //labourerList = databaseHelper.getLabourerListForJob(job.getJobID());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                // Reference to the job's labourers list
                DatabaseReference jobLabourersRef = db.child("jobs").child(String.valueOf(job.getId())).child("labourers");

                // Listen for changes to the labourers for the given job
                jobLabourersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Create a list to hold the labourers
                        ArrayList<Labourer> labourers = new ArrayList<>();

                        // Iterate through the labourer IDs in the job
                        for (DataSnapshot labourerSnapshot : dataSnapshot.getChildren()) {
                            String labourerID = labourerSnapshot.getKey(); // labourerID is the key

                            // Fetch the labourer details using the labourer ID
                            getLabourerDetails(labourerID, labourers);
                        }

                        // You now have the full list of Labourers for the job
                        // You can pass the list to your activity or fragment
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle potential errors here
                        Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }




    private void getLabourerDetails(String labourerID, final ArrayList<Labourer> labourers) {
        DatabaseReference labourerRef = FirebaseDatabase.getInstance().getReference().child("labourers").child(labourerID);

        labourerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the labourer object
                Labourer labourer = dataSnapshot.getValue(Labourer.class);
                if (labourer != null) {
                    labourers.add(labourer); // Add to the list
                }

                // You can now handle the list of labourers, e.g., display them in a UI
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                Log.w("Firebase", "loadLabourer:onCancelled", databaseError.toException());
            }
        });

        populateListView();

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
//        attendance.setDate(Calendar.getInstance());
//        Job job = jobList.get(jobSP.getSelectedItemPosition());
//        attendance.setJobID(job.getJobID());
            //AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);

            CheckBox cb;
            for (int x = 0; x < listView.getChildCount(); x++) {
                cb = listView.getChildAt(x).findViewById(R.id.present);
                if (true == cb.isChecked()) {
                    attendance.setLabourerID(labourerList.get(x).getLabourerID());
                    // long attendanceID = databaseHelper.addAttendance(attendance);
                    //attendance.setAttendanceID(attendanceID);

                    // Get a reference to the Firebase Realtime Database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                    // Create an attendance object
                    Attendance attendanceData = new Attendance();
                    attendanceData.setDate(attendance.getDate()); // Assume Attendance class has a setter
                    attendanceData.setLabourerID(attendance.getLabourerID());
                    attendanceData.setJobID(attendance.getJobID());

                    // Generate a new unique ID for the attendance record
                    String attendanceID = database.child("attendances").push().getKey();

                    // Set the attendance data under the generated ID
                    database.child("attendances").child(attendanceID).setValue(attendanceData)
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
            }

        }

        //Toast.makeText(getApplicationContext(), Constants.ATTENDANCE_MARKED_SUCCESSFULLY, Toast.LENGTH_LONG).show();

    }

//    else
//    {
//        Toast.makeText(getApplicationContext(), Constants.NO_JOBS, Toast.LENGTH_LONG).show();
//    }


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

