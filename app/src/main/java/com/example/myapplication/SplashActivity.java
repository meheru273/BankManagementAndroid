package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Launcher entry point. Decides where the user should land:
 *   no account        -> Sign Up
 *   account, logged out -> Login
 *   account, logged in  -> PIN lock
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::route, 700);
    }

    private void route() {
        SessionManager session = new SessionManager(this);
        Class<?> next;
        if (!session.hasAccount()) {
            next = SignUpActivity.class;
        } else if (!session.isLoggedIn()) {
            next = LoginActivity.class;
        } else {
            next = PinActivity.class;
        }
        startActivity(new Intent(this, next));
        finish();
    }
}
