package com.android.project.activitycontrollers.rozgarsewak;


import android.Manifest;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.project.R;
//import com.android.project.activitycontrollers.labourer.LabourerHomeActivity;
//import com.android.project.database.AppDatabaseHelper;
//import com.android.project.model.Labourer;
//import com.android.project.model.RozgarSewak;
import com.android.project.activitycontrollers.labourer.LabourerHomeActivity;
import com.android.project.activitycontrollers.labourer.LabourerLoginActivity;
import com.android.project.model.Labourer;
import com.android.project.model.RozgarSewak;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class RozgarSewakLoginActivity extends AppCompatActivity {
    private EditText usernameET,passwordET;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozgarsewaklogin);
        initializeUIComponents();
    }

    public void initializeUIComponents() {
        usernameET = findViewById(R.id.userName);
        passwordET = findViewById(R.id.password);
    }

    public void login(View view)
    {
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        if (username.length() == 0)
        {
            usernameET.setError(Constants.MISSING_PASSWORD);
            usernameET.requestFocus();
        }
        else if(password.length() == 0)
        {
            passwordET.setError(Constants.MISSING_PASSWORD);
            passwordET.requestFocus();
        }
        else{

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Sewaks");

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean registeredUser = false;
                    RozgarSewak sewak=null;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedUsername = userSnapshot.child("username").getValue(String.class);
                        String storedPassword = userSnapshot.child("password").getValue(String.class);


                        // Compare stored username and password with given credentials
                        if (storedUsername.equals(username) && storedPassword.equals(password)) {

                            registeredUser = true;
                            sewak = userSnapshot.getValue(RozgarSewak.class);
                            sewak.setSewakID(userSnapshot.getKey());
                            break;

                        }
                    }
                    if (registeredUser == false) {
                        Toast.makeText(RozgarSewakLoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    } else {

                        ((AppInstance) getApplicationContext()).setCurrentRozgarSewak(sewak);
                        Intent intent = new Intent(RozgarSewakLoginActivity.this, RozgarSewakHomeActivity.class);
                        intent.putExtra("id", sewak.getSewakID());
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

    public void forgotPassword(View view)
    {
        String userName = usernameET.getText().toString().trim();

        if (userName.length() == 0)
        {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else {
            // Get a reference to the Firebase database
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Sewaks");

            // Query the database by the username
            String username = usernameET.getText().toString().trim();
            Query query = dbRef.orderByChild("username").equalTo(username);

            // Listen for data changes
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RozgarSewak rozgarSewak = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check if data exists for the given username

                        rozgarSewak = snapshot.getValue(RozgarSewak.class);
                        if (rozgarSewak == null) {
                            Toast.makeText(getApplicationContext(), Constants.INVALID_USER, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.FORGOT_PASSWORD, Toast.LENGTH_LONG).show();
                            final String passwordString = "Hello " + rozgarSewak.getName() + ". \nYour registered password is: " + rozgarSewak.getPassword() + "\nTeam Android App";


                            sendSms(rozgarSewak.getMobile(), passwordString);
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

        public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu,menu);
        return true;
    }
        public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId())
        {
            case R.id.about:
                android.app.AlertDialog.Builder dailogBuilder = new android.app.AlertDialog.Builder(this);
                dailogBuilder.setIcon(R.drawable.applogo);
                dailogBuilder.setTitle(R.string.app_name);
                dailogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dailogBuilder.create();
                dailogBuilder.show();
                return true;
        }

        return false;
    }


    }


