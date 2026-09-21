package com.android.project.activitycontrollers.fieldinspector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.WorkProgress;
import com.android.project.model.Job;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InspectWorkProgressActivity extends AppCompatActivity {
    EditText descriptionET;
    ArrayList<Job> jobList = null;
    private Spinner jobSP;
    ArrayList<String> jobTitles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_work_progress);
        jobList = new ArrayList<>();
        jobSP = findViewById(R.id.joblist);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Jobs");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Job job = dataSnapshot.getValue(Job.class);// creating User object
                        job.setId(dataSnapshot.getKey()); //set the id to the user object
                        jobList.add(job); //Adding the user object to the arraylist
                    }
                    populateSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InspectWorkProgressActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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



        }


    public void submit(View view){
        descriptionET=findViewById(R.id.description);

        String description=descriptionET.getText().toString();

        WorkProgress inspectWorkProgress = new WorkProgress();
        inspectWorkProgress.setDescription(description);
        inspectWorkProgress.setJobId(jobList.get(jobSP.getSelectedItemPosition()).getId());
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String stringDate = dateFormatter.format(date.getTime());
        inspectWorkProgress.setDate(stringDate);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String key=databaseReference.child("Work_Progress").push().getKey();
        databaseReference.child("Work_Progress").child(key).setValue(inspectWorkProgress);
        inspectWorkProgress.setId(key);
        if (inspectWorkProgress.getId() != null) {
            Toast.makeText(this, Constants.DETAILS_SUCESS, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, FieldinspectorHomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, WorkProgress.class);
            startActivity(intent);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentFieldInspector(null);
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
            case R.id.changePassword:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                android.app.AlertDialog.Builder helpDialogBuilder = new android.app.AlertDialog.Builder(this);
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
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                }
                return true;

        }

        return false;
    }

}