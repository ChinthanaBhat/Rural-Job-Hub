package com.android.project.activitycontrollers.pradhan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.labourer.LabourerHomeActivity;
import com.android.project.activitycontrollers.labourer.RegisterForSchemeActivity;
import com.android.project.model.Job;
import com.android.project.model.Labourer;
import com.android.project.R;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.model.Labourer;
//import com.android.project.pradhan.project.AppDatabaseHelper;
//import com.android.project.pradhan.project.pradhan.AdminChangePasswordActivity;
//import com.android.project.pradhan.project.utility.AppInstance;
//import com.android.project.pradhan.project.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LabourerDetailsActivity extends AppCompatActivity {
    TextView nameTV, aadharTV, bplTV, voterTV, genderTV, ageTV, mobileTV,panchayathTV;
    ImageView iconIV;

    private String labourerID;
    private Labourer labourer = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourerdetails);
        initializeUIComponents();
        labourerID = getIntent().getStringExtra(Constants.ID_KEY);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Labourers").child(labourerID);


        // Use ValueEventListener to fetch the data
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the labourer with the specified ID exists
                if (dataSnapshot.exists()) {
                    // Extract the data from the snapshot
                    labourer = dataSnapshot.getValue(Labourer.class);// creating User object
                    labourer.setLabourerID(dataSnapshot.getKey()); //set the id to the user object
                    displayData();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read errors (e.g., permission issues)
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });

    }

    public void initializeUIComponents() {
        nameTV = findViewById(R.id.name);
        mobileTV = findViewById(R.id.mobile);
        bplTV = findViewById(R.id.bpl);
        aadharTV = findViewById(R.id.aadhar);
        voterTV = findViewById(R.id.voter);
        genderTV = findViewById(R.id.gender);
        ageTV = findViewById(R.id.age);
        mobileTV = findViewById(R.id.mobile);
        panchayathTV = findViewById(R.id.panchayath);
        iconIV =  findViewById(R.id.image);

    }

    public void displayData()
    {
        nameTV.setText(labourer.getName());
        bplTV.setText(labourer.getBplCardNumber());
        aadharTV.setText(labourer.getAadharID());
        voterTV.setText(labourer.getVoterID());
        genderTV.setText(labourer.getGender());
        ageTV.setText(String.valueOf(labourer.getAge()));
        mobileTV.setText(labourer.getMobile());
        panchayathTV.setText(labourer.getGramPanchayath());
        bplTV.setText(labourer.getBplCardNumber());
        aadharTV.setText(labourer.getAadharID());
        voterTV.setText(labourer.getVoterID());
        Glide.with(this).load(labourer.getProfilePath())
                .into(iconIV);


    }

    public void issueJobCard(View view) {
        if (labourer.isJobCardIssued() == true)
        {
            Toast.makeText(getApplicationContext(), Constants.JOB_CARD_IS_ALREADY_ISSUED, Toast.LENGTH_LONG).show();
        }
        else {
            labourer.setJobCardIssued(true);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Labourers").child(labourer.getLabourerID());
            Map<String,Object> updates = new HashMap<>();
            updates.put("jobCardIssued",true);
            myRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(LabourerDetailsActivity.this,"Job Card issued successfully",Toast.LENGTH_LONG).show();
                    Intent intent = new
                            Intent(LabourerDetailsActivity.this, PradhanHomeActivity.class);
                    startActivity(intent);
                }
            });
        }
            Toast.makeText(getApplicationContext(), Constants.JOBCARD_ISSUED_SUCCESSFULLY, Toast.LENGTH_LONG).show();


                sendSms(labourer.getMobile(), "Pradhan has issued Job Card using Android App");
                // Permission has already been granted

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


    private void sendSms(String phonenumber, String message) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> msgArray = smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(phonenumber, null, msgArray, null, null);
            //Toast.makeText(getApplicationContext(), "Message Sent",Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}