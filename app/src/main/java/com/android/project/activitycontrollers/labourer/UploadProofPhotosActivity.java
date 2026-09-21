package com.android.project.activitycontrollers.labourer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.Attendance;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.android.project.utility.ImageFilePath;;
import com.android.project.utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UploadProofPhotosActivity extends AppCompatActivity {
    private ImageView beforeIV, afterIV;
    private String beforePath = null, afterPath = null;
    private ActivityResultLauncher<Intent> galleryLauncher = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri beforeuri = null;
    Uri afteruri = null;
    int selectedStatus=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadbeforeafterphotos);
        initializeUIComponents();
        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null) {
                        if (selectedStatus == Constants.BEFORE) {
                            beforeuri = data.getData();
                            beforePath = beforeuri.toString();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), beforeuri);
                                beforeIV.setImageBitmap(bitmap);
                                beforeIV.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            afteruri = data.getData();
                            afterPath = afteruri.toString();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), afteruri);
                                afterIV.setImageBitmap(bitmap);
                                afterIV.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

    }

    public void initializeUIComponents() {
        beforeIV = findViewById(R.id.before);
        afterIV = findViewById(R.id.after);
    }

    public void upload(View view) {

        if (beforePath == null) {
            Toast.makeText(getApplicationContext(), Constants.MISSING_BEFOREPHOTO, Toast.LENGTH_LONG).show();
        }
        else if (afterPath == null) {
            Toast.makeText(getApplicationContext(), Constants.MISSING_AFTERPHOTO, Toast.LENGTH_LONG).show();
        }
        else
        {
            saveBeforeImage();
            saveAfterImage();
            Labourer labourer = ((AppInstance)getApplicationContext()).getCurrentLabourer();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LabourerToJob");

            Query query = ref.orderByChild("Labourer_Id").equalTo(labourer.getLabourerID());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // This is the matched node
                        String key = snapshot.getKey();
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Calendar date = Calendar.getInstance();
                        String stringDate = dateFormatter.format(date.getTime());

                        // Add a new child node under the matched node
                        ref.child(key).child("Date").setValue(stringDate)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Constants.PROGRESSUPLOADING); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing progress dialog with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                progressDialog.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), Constants.PHOTOS_UPLOADED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
            }
        }, 3000);
    }



    public void selectBeforePhoto(View view) {
        showAlertFor(Constants.BEFORE);
    }
    //
    public void selectAfterPhoto(View view) { showAlertFor(Constants.AFTER); }
    //
    public void showAlertFor(final int status)
    {

        final CharSequence[] items = {"Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission(UploadProofPhotosActivity.this);
                if (items[item].equals("Choose from Library")) {
                    if (result) {
                        if (status == Constants.BEFORE) {
                            galleryIntent(Constants.BEFORE);
                        }
                        else {
                            galleryIntent(Constants.AFTER);

                        }
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();}
    public void galleryIntent(int status){
        Intent intent= new Intent();
        intent.setType("image/*");
        selectedStatus = status;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(Intent.createChooser(intent,"Select File"));
    }

    public void saveBeforeImage(){
        StorageReference imageRe = storageReference.child("images/" +beforeuri.getLastPathSegment());
        UploadTask uploadTask = imageRe.putFile(beforeuri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRe.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        beforePath = uri.toString();

                        Labourer labourer = ((AppInstance) getApplicationContext()).getCurrentLabourer();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LabourerToJob");

                        Query query = ref.orderByChild("Labourer_Id").equalTo(labourer.getLabourerID());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // This is the matched node
                                    String key = snapshot.getKey();

                                    // Add a new child node under the matched node
                                    ref.child(key).child("beforePath").setValue(beforePath)
                                            .addOnSuccessListener(aVoid -> {
                                            })
                                            .addOnFailureListener(e -> {
                                            });
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

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
    public void saveAfterImage(){
        StorageReference imageRe = storageReference.child("images/" +beforeuri.getLastPathSegment());
        UploadTask uploadTask = imageRe.putFile(afteruri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRe.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        afterPath = uri.toString();
                        Labourer labourer = ((AppInstance) getApplicationContext()).getCurrentLabourer();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LabourerToJob");

                        Query query = ref.orderByChild("Labourer_Id").equalTo(labourer.getLabourerID());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // This is the matched node
                                    String key = snapshot.getKey();

                                    // Add a new child node under the matched node
                                    ref.child(key).child("afterPath").setValue(afterPath)
                                            .addOnSuccessListener(aVoid -> {
                                            })
                                            .addOnFailureListener(e -> {
                                            });
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

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

    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu, menu);
        return true;
    }
    //
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.logout:
                ((AppInstance) getApplicationContext()).setCurrentLabourer(null);
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.about:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
                AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this);
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
