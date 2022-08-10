package com.example.bucketlist.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    Context context;
    private final String FIRST_TIME = "isFirstTime";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        this.context = context;
        String PREFERENCE_FILE_KEY = "com.example.bucketlist";
        this.sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();
    }


    public void setFirstRun() {
        editor.putBoolean(FIRST_TIME, false).commit();
        editor.commit();
    }

    public boolean isFirstRun() {
        return sharedPref.getBoolean(FIRST_TIME, true);
    }
}
