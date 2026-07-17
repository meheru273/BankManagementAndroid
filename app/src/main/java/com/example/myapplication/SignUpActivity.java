package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm, tilPin;
    private TextInputEditText etName, etEmail, etPassword, etConfirm, etPin;
    private MaterialButton btnSignUp;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

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

        btnSignUp = findViewById(R.id.btnSignUp);
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

        setLoading(true);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        Toast.makeText(this, authError(task.getException()), Toast.LENGTH_LONG).show();
                        return;
                    }
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        setLoading(false);
                        Toast.makeText(this, getString(R.string.err_signup_failed), Toast.LENGTH_LONG).show();
                        return;
                    }
                    createProfile(user.getUid(), name, email, pin);
                });
    }

    private void createProfile(String uid, String name, String email, String pin) {
        new FinancialServicesFacade().createProfile(name, email, SessionManager.hash(pin), (ok, message) -> {
            if (!ok) {
                setLoading(false);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return;
            }
            new SessionManager(this).savePin(uid, pin);
            goToMain();
        });
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void setLoading(boolean loading) {
        btnSignUp.setEnabled(!loading);
        btnSignUp.setText(loading ? getString(R.string.please_wait) : getString(R.string.sign_up));
    }

    private String authError(Exception e) {
        return e == null ? getString(R.string.err_signup_failed) : e.getLocalizedMessage();
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
