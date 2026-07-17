package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Local, on-device cache of the user's app-unlock PIN.
 *
 * Identity, balance and transaction history all live in Firebase now. The PIN
 * is purely a fast local gate shown on every app open: we store only its
 * SHA-256 hash, tagged with the Firebase uid it belongs to, so it works
 * instantly and offline without a network round-trip.
 */
public class SessionManager {

    private static final String PREFS = "bankmgmt_gate";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_UID = "uid";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Stores the PIN (hashing it) for the given Firebase user. Used at sign-up. */
    public void savePin(String uid, String pin) {
        savePinHash(uid, hash(pin));
    }

    /** Stores an already-hashed PIN for the given user. Used at login (hash comes from the DB). */
    public void savePinHash(String uid, String pinHash) {
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_PIN_HASH, pinHash)
                .apply();
    }

    /** True if we hold a PIN for this uid (so the lock screen can be shown). */
    public boolean hasPinFor(String uid) {
        return uid != null
                && uid.equals(prefs.getString(KEY_UID, null))
                && prefs.contains(KEY_PIN_HASH);
    }

    public boolean checkPin(String pin) {
        return hash(pin).equals(prefs.getString(KEY_PIN_HASH, ""));
    }

    public void clear() {
        prefs.edit().remove(KEY_PIN_HASH).remove(KEY_UID).apply();
    }

    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}
