package com.android.project.activitycontrollers.rozgarsewak;


import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.project.activitycontrollers.UserSelectionActivity;
import com.android.project.model.Attendance;
import com.android.project.R;
import com.android.project.activitycontrollers.pradhan.AdminChangePasswordActivity;
import com.android.project.adapters.ViewAttendanceListItemAdapter;
//import com.android.project.model.Attendance;
//import com.android.project.model.Attendance;
//import com.android.project.pradhan.AdminChangePasswordActivity;
import com.android.project.utility.AppInstance;

import com.android.project.utility.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {
    private ListView listView = null;

    ViewAttendanceListItemAdapter customAdapter;
    List<Attendance> attendanceList;
    private TextView mNoAttendanceView;
    long labourerID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewattendancelist);
        mNoAttendanceView = findViewById(R.id.no_attendance_text);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference attendanceRef = database.getReference("Attendance");
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendanceList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attendance attendance = snapshot.getValue(Attendance.class);
                    if (attendance != null) {
                        attendanceList.add(attendance);
                    }
                }
                populateListView();

                // Call the callback with the result
//                callback.onAttendanceListFetched(attendances);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
//                callback.onError(databaseError.toException());
            }
        });

    }


    public void populateListView() {
        if (attendanceList.size() > 0) {
            mNoAttendanceView.setVisibility(View.GONE);
            customAdapter = new ViewAttendanceListItemAdapter(this, R.layout.viewattendancelist_item);
            customAdapter.setAttendanceList(attendanceList);
            listView = findViewById(R.id.listView);
            listView.setAdapter(customAdapter);

        } else {
            mNoAttendanceView.setVisibility(View.VISIBLE);
        }
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