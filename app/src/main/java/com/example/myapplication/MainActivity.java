package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public CardView card1, card2, card3, card4, card5, card6;
    private FinancialServicesFacade financialServices;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        NavigationManager navManager = NavigationManager.getInstance();
        financialServices = new FinancialServicesFacade();

        card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            navManager.navigateToCashIn(this);
            financialServices.CashIn(this, 100);
        });

        card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            navManager.navigateToCashOut(this);
            financialServices.CashOut(this, 100);
        });

        card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            navManager.navigateToSendMoney(this);
            financialServices.SendMoney(this, 100);
        });

        card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> {
            navManager.navigateToSavings(this);
            financialServices.Savings(this, 100);
        });

        card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> {
            navManager.navigateToTransactions(this);
            financialServices.Transactions(this, 100);
        });

        card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> {
            navManager.navigateToPayBill(this);
            financialServices.PayBill(this, 100);
        });
    }
}
