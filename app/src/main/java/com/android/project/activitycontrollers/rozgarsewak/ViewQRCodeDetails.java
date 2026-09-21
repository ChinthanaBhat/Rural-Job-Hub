package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.database.AppDatabaseHelper;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
//import com.android.project.Model.QRCode;
import com.android.project.model.QRCode;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ViewQRCodeDetails extends AppCompatActivity {
    TextView nameTV, aadharTV, bplTV, voterTV, genderTV, ageTV, mobileTV,panchayathTV,jobAllottedTV,bankTV, ifscTV, accountTV;
    ImageView iconIV;
    Labourer labourer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewqrcodedetails);
        initializeUIComponents();
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
        iconIV =  findViewById(R.id.image);
        bankTV = findViewById(R.id.bank);
        ifscTV = findViewById(R.id.ifsc);
        accountTV = findViewById(R.id.account);

    }

    public void viewAttendance(View view)
    {
        Intent intent = new Intent(this, ViewAttendanceActivity.class);
        intent.putExtra(Constants.ID_KEY, labourer.getLabourerID());
        startActivity(intent);

    }


    public void displayData() {
        String details = getIntent().getStringExtra("Details");
        Gson gson = new Gson();
        QRCode qrCode = gson.fromJson(details, QRCode.class);
        //AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
        //labourer = databaseHelper.getLabourerWithID(qrCode.getLabourerID());
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Labourers").child(qrCode.getLabourerID());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    labourer = snapshot.getValue(Labourer.class);// creating User object
                    labourer.setLabourerID(snapshot.getKey()); //set the id to the user object
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
                    Glide.with(ViewQRCodeDetails.this).load(labourer.getProfilePath())
                            .into(iconIV);

                    displayJobDetails();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        // Job job = databaseHelper.getJobWithID(jobID);
//        if (job == null) {
//            jobAllottedTV.setText("No job allotted for this labourer");
//        } else {
//            jobAllottedTV.setText(job.getTitle());
//        }


    }

    public void displayJobDetails() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Query the job-labourer table for the specific labourer ID
        database.child("LabourerToJob") // assuming this is the node where job-labourer mapping is stored
                .orderByChild("Labourer_Id") // The field name in the database that holds the labourer ID
                .equalTo(labourer.getLabourerID()) // The value to match (labourerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming jobID is a direct child of each labourer record
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String jobID = snapshot.child("id").getValue(String.class);
                                if (jobID == null) {
                                    jobAllottedTV.setText("No job allotted for this labourer");
                                } else {
                                    DatabaseReference database1 = FirebaseDatabase.getInstance().getReference();
                                    // Reference to the specific job node using the jobID
                                    DatabaseReference jobRef = database1.child("Jobs").child(jobID);
                                    // Fetch the job data once
                                    //String imagePath;
                                    jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Check if the job exists
                                            if (dataSnapshot.exists()) {
                                                // Retrieve the job details from the snapshot

                                                Job job = dataSnapshot.getValue(Job.class);// creating User object
                                                job.setId(dataSnapshot.getKey()); //set the id to the user object

                                                // Create a Job object and set its fields


                                                if (job == null)
                                                {
                                                    jobAllottedTV.setText("No Job Allotted");
                                                }
                                                else {
                                                    jobAllottedTV.setText(job.getTitle());
                                                }
                                            }


                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
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
    }


    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentRozgarSewak(null);
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


