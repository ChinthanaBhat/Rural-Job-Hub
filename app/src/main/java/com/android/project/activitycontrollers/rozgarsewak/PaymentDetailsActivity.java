package com.android.project.activitycontrollers.rozgarsewak;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
//import android.support.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.model.Labourer;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PaymentDetailsActivity extends AppCompatActivity {

    private EditText amountET;
    private TextView dateTV,nameTV,mobileTV;
    private Calendar date = null;
    private Labourer labourer = null;
    private String amount = null;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*$%";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentdetails);
        //AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(this);
        //labourer = appDatabaseHelper.getLabourerWithID(getIntent().getLongExtra(Constants.ID_KEY,0));
        nameTV = findViewById(R.id.name);
        mobileTV = findViewById(R.id.mobile);
        amountET = findViewById(R.id.amount);
        dateTV = findViewById(R.id.date);

        //public Labourer getLabourerWithID(long labourerID) {
            // Initialize the Firebase database reference
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Assuming you have a "Labourers" node in Firebase, you can query it using the labourerID
        String labourerID = getIntent().getStringExtra(Constants.ID_KEY);

        DatabaseReference labourerRef = databaseReference.child("Labourers").child(labourerID);

            // Retrieve the Labourer data from Firebase
            labourerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Check if data exists
                    if (dataSnapshot.exists()) {
                        // Map data from Firebase to a Labourer object
                       labourer = dataSnapshot.getValue(Labourer.class);

                        // Example of manually setting the Labourer ID since Firebase doesn't auto-generate it
                        labourer.setLabourerID(labourerID);
                        nameTV.setText(labourer.getName());
                        mobileTV.setText(labourer.getMobile());

                        // Optionally, you can now do something with the 'labourer' object, like update UI
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error if any
                    Log.w("Firebase", "loadLabourer:onCancelled", databaseError.toException());
                }
            });

            // Returning null here for now since Firebase is asynchronous, you should handle the result in onDataChange()
//            return null;
        }




    public void selectDate(View view)
    {
        dateTV.setError(null);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                String expenseDate = dateFormatter.format(newDate.getTime());
                dateTV.setText(expenseDate);
                date = newDate;
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void makePayment(View view)
    {
        amount = amountET.getText().toString();
        if (amount == null)
        {
            amountET.setError(Constants.MISSING_AMOUNT);
            amountET.requestFocus();
        }
        else if(date == null) {
            dateTV.requestFocus();
            dateTV.setError(Constants.MISSING_DATE);
        }
        else
        {
            Toast.makeText(getApplicationContext(), Constants.PAYMENT_MADE_SUCCESSFULLY, Toast.LENGTH_LONG).show();


                String accNo = labourer.getAccount();
                String lastFourDigits = null;
                if (accNo.length() > 4)
                {
                    lastFourDigits = accNo.substring(accNo.length() - 4);
                }
                else
                {
                    lastFourDigits = accNo;
                }
                sendSms(labourer.getMobile(), "Rozgar Sewak has deposited an amount of Rs " + amount + " to your bank account ending with " + lastFourDigits + " using Android App.");

                  }
        }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_SEND_SMS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String accNo = labourer.getAccount();
                    String lastFourDigits = null;
                    if (accNo.length() > 4) {
                        lastFourDigits = accNo.substring(accNo.length() - 4);
                    } else {
                        lastFourDigits = accNo;
                    }

                    sendSms(labourer.getMobile(), "Rozgar Sewak has deposited an amount of Rs " + lastFourDigits + " to your bank account ending with " + accNo + " using Android App.");
                    // Permission has already been granted

                } else {

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }
                return;

        }
    }

    private void sendSms(String phonenumber, String message) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> msgArray = smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(phonenumber, null, msgArray, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
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
