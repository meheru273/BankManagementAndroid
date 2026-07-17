package com.example.myapplication;

/** A bank account card shown on Home / Accounts (demo data for now — backend later). */
public class Account {
    public final String name;
    public final String number;
    public final String balanceLabel;
    public final double balance;
    public final int bgColor;      // card background
    public final int textColor;    // on-card text
    public final int iconBgColor;  // icon chip background
    public final int iconRes;
    public final boolean bordered;

    public Account(String name, String number, String balanceLabel, double balance,
                   int bgColor, int textColor, int iconBgColor, int iconRes, boolean bordered) {
        this.name = name;
        this.number = number;
        this.balanceLabel = balanceLabel;
        this.balance = balance;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.iconBgColor = iconBgColor;
        this.iconRes = iconRes;
        this.bordered = bordered;
    }
}
