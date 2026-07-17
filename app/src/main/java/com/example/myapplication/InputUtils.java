package com.example.myapplication;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Small input-parsing helpers shared by the transaction screens. */
public final class InputUtils {

    private InputUtils() {}

    /**
     * Reads a positive money amount from the field. On empty/invalid input it
     * shows an inline error on the layout and returns null.
     */
    public static Double readAmount(TextInputLayout til, TextInputEditText et, String emptyMsg) {
        til.setError(null);
        String s = et.getText() == null ? "" : et.getText().toString().trim();
        if (s.isEmpty()) {
            til.setError(emptyMsg);
            return null;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            til.setError(emptyMsg);
            return null;
        }
    }

    public static String readText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
