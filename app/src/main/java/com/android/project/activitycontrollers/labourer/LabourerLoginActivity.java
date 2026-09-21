package com.android.project.activitycontrollers.labourer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.labourer.LabourerSignupActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.project.model.FieldInspector;
import com.android.project.model.Labourer;
import com.android.project.R;
//import com.android.project.database.AppDatabaseHelper;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class LabourerLoginActivity extends AppCompatActivity {

    private EditText usernameET = null;
    private EditText passwordET = null;
    Labourer labourer=new Labourer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourerloginscreen);
    }
    public void signUp(View view)
    {
        Intent intent  = new Intent(this, com.android.project.activitycontrollers.labourer.LabourerSignupActivity.class);
        startActivity(intent);
    }
    public void login(View view) {
        usernameET = findViewById(R.id.userName);
        passwordET = findViewById(R.id.password);
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();



        if (username.length() == 0)
        {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else if(password.length() == 0)
        {
            passwordET.setError(Constants.MISSING_PASSWORD);
            passwordET.requestFocus();
        }
        else {
            if (labourer == null) {
                Toast.makeText(this, "Register", Toast.LENGTH_LONG).show();
           }
            else{
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("Labourers");

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean registeredUser = false;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String storedUsername = userSnapshot.child("username").getValue(String.class);
                            String storedPassword = userSnapshot.child("password").getValue(String.class);


                            // Compare stored username and password with given credentials
                            if (storedUsername.equals(username) && storedPassword.equals(password)) {

                                registeredUser = true;
                                labourer = userSnapshot.getValue(Labourer.class);
                                labourer.setLabourerID(userSnapshot.getKey());
                                break;

                            }
                        }
                        if (registeredUser == false) {
                            Toast.makeText(LabourerLoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        } else {

                            Intent intent = new Intent(LabourerLoginActivity.this, LabourerHomeActivity.class);
                            intent.putExtra("id", labourer.getLabourerID());
                            ((AppInstance) getApplicationContext()).setCurrentLabourer(labourer);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database read error
                    }
                });

            }

        }
    }

    public void forgotPassword(View view)
    {
        usernameET = findViewById(R.id.userName);
        String userName = usernameET.getText().toString().trim();

        if (userName.length() == 0)
        {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Labourers");

            // Query the database by the username
            String username = usernameET.getText().toString().trim();
            Query query = dbRef.orderByChild("username").equalTo(username);

            // Listen for data changes
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Labourer labourer = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check if data exists for the given username

                        labourer = snapshot.getValue(Labourer.class);
                        if (labourer == null) {
                            Toast.makeText(getApplicationContext(), Constants.INVALID_USER, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.FORGOT_PASSWORD, Toast.LENGTH_LONG).show();
                            final String passwordString = "Hello " + labourer.getName() + ". \nYour registered password is: " + labourer.getPassword() + "\nTeam Android App";


                            sendSms(labourer.getMobile(), passwordString);
                            // Permission has already been granted
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                    //listener.onDataReceived(null);
                }
            });

        }
    }


    private void sendSms(String phonenumber, String message)
    {
        SmsManager manager = SmsManager.getDefault();

        int length = message.length();

        if(length > 160)
        {
            ArrayList<String> messagelist = manager.divideMessage(message);

            manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        }
        else
        {
            manager.sendTextMessage(phonenumber, null, message, null, null);
        }
    }

}
