package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/** Phone-number sign-in: enter mobile -> receive SMS code -> verify. */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ViewFlipper flipper;
    private TextInputLayout tilPhone, tilOtp;
    private TextInputEditText etPhone, etOtp;
    private MaterialButton btnSendCode, btnVerify;
    private TextView otpSubtitle;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneE164;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Auto-retrieval or instant validation — sign in directly.
                    signIn(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    setBusy(false);
                    Toast.makeText(LoginActivity.this, friendlyError(e), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = id;
                    resendToken = token;
                    setBusy(false);
                    showOtp();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        // Debug builds run on emulators/Appetize with no Play Services, where Phone Auth
        // falls back to a reCAPTCHA web challenge that loops. Disable app verification so
        // test phone numbers work without reCAPTCHA. NEVER ships in a release build.
        if (BuildConfig.DEBUG) {
            auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        }
        flipper = findViewById(R.id.login_flipper);
        tilPhone = findViewById(R.id.tilPhone);
        tilOtp = findViewById(R.id.tilOtp);
        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        otpSubtitle = findViewById(R.id.otp_subtitle);

        // Pre-fill the saved number (login only using the code).
        String saved = new LocalProfile(this).getPhone();
        if (saved != null && saved.startsWith("+880")) etPhone.setText(saved.substring(4));

        btnSendCode.setOnClickListener(v -> onSendCode());
        btnVerify.setOnClickListener(v -> onVerify());
        findViewById(R.id.otp_back).setOnClickListener(v -> flipper.setDisplayedChild(0));
        findViewById(R.id.tvResend).setOnClickListener(v -> onResend());
    }

    private void onSendCode() {
        tilPhone.setError(null);
        String digits = etPhone.getText() == null ? "" : etPhone.getText().toString().replaceAll("[^0-9]", "");
        if (digits.startsWith("0")) digits = digits.substring(1);
        if (digits.length() < 9) {
            tilPhone.setError(getString(R.string.err_enter_phone));
            return;
        }
        phoneE164 = "+880" + digits;
        setBusy(true);
        startVerification(null);
    }

    private void startVerification(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions.Builder b = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks);
        if (token != null) b.setForceResendingToken(token);
        PhoneAuthProvider.verifyPhoneNumber(b.build());
    }

    private void showOtp() {
        otpSubtitle.setText(getString(R.string.code_sent_to, phoneE164));
        etOtp.setText("");
        tilOtp.setError(null);
        flipper.setDisplayedChild(1);
    }

    private void onVerify() {
        tilOtp.setError(null);
        String code = etOtp.getText() == null ? "" : etOtp.getText().toString().trim();
        if (code.length() < 6) {
            tilOtp.setError(getString(R.string.err_enter_code));
            return;
        }
        if (verificationId == null) {
            Toast.makeText(this, R.string.err_enter_code, Toast.LENGTH_SHORT).show();
            return;
        }
        setBusy(true);
        signIn(PhoneAuthProvider.getCredential(verificationId, code));
    }

    private void onResend() {
        if (phoneE164 == null) return;
        setBusy(true);
        startVerification(resendToken);
    }

    private void signIn(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                setBusy(false);
                Toast.makeText(this, R.string.err_invalid_code, Toast.LENGTH_LONG).show();
                return;
            }
            onAuthed();
        });
    }

    private void onAuthed() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            setBusy(false);
            Toast.makeText(this, R.string.err_signup_failed, Toast.LENGTH_LONG).show();
            return;
        }
        if (phoneE164 != null) new LocalProfile(this).savePhone(phoneE164);

        FinancialServicesFacade facade = new FinancialServicesFacade();
        facade.checkProfileExists(exists -> {
            if (exists) {
                String uid = user.getUid();
                facade.loadPinHash(hash -> {
                    if (hash != null) new SessionManager(this).savePinHash(uid, hash);
                    goToMain();
                });
            } else {
                Intent i = new Intent(this, RegisterActivity.class);
                i.putExtra(RegisterActivity.EXTRA_PHONE, phoneE164);
                startActivity(i);
                finish();
            }
        });
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void setBusy(boolean busy) {
        btnSendCode.setEnabled(!busy);
        btnVerify.setEnabled(!busy);
        btnSendCode.setText(busy ? getString(R.string.please_wait) : getString(R.string.send_code));
        btnVerify.setText(busy ? getString(R.string.please_wait) : getString(R.string.verify));
    }

    private String friendlyError(FirebaseException e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) return getString(R.string.err_enter_phone);
        if (e instanceof FirebaseTooManyRequestsException) return getString(R.string.err_too_many);
        String m = e.getLocalizedMessage();
        return m != null ? m : getString(R.string.err_signup_failed);
    }
}
