package com.leaddealer.spinroad;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

import io.branch.referral.Branch;

public class Spinspace extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
