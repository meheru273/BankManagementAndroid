package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm, tilPin;
    private TextInputEditText etName, etEmail, etPassword, etConfirm, etPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);
        tilPin = findViewById(R.id.tilPin);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        etPin = findViewById(R.id.etPin);

        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> attemptSignUp());

        findViewById(R.id.tvGoLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        clearErrors();
        String name = value(etName);
        String email = value(etEmail);
        String password = value(etPassword);
        String confirm = value(etConfirm);
        String pin = value(etPin);

        boolean valid = true;
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.err_required));
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            valid = false;
        }
        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.err_password_length));
            valid = false;
        }
        if (!confirm.equals(password)) {
            tilConfirm.setError(getString(R.string.err_password_match));
            valid = false;
        }
        if (pin.length() != 4) {
            tilPin.setError(getString(R.string.err_pin_length));
            valid = false;
        }
        if (!valid) return;

        SessionManager session = new SessionManager(this);
        session.createAccount(name, email, password, pin);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);
        tilPin.setError(null);
    }

    private String value(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}
