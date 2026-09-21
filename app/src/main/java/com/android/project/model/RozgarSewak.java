package com.android.project.model;

public class RozgarSewak
{
    private String name;

    private String username;
    private String password;
    private String mobile;
    private String profilePath;
    private String gramPanchayath;
    private String sewakID;

    public String getGramPanchayath() {
        return gramPanchayath;
    }

    public void setGramPanchayath(String gramPanchayath) {
        this.gramPanchayath = gramPanchayath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setSewakID(String sewakID) {
        this.sewakID = sewakID;
    }

    public String getSewakID() {
        return sewakID;
    }

}