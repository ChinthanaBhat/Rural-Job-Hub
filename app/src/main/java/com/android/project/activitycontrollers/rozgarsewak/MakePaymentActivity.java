package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.android.project.model.Labourer;
import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.model.Labourer;
import com.android.project.adapters.LabourerListItemAdapter;
import com.android.project.model.Labourer;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MakePaymentActivity extends AppCompatActivity {
    private ListView listView = null;

    LabourerListItemAdapter customAdapter;
    ArrayList<Labourer> labourerList;
    private TextView mNoLabourersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepayment);
        labourerList =new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference labourersRef = databaseReference.child("Labourers");

        // Query for workers who are allocated to a job (assuming "allottedForJob" is the key in Firebase).
        Query query = labourersRef.orderByChild("allottedForJob").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Labourer labourer = snapshot.getValue(Labourer.class); // Map the snapshot to the Labourer object.
                    labourer.setLabourerID(snapshot.getKey());
                    labourerList.add(labourer);
                }
                populateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("Firebase", "Error retrieving data", databaseError.toException());
            }
        });

    }

    public void populateListView() {
        mNoLabourersView = findViewById(R.id.no_labourer_text);
        mNoLabourersView.setText(Constants.NO_APPROVED_LABOURERS_DESCRIPTION);


        if (labourerList.size() > 0) {
            mNoLabourersView.setVisibility(View.GONE);
            customAdapter = new LabourerListItemAdapter(this, R.layout.labourerlist_item);
            customAdapter.setLabourerList(labourerList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Labourer labourer = labourerList.get(i);
                    Intent intent = new Intent(getApplicationContext(), ViewProofPhotosActivity.class);

                    intent.putExtra(Constants.ID_KEY, labourer.getLabourerID());
                    startActivity(intent);
                }
            });


        } else {
            mNoLabourersView.setVisibility(View.VISIBLE);
            if (customAdapter != null) {
                reloadData();
            }
        }
    }


    public void reloadData() {
        labourerList.clear();
//        AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
//        labourerList = databaseHelper.getJobAllottedLabourerList();
        if (labourerList.size() <= 0) {
            mNoLabourersView.setVisibility(View.VISIBLE);
        } else {
            mNoLabourersView.setVisibility(View.GONE);
            customAdapter.setLabourerList(labourerList);
            customAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reloadData();
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

