package com.example.myapplication;

import java.text.NumberFormat;
import java.util.Locale;

/** Formatting helpers for money and dates so every screen looks consistent. */
public final class MoneyUtils {

    private MoneyUtils() {}

    /** e.g. 4600.5 -> "$4,600.50" */
    public static String format(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "$" + nf.format(amount);
    }

    /** e.g. 4600.5 -> "+$4,600.50" / "-$4,600.50" based on direction. */
    public static String formatSigned(double amount, boolean incoming) {
        return (incoming ? "+" : "-") + format(amount);
    }
}
