package com.android.project.activitycontrollers.pradhan;

import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.activitycontrollers.rozgarsewak.AddLabourerActivity;
import com.android.project.adapters.LabourerListItemAdapter;
import com.android.project.model.Labourer;
import com.android.project.R;
//import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.activitycontrollers.UserSelectionActivity;
//import com.android.project.database.AppDatabaseHelper;
//import com.android.project.model.Labourer;
//import com.android.project.UserSelectionActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ApproveLabourerListActivity extends AppCompatActivity {
    private ListView listView = null;

    LabourerListItemAdapter customAdapter;
    ArrayList<Labourer> labourerList;
    private TextView mNoLabourersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlabourerlist);
        mNoLabourersView = findViewById(R.id.no_labourer_text);
        labourerList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Labourers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Labourer labourer = dataSnapshot.getValue(Labourer.class);// creating User object
                        labourer.setLabourerID(dataSnapshot.getKey()); //set the id to the user object
                        labourerList.add(labourer); //Adding the user object to the arraylist
                    }
                    populateListView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApproveLabourerListActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateListView() {
        if(labourerList.size() > 0) {
            mNoLabourersView.setVisibility(View.GONE);
            customAdapter = new  LabourerListItemAdapter(this,R.layout.labourerlist_item);
            customAdapter.setLabourerList(labourerList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int
                        i, long l){
                    Labourer labourer = labourerList.get(i);
                    Intent intent = new Intent(getApplicationContext(),
                            ApproveLabourerActivity.class);
                    intent.putExtra("id", labourer.getLabourerID());
                    startActivity(intent);
                }
            });
        } else {
            mNoLabourersView.setVisibility(View.VISIBLE);
            if(customAdapter!= null) {
                reloadData();
            }
        }


    }
    public void reloadData() {
        labourerList.clear();
        customAdapter.setLabourerList(labourerList);
        customAdapter.notifyDataSetChanged();
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
