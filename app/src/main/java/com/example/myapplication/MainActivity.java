package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/** Hosts the bottom-nav destinations (Home, Accounts, Scan, Bills, Profile) as fragments. */
public class MainActivity extends AppCompatActivity {

    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            goToLogin();
            return;
        }

        findViewById(R.id.nav_home).setOnClickListener(v -> showHome());
        findViewById(R.id.nav_accounts).setOnClickListener(v -> showAccounts());
        findViewById(R.id.nav_bills).setOnClickListener(v -> showBills());
        findViewById(R.id.nav_profile).setOnClickListener(v -> show("profile", 3, new ProfileFragment()));
        findViewById(R.id.nav_scan).setOnClickListener(v -> show("scan", -1, new ScanFragment()));

        if (savedInstanceState == null) showHome();
    }

    public void showHome() {
        show("home", 0, new HomeFragment());
    }

    public void showAccounts() {
        show("accounts", 1, new AccountsFragment());
    }

    public void showBills() {
        show("bills", 2, new BillsFragment());
    }

    private void show(String tag, int selectedIndex, Fragment fragment) {
        if (tag.equals(currentTag)) return;
        currentTag = tag;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        updateNav(selectedIndex);
    }

    private void updateNav(int selected) {
        tint(selected == 0, R.id.nav_home_icon, R.id.nav_home_label);
        tint(selected == 1, R.id.nav_accounts_icon, R.id.nav_accounts_label);
        tint(selected == 2, R.id.nav_bills_icon, R.id.nav_bills_label);
        tint(selected == 3, R.id.nav_profile_icon, R.id.nav_profile_label);
    }

    private void tint(boolean active, int iconId, int labelId) {
        @ColorRes int color = active ? R.color.plum : R.color.nav_inactive;
        int c = ContextCompat.getColor(this, color);
        ((ImageView) findViewById(iconId)).setColorFilter(c);
        ((TextView) findViewById(labelId)).setTextColor(c);
    }

    private void goToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
