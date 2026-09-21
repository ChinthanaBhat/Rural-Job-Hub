package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.RozgarSewak;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.database.AppDatabaseHelper;
//import com.android.project.model.RozgarSewak;
import com.android.project.model.RozgarSewak;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPasswordET, newPasswordET,  confirmNewPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        initializeUIComponents();
    }

    public void initializeUIComponents()
    {
        oldPasswordET = findViewById(R.id.oldPassword);
        newPasswordET =  findViewById(R.id.newPassword);
        confirmNewPasswordET =  findViewById(R.id.confirmNewPassword);


    }

    public void changePassword(View view)
    {
        String oldPassword = oldPasswordET.getText().toString().trim();
        String newPassword = newPasswordET.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordET.getText().toString().trim();
        RozgarSewak rozgarSewak = ((AppInstance)getApplicationContext()).getCurrentRozgarSewak();

        if (oldPassword.length() == 0) {
            oldPasswordET.setError(Constants.MISSING_OLD_PASSWORD);
            oldPasswordET.requestFocus();
        }
        else if (false == rozgarSewak.getPassword().equals(oldPassword))
        {
            oldPasswordET.setError(Constants.OLD_PASSWORD_INCORRECT);
            oldPasswordET.requestFocus();
        }
        else if(newPassword.length() == 0)
        {
            newPasswordET.setError(Constants.MISSING_NEW_PASSWORD);
            newPasswordET.requestFocus();
        }
        else if(newPassword.length()< Constants.MINIMUM_PASSWORD_LENGTH )
        {
            newPasswordET.setError(Constants.INVALID_PASSWORD);
            newPasswordET.requestFocus();
        }
        else if (false == isValidPassword(newPassword) )
        {
            newPasswordET.setError(Constants.INVALID_PASSWORD);
            newPasswordET.requestFocus();
        }
        else if (confirmNewPassword.length() == 0) {
            confirmNewPasswordET.setError(Constants.MISSING_PASSWORD_CONFIRMATION);
            confirmNewPasswordET.requestFocus();
        }
        else if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordET.setError(Constants.PASSWORD_MISMATCH);
            confirmNewPasswordET.requestFocus();
        }
        else
        {
//            AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
//            databaseHelper.updatePasswordForRozgarSewak(newPassword, rozgarSewak.getSewakID());
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            // Prepare the path to the specific Rozgar Sewak by their sewakID
            DatabaseReference sewakRef = database.child("Sewaks").child(rozgarSewak.getSewakID());

            // Create a map with the updated password
            Map<String, Object> updates = new HashMap<>();
            updates.put("password", newPassword);

            // Update the password for the specific sewak
            sewakRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Handle success
                    Log.d("Firebase", "Password updated successfully");
                } else {
                    // Handle failure
                    Log.e("Firebase", "Failed to update password", task.getException());
                }
            });
        }
//            Toast.makeText(this, Constants.PASSWORD_CHANGED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
//            finish();
        }


    public  boolean isValidPassword(final String password)
    {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[*@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentRozgarSewak(null);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
        }

        return false;
    }

}
