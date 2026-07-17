package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Lightweight, offline account + session store backed by SharedPreferences.
 *
 * This is a self-contained demo authentication layer: an account (name, email,
 * password, PIN) lives on the device. Passwords and PINs are stored only as
 * SHA-256 hashes, never in plain text. Swap this out for a real backend
 * (e.g. Firebase Auth) when moving beyond a demo.
 */
public class SessionManager {

    private static final String PREFS = "bankmgmt_session";

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS_HASH = "pass_hash";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_BALANCE = "balance_bits";
    private static final String KEY_SAVINGS = "savings_bits";
    private static final String KEY_LOGGED_IN = "logged_in";

    private static final double DEFAULT_BALANCE = 4500d;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // --- Account state ---------------------------------------------------

    public boolean hasAccount() {
        return prefs.contains(KEY_EMAIL);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void createAccount(String name, String email, String password, String pin) {
        prefs.edit()
                .putString(KEY_NAME, name.trim())
                .putString(KEY_EMAIL, email.trim().toLowerCase())
                .putString(KEY_PASS_HASH, hash(password))
                .putString(KEY_PIN_HASH, hash(pin))
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
        putDouble(KEY_BALANCE, DEFAULT_BALANCE);
        putDouble(KEY_SAVINGS, 0d);
    }

    public boolean emailMatches(String email) {
        String stored = prefs.getString(KEY_EMAIL, null);
        return stored != null && stored.equalsIgnoreCase(email.trim());
    }

    public boolean checkPassword(String password) {
        return hash(password).equals(prefs.getString(KEY_PASS_HASH, ""));
    }

    public boolean checkPin(String pin) {
        return hash(pin).equals(prefs.getString(KEY_PIN_HASH, ""));
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    public void logout() {
        setLoggedIn(false);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "User");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    // --- Money -----------------------------------------------------------

    public double getBalance() {
        return getDouble(KEY_BALANCE, DEFAULT_BALANCE);
    }

    public void setBalance(double value) {
        putDouble(KEY_BALANCE, value);
    }

    public double getSavings() {
        return getDouble(KEY_SAVINGS, 0d);
    }

    public void setSavings(double value) {
        putDouble(KEY_SAVINGS, value);
    }

    // --- Helpers ---------------------------------------------------------

    private void putDouble(String key, double value) {
        prefs.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    private double getDouble(String key, double fallback) {
        if (!prefs.contains(key)) return fallback;
        return Double.longBitsToDouble(prefs.getLong(key, 0L));
    }

    private static String hash(String input) {
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
            // SHA-256 is guaranteed to exist on Android; fall back defensively.
            return Integer.toHexString(input.hashCode());
        }
    }
}
