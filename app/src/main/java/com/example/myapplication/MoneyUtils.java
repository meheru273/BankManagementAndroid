package com.example.myapplication;

import java.text.NumberFormat;
import java.util.Locale;

/** Formatting helpers for money so every screen looks consistent. */
public final class MoneyUtils {

    private MoneyUtils() {}

    /** Bangladeshi Taka symbol used across the app. */
    public static final String CURRENCY = "৳"; // ৳

    // en-IN gives the lakh grouping (5,20,400.00) used in the design.
    private static final Locale LOCALE = new Locale("en", "IN");

    /** e.g. 5000.5 -> "৳ 5,000.50" */
    public static String format(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return CURRENCY + " " + nf.format(amount);
    }

    /** e.g. -> "+ ৳ 5,000.50" / "- ৳ 5,000.50" */
    public static String formatSigned(double amount, boolean incoming) {
        return (incoming ? "+ " : "- ") + format(amount);
    }

    /** Groups a raw typed integer/decimal string, e.g. "520400" -> "5,20,400". */
    public static String group(String raw) {
        if (raw == null || raw.isEmpty()) return "0";
        String[] parts = raw.split("\\.", -1);
        long whole;
        try {
            whole = Long.parseLong(parts[0].isEmpty() ? "0" : parts[0]);
        } catch (NumberFormatException e) {
            return raw;
        }
        String grouped = NumberFormat.getIntegerInstance(LOCALE).format(whole);
        return parts.length > 1 ? grouped + "." + parts[1] : grouped;
    }
}
