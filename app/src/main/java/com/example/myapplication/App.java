package com.example.myapplication;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Application entry point. Enables Realtime Database disk persistence once, up
 * front, so balance and transactions survive brief network drops. This must run
 * before any other FirebaseDatabase usage, which is why it lives here.
 */
public class App extends Application {

    private static boolean persistenceConfigured = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // The redesigned UI is light-only; lock night mode off for consistency.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (!persistenceConfigured) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } catch (Exception ignored) {
                // Already configured for this process; safe to ignore.
            }
            persistenceConfigured = true;
        }
    }
}
