package com.android.project.activitycontrollers.labourer;


import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.project.model.Labourer;
import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;

import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterForSchemeActivity extends AppCompatActivity {
    private EditText bplCardET, voterIDET, aadharIDET;
    String bplcardNumbers,voterIds,aadharIDs;
    Labourer labourer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_registration_for_scheme);
        bplCardET = findViewById(R.id.bplno);
        voterIDET = findViewById(R.id.voterid);
        aadharIDET = findViewById(R.id.adharID);
//        bplcardNumbers=bplCardET.getText().toString();
//        voterIds=voterIDET.getText().toString();
//        aadharIDs=aadharIDET.getText().toString();


    }

    public void submit(View view)
    {
        String bplcard = bplCardET.getText().toString().trim();
        String voterid = voterIDET.getText().toString().trim();
        String aadharid = aadharIDET.getText().toString().trim();
        //AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);

        ArrayList<String> aadharIDs = null;//databaseHelper.getAllLabourerAadharIDs();
        ArrayList<String> voterIDs = null;//databaseHelper.getAllLabourerVoterIDs();
        ArrayList<String> bplcardNumbers = null;//databaseHelper.getAllLabourerBPLCardNumbers();
        // Fetching all labourers' usernames
        if (bplcard.length() == 0) {
            bplCardET.setError(Constants.MISSING_BPLCARD);
            bplCardET.requestFocus();
        }
//        else if (bplcardNumbers.size() > 0 && bplcardNumbers.contains(bplcard)) {
//            bplCardET.setError(Constants.DUPLICATE_BPLCARD);
//            bplCardET.requestFocus();
//        }
        else if (voterid.length() == 0) {
            voterIDET.setError(Constants.MISSING_VOTERID);
            voterIDET.requestFocus();
        }
        else if (voterid.length() != 10) {
            voterIDET.setError(Constants.INVALID_VOTERID);
            voterIDET.requestFocus();
        }
//        else if (voterIDs.size() > 0 && voterIDs.contains(voterid)) {
//            voterIDET.setError(Constants.DUPLICATE_VOTERID);
//            voterIDET.requestFocus();
//        }
        else if (aadharid.length() == 0) {
            aadharIDET.setError(Constants.MISSING_AADHARCARD);
            aadharIDET.requestFocus();
        }
        else if (aadharid.length() != 12) {
            aadharIDET.setError(Constants.INVALID_AADHARCARD);
            aadharIDET.requestFocus();
        }
//        else if (aadharIDs.size() > 0 && aadharIDs.contains(aadharid)) {
//            aadharIDET.setError(Constants.DUPLICATE_AADHARID);
//            aadharIDET.requestFocus();
//        }
        else
        {
            Labourer labourer = ((AppInstance)getApplicationContext()).getCurrentLabourer();
            labourer.setAadharID(aadharid);
            labourer.setBplCardNumber(bplcard);
            labourer.setVoterID(voterid);
            labourer.setRegisteredForScheme(true);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Labourers").child(labourer.getLabourerID());
            Map<String,Object> updates = new HashMap<>();
            updates.put("aadharID",aadharid);
            updates.put("bplCardNumber",bplcard);
            updates.put("voterID",voterid);
            updates.put("registeredForScheme",true);

            myRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void unused) {
                                                                               Toast.makeText(RegisterForSchemeActivity.this,"Labourer registered for scheme successfully",Toast.LENGTH_LONG).show();
                                                                                       Intent intent = new
                                                                                               Intent(RegisterForSchemeActivity.this,LabourerHomeActivity.class);
                                                                               startActivity(intent);
                                                                           }
                                                                       });
        }

           // databaseHelper.registerLabourerForScheme(labourer);
            Toast.makeText(this, Constants.LABOURER_REGISTERED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
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
