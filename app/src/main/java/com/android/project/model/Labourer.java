package com.android.project.model;

public class Labourer {
    private String name;
    private String username;
    private String password;
    private String mobile;
    private int age;
    private String gender;
    private String labourerID;

    public void setLabourerID(String labourerID) {
        this.labourerID = labourerID;
    }

    public String getLabourerID() {
        return labourerID;
    }

    private String profilePath;
    private boolean approved;
    private boolean jobCardIssued;
    private String qrCodePath;
    private String gramPanchayath;
    private String aadharID;
    private String bplCardNumber;
    private String voterID;
    private String bank;
    private String ifsc;
    private String account;
    private boolean registeredForScheme;
    private boolean allottedForJob;
    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isAllottedForJob() {
        return allottedForJob;
    }

    public void setAllottedForJob(boolean allottedForJob) {
        this.allottedForJob = allottedForJob;
    }

    public boolean isRegisteredForScheme() {
        return registeredForScheme;
    }

    public void setRegisteredForScheme(boolean registeredForScheme) {
        this.registeredForScheme = registeredForScheme;
    }

    public String getAadharID() {
        return aadharID;
    }

    public void setAadharID(String aadharID) {
        this.aadharID = aadharID;
    }

    public String getBplCardNumber() {
        return bplCardNumber;
    }

    public void setBplCardNumber(String bplCardNumber) {
        this.bplCardNumber = bplCardNumber;
    }

    public String getVoterID() {
        return voterID;
    }

    public void setVoterID(String voterID) {
        this.voterID = voterID;
    }

    public String getGramPanchayath() {
        return gramPanchayath;
    }

    public void setGramPanchayath(String gramPanchayath) {
        this.gramPanchayath = gramPanchayath;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public boolean isJobCardIssued() {
        return jobCardIssued;
    }

    public void setJobCardIssued(boolean jobCardIssued) {
        this.jobCardIssued = jobCardIssued;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}