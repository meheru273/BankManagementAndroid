package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

/** Quick-unlock screen shown on every app open when a PIN is cached for the signed-in user. */
public class PinActivity extends AppCompatActivity {

    private TextInputLayout tilPin;
    private TextInputEditText etPin;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        session = new SessionManager(this);
        tilPin = findViewById(R.id.tilPin);
        etPin = findViewById(R.id.etPin);

        MaterialButton btnUnlock = findViewById(R.id.btnUnlock);
        btnUnlock.setOnClickListener(v -> verify());

        etPin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                tilPin.setError(null);
                if (s.length() == 4) verify();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.tvUsePassword).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            session.clear();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void verify() {
        String pin = etPin.getText() == null ? "" : etPin.getText().toString();
        if (pin.length() != 4) {
            tilPin.setError(getString(R.string.err_pin_length));
            return;
        }
        if (!session.checkPin(pin)) {
            tilPin.setError(getString(R.string.wrong_pin));
            etPin.setText("");
            return;
        }
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
