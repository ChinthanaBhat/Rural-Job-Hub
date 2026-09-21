package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.pradhan.AdminChangePasswordActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ViewProofPhotosActivity extends AppCompatActivity {

    private ImageView beforeIV, afterIV;
    private Button proceedBT;
    private LinearLayout proofLL;
    private TextView noProofTV;
    ArrayList<String> proofPhotos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewproofphotos);
        initializeUIComponents();
        displayData();
    }

    public void initializeUIComponents()
    {
        beforeIV = findViewById(R.id.before);
        afterIV = findViewById(R.id.after);
        proceedBT = findViewById(R.id.proceed);
        proofLL = findViewById(R.id.proof_layout);
        noProofTV = findViewById(R.id.no_proof);
        proofPhotos = new ArrayList<>();

    }

    public void displayData()
    {
        String labourerID=getIntent().getStringExtra(Constants.ID_KEY);
        //ArrayList<String> proofPhotos = databaseHelper.getProofPhotosForLabourerForDate(labourerID, Calendar.getInstance());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LabourerToJob");

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar date = Calendar.getInstance();
        String stringDate = dateFormatter.format(date.getTime());

        ref.orderByChild("Labourer_Id").equalTo(labourerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String jobDate = dataSnapshot.child("Date").getValue(String.class);

                            if (jobDate != null && jobDate.equals(stringDate)) {
                                String beforePhoto = dataSnapshot.child("beforePath").getValue(String.class);
                                String afterPhoto = dataSnapshot.child("afterPath").getValue(String.class);

                                proofPhotos.add(beforePhoto);
                                proofPhotos.add(afterPhoto);

                                if (proofPhotos == null) {
                                    proceedBT.setVisibility(View.GONE);
                                    noProofTV.setVisibility(View.VISIBLE);
                                    proofLL.setVisibility(View.GONE);
                                } else {
                                    proceedBT.setVisibility(View.VISIBLE);
                                    noProofTV.setVisibility(View.GONE);
                                    proofLL.setVisibility(View.VISIBLE);
                                    String beforePath = proofPhotos.get(0);
                                    if (null != beforePath) {
                                        Glide.with(ViewProofPhotosActivity.this)
                                                .load(beforePath)
                                                .into(beforeIV);
                                    } else {
                                        int resID = getResources().getIdentifier("noimage", Constants.DRAWABLE_RESOURCE, getPackageName());
                                        Glide.with(ViewProofPhotosActivity.this)
                                                .load(resID)
                                                .into(beforeIV);
                                    }
                                    String afterPath = proofPhotos.get(1);
                                    if (null != afterPath) {
                                        Glide.with(ViewProofPhotosActivity.this)
                                                .load(afterPath)
                                                .into(afterIV);
                                    } else {
                                        int resID = getResources().getIdentifier("noimage", Constants.DRAWABLE_RESOURCE, getPackageName());
                                        Glide.with(ViewProofPhotosActivity.this)
                                                .load(resID)
                                                .into(afterIV);
                                    }
                                }
                            }
                                break; // we found the matching date, exit loop
                            }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
//
    public void proceed(View view)
    {
        Intent intent = new Intent(getApplicationContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ID_KEY,getIntent().getStringExtra(Constants.ID_KEY));

        startActivity(intent);
        finish();

    }
//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.changePassword:
                Intent intent = new Intent(this, AdminChangePasswordActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                ((AppInstance) getApplicationContext()).setAdminUser(false);
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
        }
        return false;
    }


}
