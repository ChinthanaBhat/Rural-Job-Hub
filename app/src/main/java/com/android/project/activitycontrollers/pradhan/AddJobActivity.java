package com.android.project.activitycontrollers.pradhan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.Job;
import com.android.project.R;
//import com.android.project.UserSelectionActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.example.giftbuy.UserSelectionActivity;

public class AddJobActivity extends AppCompatActivity {
    private EditText titleET, descriptionET, numberOfLabourersET, durationET, fundsET;
    private Job job = null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjobdetails);
        initializeUIComponents();
    }

    public void initializeUIComponents() {
        numberOfLabourersET = findViewById(R.id.req_labourers);
        titleET = findViewById(R.id.title);
        descriptionET = findViewById(R.id.description);
        durationET = findViewById(R.id.duration);
        fundsET = findViewById(R.id.funds);
    }

    public void addJob(View view) {
        initializeUIComponents();
        String title = titleET.getText().toString().trim();
        String description = descriptionET.getText().toString().trim();
        String numberOfLabourers = numberOfLabourersET.getText().toString().trim();
        String duration = durationET.getText().toString().trim();
        String funds = fundsET.getText().toString().trim();

        if (title.length() == 0) {
            titleET.setError(Constants.MISSING_TITLE);
            titleET.requestFocus();
        } else if (description.length() == 0) {
            descriptionET.setError(Constants.MISSING_DESCRIPTION);
            descriptionET.requestFocus();
        } else if (numberOfLabourers.length() == 0) {
            numberOfLabourersET.setError(Constants.MISSING_NUMBER_OF_LABOURERS);
            numberOfLabourersET.requestFocus();
        } else if (duration.length() == 0) {
            durationET.setError(Constants.MISSING_DURATION);
            durationET.requestFocus();
        }
        else if (funds.length() == 0) {
            fundsET.setError(Constants.MISSING_FUNDS);
            fundsET.requestFocus();
        }
        else {
            job = new Job();
            job.setTitle(title);
            job.setDescription(description);
            job.setDurationInDays(Integer.parseInt(duration));
            job.setFundsAllotted(funds);
            job.setNumberOfLabourers(Integer.parseInt(numberOfLabourers));
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            String key = databaseReference.child("Jobs").push().getKey();
            databaseReference.child("Jobs").child(key).setValue(job);
            job.setId(key);
            if(job.getId() != null) {
                Toast.makeText(AddJobActivity.this, "Job added succesfully", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                Toast.makeText(AddJobActivity.this, "Error occured while adding the job", Toast.LENGTH_LONG).show();
            }
        }
    }



    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;

            case R.id.logout:
                ((AppInstance) getApplicationContext()).setAdminUser(false);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.changePassword:
                Intent intent = new Intent(this, AdminChangePasswordActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
