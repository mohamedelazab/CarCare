package com.mobile.carcare.carcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferences {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String USER_KEY ="user_id";

    public MyPreferences(Context context){
        this.context =context;
        sharedPreferences =PreferenceManager.getDefaultSharedPreferences(context);
    }

    //1 for user and 2 for agency..
    public void setUserType(int userType){
        sharedPreferences.edit().putInt(USER_KEY, userType).apply();
    }

    public int getUserType(){
        return sharedPreferences.getInt(USER_KEY,-1);
    }

}
