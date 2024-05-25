//Account class create korte hobe taile error chole jabe
//Employee Account jeta Account ke extend kore
//Employee Account e employeeId, name, branch thakbe
//user account e name thakbe
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeRegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private EditText emailField, passwordField, employeeIdField, nameField;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_register);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("employees");

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        employeeIdField = findViewById(R.id.employeeIdField);
        nameField = findViewById(R.id.nameField);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEmployee();
            }
        });
    }

    private void registerEmployee() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String employeeId = employeeIdField.getText().toString().trim();
        String name = nameField.getText().toString().trim();

        if (name.isEmpty()) {
            nameField.setError("Name is required");
            nameField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please provide a valid email");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordField.setError("Password should be at least 6 characters long");
            passwordField.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String userId = auth.getCurrentUser().getUid();

                    Employee employee = Employee.getInstance(name, employeeId.isEmpty() ? null : employeeId);

                    // Save employee info to Realtime Database
                    dbRef.child(userId).setValue(employee)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(EmployeeRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                redirectToEmployeeDashboardActivity();
                            })
                            .addOnFailureListener(e -> Toast.makeText(EmployeeRegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(EmployeeRegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectToEmployeeDashboardActivity() {
        Intent intent = new Intent(EmployeeRegisterActivity.this, EmployeeDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
