package com.android.project.model;

import java.util.Calendar;
import java.util.List;

public class Attendance {
    private String attendanceID;
    private String date;
    private String labourerID;
    private String jobID;

    public String getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(String attendanceID) {
        this.attendanceID = attendanceID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabourerID() {
        return labourerID;
    }

    public void setLabourerID(String labourerID) {
        this.labourerID = labourerID;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }


}
