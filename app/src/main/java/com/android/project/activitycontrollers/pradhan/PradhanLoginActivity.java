package com.android.project.activitycontrollers.pradhan;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
//import android.support.annotation.Nullable;
//
//import androidx.appcompat.app.AppCompatActivity;


import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.R;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;



public class PradhanLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pradhanloginscreen);
    }

    public void login(View view) {
        EditText usernameET = findViewById(R.id.userName);
        EditText passwordET = findViewById(R.id.password);

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(this);
        String adminPassword = Constants.ADMIN_PASSWORD;
        if (mypref.contains(Constants.ADMIN_PASSWORD_KEY))
        {
            adminPassword = mypref.getString(Constants.ADMIN_PASSWORD_KEY, Constants.ADMIN_PASSWORD);
        }


        if (username.length() == 0)
        {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else if (password.length() == 0)
        {
            passwordET.setError(Constants.MISSING_PASSWORD);
            passwordET.requestFocus();
        }
        // Validate credentials
        else if (username.equals("admin") && password.equals(adminPassword))
        {
            ((AppInstance)getApplicationContext()).setAdminUser(true);
            Intent i = new Intent(this, PradhanHomeActivity.class);
            startActivity(i);
            // close this activity
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), Constants.INVALID_ADMIN_CREDENTIALS,Toast.LENGTH_LONG).show();
        }
    }


    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
                //dialogBuilder.setIcon(R.drawable.icon);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;
        }
        return false;
    }
}


