package com.android.project.activitycontrollers.pradhan;


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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.rozgarsewak.ChangePasswordActivity;
import com.android.project.adapters.WorkProgressListItemAdapter;
import com.android.project.model.WorkProgress;
import com.android.project.model.Job;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewWorkProgressActivity extends AppCompatActivity {
    ArrayList<Job> jobList = null;
    private Spinner jobSP;
    ArrayList<String> jobTitles = null;
    Button updateButton;
    private ListView listView = null;
    WorkProgressListItemAdapter customAdapter;
    private TextView mNoContentsView;
    ArrayList<WorkProgress> workProgressList = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewworkprogress);
        initializeUIComponents();
        workProgressList = new ArrayList<>();
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
                Toast.makeText(ViewWorkProgressActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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
                workProgressList.clear();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference labourersRef = databaseReference.child("Work_Progress");

                // Query for workers who are allocated to a job (assuming "allottedForJob" is the key in Firebase).
                Query query = labourersRef.orderByChild("jobId").equalTo(job.getId());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            WorkProgress workProgress = snapshot.getValue(WorkProgress.class);
                            workProgress.setId(snapshot.getKey());
                            workProgressList.add(workProgress);
                        }
                        populateListView();
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
    }


    public void populateListView() {
        mNoContentsView = findViewById(R.id.no_contents_text);
        mNoContentsView.setText(Constants.NO_WORK_PROGRESS);

        if (workProgressList.size() > 0) {
            mNoContentsView.setVisibility(View.GONE);
            customAdapter = new WorkProgressListItemAdapter(ViewWorkProgressActivity.this, R.layout.workprogresslist_item);
            customAdapter.setWorkProgressList(workProgressList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);

        } else {
            mNoContentsView.setVisibility(View.VISIBLE);

            if (customAdapter != null) {
                reloadData();
            }
        }
    }

    public void reloadData() {
        workProgressList.clear();
        customAdapter.setWorkProgressList(workProgressList);
        customAdapter.notifyDataSetChanged();
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
                Intent i = new Intent(ViewWorkProgressActivity.this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(ViewWorkProgressActivity.this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;
            case R.id.changePassword:
                Intent intent = new Intent(ViewWorkProgressActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                android.app.AlertDialog.Builder helpDialogBuilder = new android.app.AlertDialog.Builder(ViewWorkProgressActivity.this);
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
                    Toast.makeText(ViewWorkProgressActivity.this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                }
                return true;

        }

        return false;
    }
}

