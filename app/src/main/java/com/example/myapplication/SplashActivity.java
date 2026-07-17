package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Launcher entry point. Routing is driven by Firebase Auth:
 *   not signed in            -> Login
 *   signed in, PIN cached    -> PIN lock
 *   signed in, no PIN cached -> straight to the dashboard
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::route, 700);
    }

    private void route() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Class<?> next;
        if (user == null) {
            next = LoginActivity.class;
        } else if (new SessionManager(this).hasPinFor(user.getUid())) {
            next = PinActivity.class;
        } else {
            next = MainActivity.class;
        }
        startActivity(new Intent(this, next));
        finish();
    }
}
