package com.android.project.activitycontrollers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.android.project.R;
import com.android.project.activitycontrollers.fieldinspector.FieldInspectorLoginActivity;
import com.android.project.activitycontrollers.labourer.LabourerLoginActivity;
import com.android.project.activitycontrollers.pradhan.PradhanLoginActivity;
import com.android.project.activitycontrollers.rozgarsewak.RozgarSewakLoginActivity;
import com.android.project.utility.Constants;


public class UserSelectionActivity extends AppCompatActivity {
    private RadioGroup userTypeRG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userselectionscreen);
        initializeUIComponents();
    }

    public void initializeUIComponents() {

        userTypeRG = findViewById(R.id.userType);
        userTypeRG.check(R.id.panchayath);
    }
    public void selectUser(View view)
    {
        int selectedOption = userTypeRG.getCheckedRadioButtonId();
        Intent i = null;
        if (selectedOption == R.id.panchayath)
        {
            i = new Intent(getApplicationContext(),PradhanLoginActivity.class);
        }
        else if (selectedOption == R.id.rozgar)
        {
            i = new Intent(getApplicationContext(), RozgarSewakLoginActivity.class);
        }
        else if (selectedOption == R.id.labourer)
        {
            i = new Intent(getApplicationContext(), LabourerLoginActivity.class);
        }
        else if (selectedOption == R.id.field_inspector)
        {
            i = new Intent(getApplicationContext(), FieldInspectorLoginActivity.class);
        }

        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
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
}
