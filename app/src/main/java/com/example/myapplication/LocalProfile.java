package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * On-device cache of a few user details so the app can greet the user and
 * pre-fill their mobile number on the login screen without a network round-trip.
 * The authoritative copy lives in Firebase; this is just for convenience/offline.
 */
public class LocalProfile {

    private static final String PREFS = "bankmgmt_profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NID = "nid";

    private final SharedPreferences prefs;

    public LocalProfile(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(String name, String phone, String nid) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_PHONE, phone)
                .putString(KEY_NID, nid)
                .apply();
    }

    public void savePhone(String phone) {
        prefs.edit().putString(KEY_PHONE, phone).apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, null);
    }

    public void clearIdentity() {
        // Keep the phone number so the login screen can pre-fill it; drop name/nid.
        prefs.edit().remove(KEY_NAME).remove(KEY_NID).apply();
    }
}
