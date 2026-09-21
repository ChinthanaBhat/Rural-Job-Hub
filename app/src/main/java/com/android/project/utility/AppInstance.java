package com.android.project.utility;

import android.app.Application;
import android.content.Context;

import com.android.project.model.FieldInspector;
import com.android.project.model.Labourer;
import com.android.project.model.RozgarSewak;



public class AppInstance extends Application {
    public static AppInstance instance = null;
    private boolean isAdminUser;

    private static Context mContext;
    private RozgarSewak currentRozgarSewak;
    private FieldInspector currentFieldInspector;
    private Labourer currentLabourer;

    public static Context getInstance()
    {
        if (null == instance) {
            instance = new AppInstance();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }


    public FieldInspector getCurrentFieldInspector() {
        return currentFieldInspector;
    }

    public void setCurrentFieldInspector(FieldInspector currentFieldInspector) {
        this.currentFieldInspector = currentFieldInspector;
    }

    public boolean isAdminUser()
    {
        return isAdminUser;
    }

    public void setAdminUser(boolean isAdminUser)
    {
        this.isAdminUser = isAdminUser;
    }

    public RozgarSewak getCurrentRozgarSewak() {
        return currentRozgarSewak;
    }

    public void setCurrentRozgarSewak(RozgarSewak currentRozgarSewak) {
        this.currentRozgarSewak = currentRozgarSewak;
    }

    public Labourer getCurrentLabourer() {
        return currentLabourer;
    }

    public void setCurrentLabourer(Labourer currentLabourer) {
        this.currentLabourer = currentLabourer;
    }

}


