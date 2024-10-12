package com.kanjengdev.biomey.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

public class SharedPreferences {
    Context context;

    public SharedPreferences(Context context){
        this.context = context;
    }

    // File SharedPreference Name
    public static final String SHARED_PREFS_USER = "SHARED_PREFS_USER";
    public static final String SHARED_PREFS_CHECKPOINT = "SHARED_PREFS_CHECKPOINT";

    // Public Variable
    public static final String username = "username";
    public static final String uid = "uid";
    public static final String session = "session";
    public static final String sentences = "sentences";

    // Save Function
    public void saveDefaultUsername(String name){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(username, name);
        editor.apply();
    }

    public void saveDefaultUID(String id){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(uid, id);
        editor.apply();
    }

    public void saveSession(String ses){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_CHECKPOINT, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(session, ses);
        editor.apply();
    }

    public void saveSentences(String sent){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_CHECKPOINT, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(sentences, sent);
        editor.apply();
    }

    // Load Function
    public String loadUsername(){
        final android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);

        return sharedPreferences.getString(username, "");
    }

    public String loadUID(){
        final android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);

        return sharedPreferences.getString(uid, "");
    }

    public String loadSession(){
        final android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_CHECKPOINT, MODE_PRIVATE);

        return sharedPreferences.getString(session, "");
    }

    public String loadSentences(){
        final android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_CHECKPOINT, MODE_PRIVATE);

        return sharedPreferences.getString(sentences, "");
    }

}
