package com.android.project.activitycontrollers.pradhan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
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
import com.android.project.model.Labourer;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.model.Labourer;
//import com.android.project.UserSelectionActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ApproveLabourerActivity extends AppCompatActivity {
    private TextView nameTV, mobileTV, ageTV, genderTV, gramPanchayathTV, labourerIDTV;
    private String labourerID;
    private Labourer labourer = null;
    private ImageView profileIV;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvelabourer);
        initializeUIComponents();


        labourerID = getIntent().getStringExtra("id");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Labourers").child(labourerID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                labourer = dataSnapshot.getValue(Labourer.class);
                labourer.setLabourerID(dataSnapshot.getKey());
                displayData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    public void initializeUIComponents() {
        nameTV = findViewById(R.id.name);
        mobileTV = findViewById(R.id.mobile);
        labourerIDTV = findViewById(R.id.id);
        ageTV = findViewById(R.id.age);
        genderTV = findViewById(R.id.gender);
        gramPanchayathTV = findViewById(R.id.panchayath);
        profileIV = findViewById(R.id.profile);
    }

    public void displayData() {
        nameTV.setText(labourer.getName());
        mobileTV.setText(labourer.getMobile());
        ageTV.setText(String.valueOf(labourer.getAge()));
        genderTV.setText(labourer.getGender());
        gramPanchayathTV.setText(labourer.getGramPanchayath());
        labourerIDTV.setText(labourerID);
        Glide.with(this).load(labourer.getProfilePath())
                .into(profileIV);

    }



    public void approve(View view) {
        labourer.setApproved(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Labourers").child(labourer.getLabourerID());
        Map<String,Object> updates = new HashMap<>();
        updates.put("approved",true);
        myRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ApproveLabourerActivity.this,"Labourer approved successfully",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ApproveLabourerActivity.this,PradhanHomeActivity.class);
                startActivity(intent);
            }
        });


        sendSms(labourer.getMobile(), "Pradhan has approved your registration request using Android App App");
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
