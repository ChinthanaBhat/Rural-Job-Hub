package com.android.project.activitycontrollers.labourer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
//import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.android.project.model.Labourer;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.database.AppDatabaseHelper;
import com.android.project.adapters.LabourerListItemAdapter;
import com.android.project.model.Job;
//import com.android.project.model.Labourer;
import com.android.project.model.QRCode;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.rozgarsewak.ViewJobsActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//import com.google.gson.Gson;

public class ViewJobCardActivity extends AppCompatActivity {
    TextView nameTV, aadharTV, bplTV, voterTV, genderTV, ageTV, mobileTV, panchayathTV, jobAllottedTV, bankTV, ifscTV, accountTV;
    ImageView iconIV;
    ArrayList<Labourer> labourerList = new ArrayList<>();
    Labourer labourer=new Labourer();
    String jobID = null;
    Job job = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewjobcard);
        initializeUIComponents();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("LabourerToJob");

        // Query the job-labourer table for the specific labourer ID
        database// assuming this is the node where job-labourer mapping is stored
                .orderByChild("Labourer_Id") // The field name in the database that holds the labourer ID
                .equalTo(labourer.getLabourerID()) // The value to match (labourerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming jobID is a direct child of each labourer record
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                jobID = snapshot.child("Job_Id").getValue(String.class);
                                fetchJobDetails();
                                break;


                                // Use jobID as needed
                            }
                        } else {
                            // Handle case where no job was found for the given labourerID
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database errors
                    }
                });

        displayData();
    }

    public void initializeUIComponents() {
        nameTV = findViewById(R.id.name);
        bplTV = findViewById(R.id.bpl);
        aadharTV = findViewById(R.id.aadhar);
        voterTV = findViewById(R.id.voter);
        genderTV = findViewById(R.id.gender);
        ageTV = findViewById(R.id.age);
        mobileTV = findViewById(R.id.mobile);
        panchayathTV = findViewById(R.id.panchayath);
        jobAllottedTV = findViewById(R.id.job);
        bankTV = findViewById(R.id.bank);
        ifscTV = findViewById(R.id.ifsc);
        accountTV = findViewById(R.id.account);

        iconIV = findViewById(R.id.image);
    }

    public void displayData() {

        Labourer labourer = ((AppInstance) getApplicationContext()).getCurrentLabourer();

        nameTV.setText(labourer.getName());
        bplTV.setText(labourer.getBplCardNumber());
        aadharTV.setText(labourer.getAadharID());
        voterTV.setText(labourer.getVoterID());
        genderTV.setText(labourer.getGender());
        ageTV.setText(String.valueOf(labourer.getAge()));
        mobileTV.setText(labourer.getMobile());
        panchayathTV.setText(labourer.getGramPanchayath());
        bankTV.setText(labourer.getBank());
        ifscTV.setText(labourer.getIfsc());
        accountTV.setText(labourer.getAccount());
        if (job == null)
        {
            jobAllottedTV.setText("No Job Allotted");
        }
        else {
            jobAllottedTV.setText(job.getTitle());
        }

        String imagePath = labourer.getProfilePath();
        Glide.with(this)
                .load(imagePath)
                .into(iconIV);


    }

    public void fetchJobDetails()
    {

        //long jobID = databaseHelper.getJobIDForLabourer(labourer.getLabourerID());
        //Job job = databaseHelper.getJobWithID(jobID);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Reference to the specific job node using the jobID
        DatabaseReference jobRef = databaseRef.child("Jobs").child(jobID);
        // Fetch the job data once
        //String imagePath;
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the job exists
                if (dataSnapshot.exists()) {
                    // Retrieve the job details from the snapshot
//                    String title = dataSnapshot.child("title").getValue(String.class);
//                    String description = dataSnapshot.child("description").getValue(String.class);
//                    String funds = dataSnapshot.child("fundsAllotted").getValue(String.class);
//                    int duration = dataSnapshot.child("durationInDays").getValue(Integer.class);
//                    int numberOfLabourers = dataSnapshot.child("numberOfLabourers").getValue(Integer.class);

                    job = dataSnapshot.getValue(Job.class);// creating User object
                    job.setId(dataSnapshot.getKey()); //set the id to the user object
                    displayData();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
            });

    

       

    }

    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentLabourer(null);
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
