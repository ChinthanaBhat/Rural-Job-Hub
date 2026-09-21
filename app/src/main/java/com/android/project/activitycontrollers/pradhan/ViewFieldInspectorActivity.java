package com.android.project.activitycontrollers.pradhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.fieldinspector.FieldinspectorHomeActivity;
import com.android.project.adapters.FieldInspectorListItemAdaptor;
import com.android.project.model.FieldInspector;
import com.android.project.utility.AppInstance;
import com.android.project.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFieldInspectorActivity extends AppCompatActivity {
    private ListView listView = null;

    FieldInspectorListItemAdaptor customAdapter;
    List<FieldInspector> fieldInspectorList;
    private TextView mNofieldinspector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_field_inspector);
        mNofieldinspector = findViewById(R.id.no_field_inspector_text);
        fieldInspectorList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("FieldInspectors");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        FieldInspector fieldInspector = dataSnapshot.getValue(FieldInspector.class);// creating User object
                        fieldInspector.setFieldinspectorId(dataSnapshot.getKey()); //set the id to the user object
                        fieldInspectorList.add(fieldInspector); //Adding the user object to the arraylist
                    }
                    populateListView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFieldInspectorActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });



    }

    public void populateListView() {
        if(fieldInspectorList.size() > 0) {
            mNofieldinspector.setVisibility(View.GONE);
            customAdapter = new FieldInspectorListItemAdaptor(this,R.layout.fieldinpector_list);
            customAdapter.setFieldInspectorList(fieldInspectorList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);

        } else {
            mNofieldinspector.setVisibility(View.VISIBLE);
            if(customAdapter!= null) {
                reloadData();
            }
        }
    }

    public  void reloadData() {
        fieldInspectorList.clear();
        customAdapter.setFieldInspectorList(fieldInspectorList);
        customAdapter.notifyDataSetChanged();
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
                Intent i = new Intent(this, FieldinspectorHomeActivity.class);
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