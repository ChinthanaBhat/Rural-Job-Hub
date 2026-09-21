package com.android.project.activitycontrollers.labourer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.activitycontrollers.pradhan.AddFieldInspectorActivity;
import com.android.project.model.Labourer;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.database.AppDatabaseHelper;
//import com.android.project.model.Labourer;
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

public class LabourerSignupActivity extends AppCompatActivity {
    private EditText nameET, usernameET, passwordET, mobileET, ageET, panchayathET, bankET, ifscET, accountET;
    private ImageView iconIV;
    private String realPath = null;
    RadioGroup genderRG;

    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri uri = null;
    Labourer labourer = null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_registrationscreen);
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
        mobileET = findViewById(R.id.mobile);
        nameET = findViewById(R.id.name);
        iconIV = findViewById(R.id.image);
        usernameET = findViewById(R.id.userName);
        passwordET = findViewById(R.id.password);
        ageET = findViewById(R.id.age);
        genderRG = findViewById(R.id.gender);
        panchayathET = findViewById(R.id.panchayath);
        bankET = findViewById(R.id.bank);
        ifscET = findViewById(R.id.ifsc);
        accountET = findViewById(R.id.account);

    }

    public void register(View view) {
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String name = nameET.getText().toString().trim();
        String mobile = mobileET.getText().toString().trim();

        String age = ageET.getText().toString().trim();
        String panchayath = panchayathET.getText().toString().trim();
        String bank = bankET.getText().toString().trim();
        String ifsc = ifscET.getText().toString().trim();
        String account = accountET.getText().toString().trim();

//            AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
//
//            ArrayList<String> userNames = databaseHelper.getAllLabourerUserNames();
        ArrayList<String> userNames = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("labourers");
        // Fetching all labourers' usernames
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create an ArrayList to store all usernames

                // Loop through each labourer entry
                for (DataSnapshot labourerSnapshot : dataSnapshot.getChildren()) {
                    // Get the username for each labourer
                    String username = labourerSnapshot.child("username").getValue(String.class);

                    // Add the username to the list
                    if (username != null) {
                        userNames.add(username);
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

        int selectedGender = genderRG.getCheckedRadioButtonId();
        String userGender = null;
        if (selectedGender == R.id.male) {
            userGender = Constants.MALE;
        } else {
            userGender = Constants.FEMALE;
        }

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
        } else if (userNames.size() > 0 && userNames.contains(username)) {
            usernameET.setError(Constants.DUPLICATE_USERNAME);
            usernameET.requestFocus();
        } else if (password.length() == 0) {
            passwordET.setError(Constants.MISSING_PASSWORD);
            passwordET.requestFocus();
        } else if (password.length() < Constants.MINIMUM_PASSWORD_LENGTH) {
            passwordET.setError(Constants.INVALID_PASSWORD);
            passwordET.requestFocus();
        } else if (panchayath.length() == 0) {
            panchayathET.setError(Constants.MISSING_PANCHAYATH);
            panchayathET.requestFocus();
        } else if (bank.length() == 0) {
            Toast.makeText(getApplicationContext(), Constants.MISSING_BANK, Toast.LENGTH_LONG).show();
        } else if (ifsc.length() == 0) {
            Toast.makeText(getApplicationContext(), Constants.MISSING_IFSC, Toast.LENGTH_LONG).show();
        } else if (account.length() == 0) {
            Toast.makeText(getApplicationContext(), Constants.MISSING_ACCOUNT, Toast.LENGTH_LONG).show();
        }  else {
            labourer = new Labourer();
            labourer.setName(name);
            labourer.setMobile(mobile);
            labourer.setUsername(username);
            labourer.setPassword(password);
            labourer.setAge(Integer.parseInt(age));
            labourer.setProfilePath(realPath);
            labourer.setGramPanchayath(panchayath);
            labourer.setGender(userGender);
            labourer.setBank(bank);
            labourer.setIfsc(ifsc);
            labourer.setAccount(account);
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
                        labourer.setProfilePath(realPath);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        String key = databaseReference.child("Labourers").push().getKey();
                        databaseReference.child("Labourers").child(key).setValue(labourer);
                        labourer.setLabourerID(key);

                        if(labourer.getLabourerID() != null) {
                            Toast.makeText(LabourerSignupActivity.this, Constants.LABOURER_REGISTRATION_SUCCESSFULL, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LabourerSignupActivity.this, LabourerLoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LabourerSignupActivity.this, "Error occured during registration", Toast.LENGTH_LONG).show();
                        }
                        //sendSms(fieldinspector.getMobile(), "Pradhan has added you as Field Inspector using Android App. \nUsername: " + fieldinspector.getUsername() + "\nPassword: " + fieldinspector.getPassword());
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


    }

