package com.android.project.activitycontrollers.pradhan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.project.R;
//import com.android.project.UserSelectionActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.RozgarSewak;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.android.project.utility.ImageFilePath;
import com.android.project.utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AddRozgarSewakActivity extends AppCompatActivity {
    private EditText nameET, usernameET, mobileET, panchayathET,passwordET;
    private ImageView iconIV;
    private String realPath = null;
    private RozgarSewak rozgarSewak = null;
    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri uri = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrozgarsewak);
        initializeUIComponents();
        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    Intent data = result.getData();
                    uri=data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        iconIV.setImageBitmap(bitmap);
                        iconIV.setVisibility(View.VISIBLE);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void initializeUIComponents() {
        mobileET = findViewById(R.id.phno);
        nameET = findViewById(R.id.name);
        iconIV = findViewById(R.id.image);
        usernameET = findViewById(R.id.username);
        panchayathET = findViewById(R.id.gram_panchayath);
        passwordET = findViewById(R.id.password);

    }

    public void addRozgarSewak(View view) {
        initializeUIComponents();
        String username = usernameET.getText().toString().trim();
        String name = nameET.getText().toString().trim();
        String mobile = mobileET.getText().toString().trim();
        String panchayath = panchayathET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        ArrayList<String> userNames = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("labourers");

        // Fetching all labourers' usernames
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create an ArrayList to store all usernames


                // Loop through each labourer entry
                for (DataSnapshot labourerSnapshot : dataSnapshot.getChildren()) {
                    // Get the username for each labourer
                     String userName = labourerSnapshot.child("username").getValue(String.class);

                    // Add the username to the list
                    if (username != null) {
                        userNames.add(userName);
                    }
                }

                // Call listener with the fetched usernames
                //  listener.onLabourerUsernamesFetched(labourerUserNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                Log.e("FirebaseError", "Error fetching data", databaseError.toException());
            }
        });



        if (name.length() == 0) {
            nameET.setError(Constants.MISSING_NAME);
            nameET.requestFocus();
        } else if (mobile.length() == 0) {
            mobileET.setError(Constants.MISSING_MOBILE);
            mobileET.requestFocus();
        } else if (mobile.length() != 10) {
            mobileET.setError(Constants.INVALID_MOBILE);
            mobileET.requestFocus();
        } else if (username.length() == 0) {
            usernameET.setError(Constants.MISSING_USERNAME);
            usernameET.requestFocus();
        }
        else if (userNames.size() > 0 && userNames.contains(username)) {
            usernameET.setError(Constants.DUPLICATE_USERNAME);
            usernameET.requestFocus();
        } else if (panchayath.length() == 0) {
            panchayathET.setError(Constants.MISSING_PANCHAYATH);
            panchayathET.requestFocus();
        }
        else {
            rozgarSewak=new RozgarSewak();
            rozgarSewak.setUsername(username);
            rozgarSewak.setPassword(password);
            rozgarSewak.setName(name);
            rozgarSewak.setMobile(mobile);
            rozgarSewak.setGramPanchayath(panchayath);
            saveImage();


        }
    }

    public void saveImage(){
        StorageReference imageRe = storageReference.child("images/" +uri.getLastPathSegment());
        UploadTask uploadTask = imageRe.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRe.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        realPath = uri.toString();
                        rozgarSewak.setProfilePath(realPath);
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                        String key = databaseReference1.child("Sewaks").push().getKey();
                        databaseReference1.child("Sewaks").child(key).setValue(rozgarSewak);
                        rozgarSewak.setSewakID(key);

                        if(rozgarSewak.getSewakID() != null) {
                            Toast.makeText(AddRozgarSewakActivity.this, "RozgarSewak added succesfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(AddRozgarSewakActivity.this, "Error occured while adding the RozgarSewak", Toast.LENGTH_LONG).show();
                        }
                        sendSms(rozgarSewak.getMobile(), "Pradhan has added you as Rozgar Sewak using Android App. \nUsername: " + rozgarSewak.getUsername() + "\nPassword: " + rozgarSewak.getPassword());
                            // Permission has already been granted
                        }


                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handle error
            }
        });
    }



    public void selectPhoto(View view){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        final CharSequence[] items = {"Choose from Gallery", "Cancel"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                boolean result = true;
                if (items[item].equals("Choose from Gallery")){
                    if (result)
                        galleryIntent();
                }
                else if (items[item].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }
    //implicit intent
    public void galleryIntent(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(Intent.createChooser(intent,"Select File"));
    }



    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setIcon(R.drawable.applogo);
                dialogBuilder.setTitle(R.string.app_name);
                dialogBuilder.setMessage(Constants.APP_DESCRIPTION);
                dialogBuilder.create();
                dialogBuilder.show();
                return true;

            case R.id.logout:
                ((AppInstance) getApplicationContext()).setAdminUser(false);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;

            case R.id.changePassword:
                Intent intent = new Intent(this, AdminChangePasswordActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }


    private void sendSms(String phonenumber, String message) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> msgArray = smsManager.divideMessage(message);

            smsManager.sendMultipartTextMessage(phonenumber, null, msgArray, null, null);
            //Toast.makeText(getApplicationContext(), "Message Sent",Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}