package com.mkdenis.twitterclient;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceSettingsManager {

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static void init(Context context){
        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static void addString(String key, String text) {
        editor.putString(key, text);
        editor.commit();
    }

    public static void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public static String getString(String key) {
        return pref.getString(key, null);
    }
}
