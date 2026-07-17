package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private CardView card1, card2, card3, card4, card5, card6;
    private TextView balanceView, nameView, emailView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        NavigationManager navManager = NavigationManager.getInstance();

        balanceView = findViewById(R.id.coins);
        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.name2);

        card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> navManager.navigateToCashIn(this));

        card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> navManager.navigateToCashOut(this));

        card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> navManager.navigateToSendMoney(this));

        card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> navManager.navigateToSavings(this));

        card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> navManager.navigateToTransactions(this));

        card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> navManager.navigateToPayBill(this));

        Button logout = findViewById(R.id.button2);
        logout.setOnClickListener(v -> {
            session.logout();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh so the balance reflects any operation done on a child screen.
        nameView.setText(session.getName());
        emailView.setText(session.getEmail());
        balanceView.setText(MoneyUtils.format(session.getBalance()));
    }
}
