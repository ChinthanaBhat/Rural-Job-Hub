package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.activitycontrollers.pradhan.AddJobActivity;
//import com.android.project.activitycontrollers.pradhan.AddRozgarSewakActivity;
//import com.android.project.activitycontrollers.pradhan.ApproveLabourerListActivity;
//import com.android.project.activitycontrollers.pradhan.IssueJobCardActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;


public class RozgarSewakHomeActivity extends AppCompatActivity {

    private RadioGroup optionsRG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozgarsewak_homescreen);
        initializeUIComponents();

    }



    public void initializeUIComponents()
    {
        optionsRG = findViewById(R.id.options);
        optionsRG.check(R.id.addlabourer);
    }

    public void next(View view)
    {
        int selectedOption = optionsRG.getCheckedRadioButtonId();
        Intent intent = null;
        if (selectedOption == R.id.addlabourer)
        {
            intent = new Intent(getApplicationContext(),AddLabourerActivity.class);
        }
        else if(selectedOption==R.id.scanjobcard)
        {
            intent = new Intent(getApplicationContext(),ScanJobCardActivity.class);
        }
        else if(selectedOption==R.id.viewjobs)
        {
            intent = new Intent(getApplicationContext(),ViewJobsActivity.class);
        }

        else if(selectedOption==R.id.payment)
        {
            intent = new Intent(getApplicationContext(),MakePaymentActivity.class);
        }
        else if(selectedOption==R.id.allotment)
        {
            intent = new Intent(getApplicationContext(),AllotLabourersActivity.class);
        }
        else if(selectedOption==R.id.viewAttendance)
        {
            intent = new Intent(getApplicationContext(),ViewAttendanceActivity.class);
        }

        startActivity(intent);

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
