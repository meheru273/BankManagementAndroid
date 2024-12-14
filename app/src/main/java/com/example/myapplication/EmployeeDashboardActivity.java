package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Employee.getInstance(null, null, null).employeeInstanceDestroy();
            redirectToLogin();
        });
    }

    /**
     * Redirects to the Login Activity.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(EmployeeDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
