package com.android.project.model;

public class FieldInspector {
   private String FieldinspectorId;
    private String name;
    private String mobile;
    private String Image;
    private String username;
    private String password;
    private String panchayath;

    public String getPanchayath() {
        return panchayath;
    }

    public void setPanchayath(String panchayath) {
        this.panchayath = panchayath;
    }

    public String getFieldinspectorId() {
        return FieldinspectorId;
    }

    public void setFieldinspectorId(String fieldinspectorId) {
        FieldinspectorId = fieldinspectorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
