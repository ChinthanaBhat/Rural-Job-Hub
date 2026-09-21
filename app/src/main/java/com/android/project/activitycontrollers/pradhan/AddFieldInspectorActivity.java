package com.android.project.activitycontrollers.pradhan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.fieldinspector.FieldInspectorLoginActivity;
import com.android.project.model.FieldInspector;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.android.project.utility.ImageFilePath;
import com.android.project.utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddFieldInspectorActivity extends AppCompatActivity {
    private EditText nameET, usernameET, passwordET, mobileET, panchayathET;
    private ImageView iconIV;
    private String realPath = null;
    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri uri = null;
    FieldInspector fieldinspector=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field_inspector);
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
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        panchayathET = findViewById(R.id.gram_panchayath);
        iconIV = findViewById(R.id.image);

    }

    public void addFieldInspector(View view) {
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String name = nameET.getText().toString().trim();
        String mobile = mobileET.getText().toString().trim();
        String panchayath = panchayathET.getText().toString().trim();
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
        else if (panchayath.length() == 0) {
            panchayathET.setError(Constants.MISSING_PANCHAYATH);
            panchayathET.requestFocus();
        }
        else {
            fieldinspector = new FieldInspector();

            fieldinspector.setName(name);
            fieldinspector.setPassword(password);
            fieldinspector.setUsername(username);
            fieldinspector.setMobile(mobile);
            fieldinspector.setPanchayath(panchayath);
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
                        fieldinspector.setImage(realPath);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        String key = databaseReference.child("FieldInspectors").push().getKey();
                        databaseReference.child("FieldInspectors").child(key).setValue(fieldinspector);
                        fieldinspector.setFieldinspectorId(key);

                        if(fieldinspector.getFieldinspectorId() != null) {
                            Toast.makeText(AddFieldInspectorActivity.this, "Field Inspector added succesfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(AddFieldInspectorActivity.this, "Error occured while adding the Field Inspector", Toast.LENGTH_LONG).show();
                        }
                        sendSms(fieldinspector.getMobile(), "Pradhan has added you as Field Inspector using Android App. \nUsername: " + fieldinspector.getUsername() + "\nPassword: " + fieldinspector.getPassword());
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




