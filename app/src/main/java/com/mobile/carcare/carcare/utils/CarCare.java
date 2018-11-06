package com.mobile.carcare.carcare.utils;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class CarCare extends Application {

    FirebaseAnalytics firebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAnalytics =FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}