package com.android.project.activitycontrollers.fieldinspector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.labourer.LabourerHomeActivity;
import com.android.project.activitycontrollers.labourer.LabourerLoginActivity;
import com.android.project.model.FieldInspector;
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

public class FieldInspectorLoginActivity extends AppCompatActivity {
    private EditText usernameET,passwordET;
    FieldInspector fieldinspector=new FieldInspector();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_inspector_login);
    }

    public void login(View view)
    {
        usernameET=findViewById(R.id.username);
        passwordET=findViewById(R.id.password);

        String username=usernameET.getText().toString().trim();
        String password=passwordET.getText().toString().trim();

        if(username.length()==0){
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else if(password.length()==0)
        {
            passwordET.setError(Constants.MISSING_PASSWORD);
            passwordET.requestFocus();
        }
        else{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("FieldInspectors");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean fieldInspector = false;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedUsername = userSnapshot.child("username").getValue(String.class);
                        String storedPassword = userSnapshot.child("password").getValue(String.class);

                        // Compare stored username and password with given credentials
                        if (storedUsername.equals(username) && storedPassword.equals(password)) {
                            fieldInspector = true;
                            fieldinspector = userSnapshot.getValue(FieldInspector.class);
                            fieldinspector.setFieldinspectorId(userSnapshot.getKey());
                            break;

                        }
                    }
                    if (fieldInspector == false) {
                        Toast.makeText(FieldInspectorLoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    } else {
                        ((AppInstance) getApplicationContext()).setCurrentFieldInspector(fieldinspector);
                        Intent intent = new Intent(FieldInspectorLoginActivity.this, FieldinspectorHomeActivity.class);
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
        usernameET = findViewById(R.id.username);
        String userName = usernameET.getText().toString().trim();

        if (userName.length() == 0)
        {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else {
            // Get a reference to the Firebase database
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FieldInspectors");

            // Query the database by the username
            String username = usernameET.getText().toString().trim();
            Query query = dbRef.orderByChild("username").equalTo(username);

            // Listen for data changes
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FieldInspector fieldInspector = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check if data exists for the given username

                        fieldInspector = snapshot.getValue(FieldInspector.class);
                        if (fieldInspector == null) {
                            Toast.makeText(getApplicationContext(), Constants.INVALID_USER, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.FORGOT_PASSWORD, Toast.LENGTH_LONG).show();
                            final String passwordString = "Hello " + fieldInspector.getName() + ". \nYour registered password is: " + fieldInspector.getPassword() + "\nTeam Android App";


                            sendSms(fieldInspector.getMobile(), passwordString);
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


