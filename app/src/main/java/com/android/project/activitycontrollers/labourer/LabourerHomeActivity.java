package com.android.project.activitycontrollers.labourer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.labourer.RegisterForSchemeActivity;
import com.android.project.activitycontrollers.labourer.ViewJobCardActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.labourer.ChangePasswordActivity;
import com.android.project.activitycontrollers.labourer.DemandForJobActivity;
import com.android.project.activitycontrollers.labourer.GenerateQRCodeActivity;
import com.android.project.activitycontrollers.labourer.UploadProofPhotosActivity;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.annotations.Nullable;

public class LabourerHomeActivity extends AppCompatActivity {
    private RadioGroup optionsRG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_homescreen);
        initializeUIComponents();

    }


    public void initializeUIComponents()
    {
        optionsRG = findViewById(R.id.options);
        optionsRG.check(R.id.register);
    }

    public void next(View view)
    {
        int selectedOption = optionsRG.getCheckedRadioButtonId();
        Intent intent = null;
        Labourer labourer = ((AppInstance)getApplicationContext()).getCurrentLabourer();

        if (selectedOption == R.id.register)
        {
            if (true == labourer.isRegisteredForScheme())
            {
                Toast.makeText(getApplicationContext(), Constants.ALREADY_REGISTERED_FOR_SCHEME, Toast.LENGTH_LONG).show();
            }
            else {
                intent = new Intent(getApplicationContext(), RegisterForSchemeActivity.class);
                startActivity(intent);
            }
        }
        else if(selectedOption==R.id.demand_for_job)
        {
            if (false == labourer.isRegisteredForScheme()) {
                Toast.makeText(getApplicationContext(), Constants.NOT_REGISTERED_FOR_SCHEME, Toast.LENGTH_LONG).show();
            }
            else {
                intent = new Intent(getApplicationContext(), DemandForJobActivity.class);
                startActivity(intent);
            }
        }
        else if(selectedOption==R.id.generateQRCode)
        {
            if (false == labourer.isRegisteredForScheme()) {
                Toast.makeText(getApplicationContext(), Constants.QR_CODE_GENERATION_NOT_POSSIBLE_TILL_SCHEME_REGISTRATION, Toast.LENGTH_LONG).show();
            }
            else if (labourer.isJobCardIssued() == false)
            {
                Toast.makeText(this, Constants.JOB_CARD_NOT_ISSUED,Toast.LENGTH_LONG).show();
            }
            else {
                intent = new Intent(getApplicationContext(), GenerateQRCodeActivity.class);
                startActivity(intent);
            }
        }
        else if(selectedOption==R.id.view_job_card)
        {
            if (labourer.isJobCardIssued() == false)
            {
                Toast.makeText(this, Constants.JOB_CARD_NOT_ISSUED,Toast.LENGTH_LONG).show();
            }
            else {
                intent = new Intent(getApplicationContext(), ViewJobCardActivity.class);
                startActivity(intent);
            }
        }
        else if(selectedOption==R.id.upload_proof_photos)
        {
            intent = new Intent(getApplicationContext(), UploadProofPhotosActivity.class);
            startActivity(intent);

        }
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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,Constants.FEEDBACK_SUBJECT);
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
