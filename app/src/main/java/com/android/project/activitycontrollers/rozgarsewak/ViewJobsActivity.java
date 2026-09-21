package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.android.project.Model.Job;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.activitycontrollers.pradhan.AdminChangePasswordActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.pradhan.AdminChangePasswordActivity;
import com.android.project.adapters.JobListItemAdapter;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
//import com.android.project.pradhan.AdminChangePasswordActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewJobsActivity extends AppCompatActivity {
    private ListView listView = null;

    JobListItemAdapter customAdapter;
    List<Job> jobList;
    private TextView mNoJobsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewjoblist);
        mNoJobsView = findViewById(R.id.no_job_text);
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
                    populateListView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewJobsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });



    }

    public void populateListView() {
        if(jobList.size() > 0) {
            mNoJobsView.setVisibility(View.GONE);
            customAdapter = new  JobListItemAdapter(this,R.layout.joblist_item);
            customAdapter.setJobList(jobList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);

        } else {
            mNoJobsView.setVisibility(View.VISIBLE);
            if(customAdapter!= null) {
                reloadData();
            }
        }
    }

    public  void reloadData() {
        jobList.clear();
        customAdapter.setJobList(jobList);
        customAdapter.notifyDataSetChanged();
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.changePassword:
                Intent intent = new Intent(this, AdminChangePasswordActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                ((AppInstance) getApplicationContext()).setAdminUser(false);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;
        }
        return false;
    }
}

