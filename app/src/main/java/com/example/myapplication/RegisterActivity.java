package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** KYC form shown once, right after a new phone number is verified. */
public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_PHONE = "phone";

    private TextInputLayout tilName, tilNid, tilDob, tilAddress, tilBank, tilPin, tilPin2;
    private TextInputEditText etName, etNid, etDob, etAddress, etBank, etPin, etPin2;
    private MaterialButton btnRegister;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = getIntent().getStringExtra(EXTRA_PHONE);

        tilName = findViewById(R.id.tilName);
        tilNid = findViewById(R.id.tilNid);
        tilDob = findViewById(R.id.tilDob);
        tilAddress = findViewById(R.id.tilAddress);
        tilBank = findViewById(R.id.tilBank);
        tilPin = findViewById(R.id.tilPin);
        tilPin2 = findViewById(R.id.tilPin2);
        etName = findViewById(R.id.etName);
        etNid = findViewById(R.id.etNid);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        etBank = findViewById(R.id.etBank);
        etPin = findViewById(R.id.etPin);
        etPin2 = findViewById(R.id.etPin2);
        btnRegister = findViewById(R.id.btnRegister);

        ((android.widget.TextView) findViewById(R.id.reg_phone))
                .setText(getString(R.string.verified_phone, phone));

        etDob.setOnClickListener(v -> showDatePicker());
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        c.set(2000, 0, 1);
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, day);
            etDob.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(picked.getTime()));
            tilDob.setError(null);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void attemptRegister() {
        clearErrors();
        String name = value(etName), nid = value(etNid), dob = value(etDob),
                address = value(etAddress), bank = value(etBank), pin = value(etPin), pin2 = value(etPin2);

        boolean valid = true;
        if (name.isEmpty()) { tilName.setError(getString(R.string.err_required)); valid = false; }
        if (nid.isEmpty()) { tilNid.setError(getString(R.string.err_required)); valid = false; }
        if (dob.isEmpty()) { tilDob.setError(getString(R.string.err_dob)); valid = false; }
        if (address.isEmpty()) { tilAddress.setError(getString(R.string.err_required)); valid = false; }
        if (bank.isEmpty()) { tilBank.setError(getString(R.string.err_required)); valid = false; }
        if (pin.length() != 4) { tilPin.setError(getString(R.string.err_pin_length)); valid = false; }
        if (!pin2.equals(pin)) { tilPin2.setError(getString(R.string.err_pin_match)); valid = false; }
        if (!valid) return;

        setLoading(true);
        Profile profile = new Profile(name, phone, nid, dob, address, bank);
        FinancialServicesFacade facade = new FinancialServicesFacade();
        facade.createProfile(profile, SessionManager.hash(pin), (ok, message) -> {
            if (!ok) {
                setLoading(false);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return;
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build());
                new SessionManager(this).savePin(user.getUid(), pin);
            }
            new LocalProfile(this).save(name, phone, nid);
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
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? getString(R.string.please_wait) : getString(R.string.create_account));
    }

    private void clearErrors() {
        tilName.setError(null);
        tilNid.setError(null);
        tilDob.setError(null);
        tilAddress.setError(null);
        tilBank.setError(null);
        tilPin.setError(null);
        tilPin2.setError(null);
    }

    private String value(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}
