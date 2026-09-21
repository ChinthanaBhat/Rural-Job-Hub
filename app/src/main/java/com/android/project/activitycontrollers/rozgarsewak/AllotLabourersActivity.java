package com.android.project.activitycontrollers.rozgarsewak;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.android.project.Model.Job;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.labourer.LabourerLoginActivity;
import com.android.project.activitycontrollers.labourer.LabourerSignupActivity;
import com.android.project.adapters.AllotLabourerListItemAdapter;
//import com.android.project.model.Labourer;
import com.android.project.model.RozgarSewak;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllotLabourersActivity extends AppCompatActivity {
    ArrayList<Job> jobList = null;

    private Spinner jobSP;
    ArrayList<String> jobTitles = null;


    private ListView listView = null;

    AllotLabourerListItemAdapter customAdapter;
    private TextView mNoLabourerView;
    ArrayList<Labourer> labourerList = null;
    Button updateButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allotlabourers);
        initializeUIComponents();
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
                Toast.makeText(AllotLabourersActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initializeUIComponents() {
        jobSP = findViewById(R.id.joblist);
        updateButton = findViewById(R.id.button);
        jobList = new ArrayList<>();
        labourerList = new ArrayList<>();
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

        //checkIfTimeElapsed();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Labourers");

        // Query to get the list of labourers where certain conditions are met
        Query query = database.orderByChild("approved").equalTo(true);
//                .orderByChild(Constants.LABOURER_IS_REGISTERED_FOR_SCHEME_COLUMN).equalTo(1)
//                .orderByChild(Constants.LABOURER_IS_JOBCARD_ISSUED_COLUMN).equalTo(1)
//                .orderByChild(Constants.LABOURER_ALLOTTED_FOR_JOB_COLUMN).equalTo(0);

        // Listener to fetch data
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate over the snapshot to extract each labourer data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Labourer labourer = snapshot.getValue(Labourer.class);
                    labourer.setLabourerID(snapshot.getKey());
                    if (labourer != null &&
                            labourer.isRegisteredForScheme() == true &&
                            labourer.isJobCardIssued()== true &&
                            labourer.isAllottedForJob() == false) {
                        labourerList.add(labourer);
                    }
                }
                populateListView();


                // Use the labourerList as needed (for example, update the UI or process data)
            }

            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error as needed
            }


        });

    }





    public void populateListView() {
        mNoLabourerView = findViewById(R.id.no_labourer_text);
        mNoLabourerView.setText(Constants.NO_LABOURER_AVAILABLE_DESCRIPTION);

        if (labourerList.size() > 0) {
            mNoLabourerView.setVisibility(View.GONE);
            customAdapter = new AllotLabourerListItemAdapter(this, R.layout.allotlabourerlist_item);
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
            Job job = jobList.get(jobSP.getSelectedItemPosition());
            RozgarSewak rozgarSewak = ((AppInstance) getApplicationContext()).getCurrentRozgarSewak();

            CheckBox cb;
            for (int x = 0; x < listView.getChildCount(); x++) {
                cb = listView.getChildAt(x).findViewById(R.id.present);
                if (true == cb.isChecked()) {
                    labourerList.get(x).setAllottedForJob(true);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String key = databaseReference.child("LabourerToJob").push().getKey();
                    databaseReference.child("LabourerToJob").child(key).child("Labourer_Id").setValue(labourerList.get(x).getLabourerID());
                    databaseReference.child("LabourerToJob").child(key).child("Job_Id").setValue(job.getId());

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Labourers").child(labourerList.get(x).getLabourerID());

                    // Create a map with the updated password
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("allottedForJob", true);

                    // Update the password for the specific sewak
                    dbRef.updateChildren(updates).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Handle success
                            Log.d("Firebase", "Password updated successfully");
                        } else {
                            // Handle failure
                            Log.e("Firebase", "Failed to update password", task.getException());
                        }
                    });
                }



                sendSms(labourerList.get(x).getMobile(), "Rozgar Sewak has allotted you to job: " + job.getTitle() + ".\n Contact for further details: " + rozgarSewak.getMobile());
                    // Permission has already been granted
                Toast.makeText(AllotLabourersActivity.this, "Allotted to Job successfully", Toast.LENGTH_LONG).show();




            }
        }
            }

     //       Toast.makeText(getApplicationContext(), Constants.LABOURERS_ALLOTTED_SUCCESSFULLY, Toast.LENGTH_LONG).show();


//        else {
//            Toast.makeText(getApplicationContext(), Constants.NO_JOBS, Toast.LENGTH_LONG).show();
//        }
//        finish();
//
//    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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