package com.example.myapplication;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Static demo data for screens whose backend isn't built yet (multiple accounts,
 * bill billers, profile settings). Kept deliberately so the UI is complete; wire
 * these to Firebase later.
 */
public final class DemoData {

    private DemoData() {}

    public static List<Account> accounts() {
        List<Account> list = new ArrayList<>();
        list.add(new Account("Savings Account", "•••• 5115", "Available balance", 520400,
                Color.parseColor("#2B3A5B"), Color.WHITE, Color.parseColor("#26FFFFFF"),
                R.drawable.ic_savings, false));
        list.add(new Account("Current Account", "•••• 0336", "Available balance", 284830.75,
                Color.parseColor("#3A2B4A"), Color.WHITE, Color.parseColor("#26FFFFFF"),
                R.drawable.ic_landmark, false));
        list.add(new Account("Fixed Deposit", "Matures 12 Dec 2026", "Deposit value", 300000,
                Color.parseColor("#D8C24A"), Color.parseColor("#2A2431"), Color.parseColor("#17000000"),
                R.drawable.ic_lock, false));
        list.add(new Account("e-Wallet", "•••• 1018", "Available balance", 40000,
                Color.parseColor("#BD93AB"), Color.parseColor("#2A2431"), Color.parseColor("#47FFFFFF"),
                R.drawable.ic_wallet, false));
        list.add(new Account("Business Loan", "Outstanding", "Outstanding balance", 125000,
                Color.WHITE, Color.parseColor("#2A2431"), Color.parseColor("#F4EBEE"),
                R.drawable.ic_banknote, true));
        return list;
    }

    public static double netWorth() {
        return 520400 + 284830.75 + 300000 + 40000 - 125000;
    }

    public static class Biller {
        public final String name;
        public final int iconRes;
        public Biller(String name, int iconRes) { this.name = name; this.iconRes = iconRes; }
    }

    public static List<Biller> billers() {
        List<Biller> list = new ArrayList<>();
        list.add(new Biller("Electricity", R.drawable.ic_bolt));
        list.add(new Biller("Gas", R.drawable.ic_flame));
        list.add(new Biller("Water", R.drawable.ic_droplet));
        list.add(new Biller("Internet", R.drawable.ic_wifi));
        list.add(new Biller("Mobile", R.drawable.ic_smartphone));
        list.add(new Biller("Card Bill", R.drawable.ic_credit_card));
        list.add(new Biller("Tuition", R.drawable.ic_school));
        list.add(new Biller("Insurance", R.drawable.ic_shield));
        return list;
    }

    public static class Setting {
        public final String label;
        public final int iconRes;
        public Setting(String label, int iconRes) { this.label = label; this.iconRes = iconRes; }
    }

    public static List<Setting> settings() {
        List<Setting> list = new ArrayList<>();
        list.add(new Setting("Personal information", R.drawable.ic_user));
        list.add(new Setting("Security & PIN", R.drawable.ic_shield));
        list.add(new Setting("Linked bank accounts", R.drawable.ic_landmark));
        list.add(new Setting("Cards", R.drawable.ic_credit_card));
        list.add(new Setting("Notifications", R.drawable.ic_bell));
        list.add(new Setting("Help & support", R.drawable.ic_help));
        list.add(new Setting("About", R.drawable.ic_info));
        return list;
    }
}
