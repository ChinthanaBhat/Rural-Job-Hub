package com.android.project.activitycontrollers.pradhan;

import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.activity.LabourerHomeActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.labourer.LabourerHomeActivity;
import com.android.project.adapters.LabourerListItemAdapter;
import com.android.project.model.Labourer;
//import com.android.project.UserSelectionActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.android.project.pradhan.project.AppDatabaseHelper;
//import com.android.project.pradhan.project.ApproveLabourerActivity;
//import com.android.project.pradhan.project.adapters.LabourerListItemAdapter;
//import com.android.project.pradhan.project.pradhan.AdminChangePasswordActivity;
//import com.android.project.pradhan.project.utility.AppInstance;
//import com.android.project.pradhan.project.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IssueJobCardActivity extends AppCompatActivity {
    private ListView listView = null;
    Labourer labourer=new Labourer();

    LabourerListItemAdapter customAdapter;
    ArrayList<Labourer> labourerList;
    private TextView mNoLabourersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlabourerlist);
        labourerList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productsRef = database.getReference("Labourers");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot labourerSnapshot : snapshot.getChildren()) {
                    Labourer labourer = labourerSnapshot.getValue(Labourer.class);
                    labourer.setLabourerID( labourerSnapshot.getKey());
                    labourerList.add(labourer);
                }
                populateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }



    public void populateListView()
    {
        mNoLabourersView = findViewById(R.id.no_labourer_text);
        mNoLabourersView.setText(Constants.NO_LABOURERS_REGISTERED_FOR_SCHEME_DESCRIPTION);
        if (labourerList.size() > 0) {
            customAdapter = new LabourerListItemAdapter(this, R.layout.labourerlist_item);
            customAdapter.setLabourerList(labourerList);
            listView = findViewById(R.id.listView);
            listView.setAdapter((ListAdapter) customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), LabourerDetailsActivity.class);
                    intent.putExtra(Constants.ID_KEY, labourerList.get(i).getLabourerID());
                    startActivity(intent);
                }
            });
        }
    }




    public void reloadData() {
        labourerList.clear();
       // AppDatabaseHelper databaseHelper = new AppDatabaseHelper(this);
       // labourerList = databaseHelper.getLabourersRegisteredForTheScheme();
        if (labourerList.size() <= 0) {
            mNoLabourersView.setVisibility(View.VISIBLE);
        }
        else {
            mNoLabourersView.setVisibility(View.GONE);
            customAdapter.setLabourerList(labourerList);
            customAdapter.notifyDataSetChanged();
        }
    }
    public void jobCardIssued(View view) {

        labourer.setJobCardIssued(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef =
                database.getReference("Labourers").child(labourer.getLabourerID());
        Map<String,Object> updates = new HashMap<>();
        updates.put("jobCardIssued",true);
        myRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                       @Override
                                                                       public void onSuccess(Void unused) {
                                                                           Toast.makeText(IssueJobCardActivity.this,"Labourer approved successfully",Toast.LENGTH_LONG).show();
                                                                                   Intent intent = new Intent(IssueJobCardActivity.this,PradhanHomeActivity.class);
                                                                           startActivity(intent);
                                                                       }
                                                                   });
    }


        @Override
    protected void onRestart() {
        super.onRestart();
        reloadData();
    }

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
