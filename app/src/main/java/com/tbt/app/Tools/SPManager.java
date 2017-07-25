package com.tbt.app.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.tbt.app.Constants.Config;

/**
 * Created by bradley on 04-03-2017.
 */

public class SPManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    public SPManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Config.SHARED_PREFS, Context.MODE_PRIVATE);
    }
    public void edit() {
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    public void addEntity(String key, String value) {
        sharedPreferencesEditor.putString(key, value);
    }
    public void addEntity(String key, boolean value) {
        sharedPreferencesEditor.putBoolean(key, value);
    }
    public void removeEntity(String key) {
        if (hasEntity(key)) {
            sharedPreferencesEditor.remove(key);
        }
    }
    public void commit() {
        sharedPreferencesEditor.commit();
    }
    public String getEntity(String key) {
        return sharedPreferences.getString(key, null);
    }
    public boolean getEntityBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public boolean hasEntity(String key) { return sharedPreferences.contains(key); }
}
