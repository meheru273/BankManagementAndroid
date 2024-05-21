package com.example.myapplication;

import android.content.Context;
import android.content.Intent;

public class NavigationManager {
    private static NavigationManager instance;

    private NavigationManager() {}

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void navigateToCashIn(Context context) {
        context.startActivity(new Intent(context, CashIn.class));
    }

    public void navigateToCashOut(Context context) {
        context.startActivity(new Intent(context, CashOut.class));
    }

    public void navigateToSendMoney(Context context) {
        context.startActivity(new Intent(context, SendMoney.class));
    }

    public void navigateToSavings(Context context) {
        context.startActivity(new Intent(context, Savings.class));
    }

    public void navigateToTransactions(Context context) {
        context.startActivity(new Intent(context, Transactions.class));
    }

    public void navigateToPayBill(Context context) {
        context.startActivity(new Intent(context, PayBill.class));
    }
}
