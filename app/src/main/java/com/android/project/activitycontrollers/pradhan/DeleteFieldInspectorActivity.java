package com.android.project.activitycontrollers.pradhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.R;
import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.FieldInspector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteFieldInspectorActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<FieldInspector> fieldInspectorArrayList;
    Spinner fieldinspectorSP;
    TextView fieldinspectordetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_field_inspector);

        fieldinspectordetail = findViewById(R.id.no_fieldinspector);

        fieldinspectorSP = findViewById(R.id.fieldinspector_list);

        fieldInspectorArrayList = (ArrayList<FieldInspector>)getfieldInspectorArrayList();

    }

    public void deleteFieldInspector(View view)
    {
        if (fieldInspectorArrayList == null || fieldInspectorArrayList.size()==0)
        {
            Toast.makeText(this, "No Field Inspector to delete", Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            //Setting Dialog Title
            alertDialog.setTitle("Delete Field Inspector....");

            //Setting Dialog Message
            alertDialog.setMessage("Are you sure you want to delete this Field Inspector?");

            //Setting Icon to Delete
            alertDialog.setIcon(R.drawable.delete);
            alertDialog.setCancelable(false);

            //Setting Positive "YES" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Write your code here to execute after dialog

                            FieldInspector selectfieldinspector =  ((fieldInspectorArrayList.get(fieldinspectorSP.getSelectedItemPosition())));
                            deleteFieldInspector(selectfieldinspector);

                            Toast.makeText(DeleteFieldInspectorActivity.this, "Field Inspector deleted successfully", Toast.LENGTH_LONG).show();
                        }
                    });

            //Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Write your code here to execute after dialog

                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
    }

    public void deleteFieldInspector(FieldInspector fieldInspector) {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("FieldInspectors");

        String fieldInspectorId = fieldInspector.getFieldinspectorId();


        // Delete the student
        databaseRef.child(fieldInspectorId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Field Inspector successfully deleted
                        updateUI();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while deleting the student
                    }
                });
    }

    public void updateUI()
    {
        fieldinspectordetail.setText("");
        fieldInspectorArrayList.clear();
        fieldInspectorArrayList = (ArrayList<FieldInspector>) getfieldInspectorArrayList();
        final String[] fieldinspectornames = new String[fieldInspectorArrayList.size()];
        for(int i = 0; i < fieldInspectorArrayList.size(); i++)
        {
            fieldinspectornames[i] = fieldInspectorArrayList.get(i).getName();
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fieldinspectornames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//The drop down view
        fieldinspectorSP.setAdapter(adapter);

        if(fieldInspectorArrayList.size()==0)
        {
            fieldinspectordetail.setText("No Field Inspector to delete");
        }
    }


    public List<FieldInspector> getfieldInspectorArrayList() {

        List<FieldInspector> approvedfieldInspector = new ArrayList<>();

        // Get a reference to the students node in your Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("FieldInspectors");


        // Attach a listener to retrieve the data
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                approvedfieldInspector.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    FieldInspector fieldInspector = childSnapshot.getValue(FieldInspector.class);
                    fieldInspector.setFieldinspectorId(childSnapshot.getKey());
                    approvedfieldInspector.add(fieldInspector);

                }

                // Assuming approvedStudents is the ArrayList of approved students
                String[] names = new String[approvedfieldInspector.size()];

                // Populate the studentNames array with student names
                for (int i = 0; i < approvedfieldInspector.size(); i++) {
                    names[i] = approvedfieldInspector.get(i).getName();
                }

                adapter = new ArrayAdapter<String>(DeleteFieldInspectorActivity.this, android.R.layout.simple_spinner_dropdown_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//The drop down view
                fieldinspectorSP.setAdapter(adapter);

                if (approvedfieldInspector.size() == 0) {
                    fieldinspectordetail.setText("No Field Inspector to delete");
                }

                fieldinspectorSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        FieldInspector fieldInspector = approvedfieldInspector.get(position);
                        if (fieldInspector != null) {
                            fieldinspectordetail.setText("Name: " + fieldInspector.getName() +  "\nMobile: " + fieldInspector.getMobile());
                        } else {
                            fieldinspectordetail.setText("No field inspector to delete");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });

        return approvedfieldInspector;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        //logout functionality
        switch (item.getItemId()) {
            case R.id.logout:
                Intent i = new Intent(this, UserSelectionActivity.class);
                startActivity(i);
                finish();
                return true;


        }
        return false;
    }

}