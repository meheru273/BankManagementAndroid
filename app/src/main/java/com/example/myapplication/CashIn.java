package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CashIn extends AppCompatActivity {
    private EditText editTextAmount;
    private Button cashInButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cash_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextAmount = findViewById(R.id.cashamount);
        cashInButton = findViewById(R.id.button);

        // Set up the button click listener
        cashInButton.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString().trim();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                // Now you can use this amount to cash in
                cashInMoney(amount);
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cashInMoney(double amount) {
        // Logic to add the amount to user's balance or account
        Toast.makeText(this, "Cashed in: $" + amount, Toast.LENGTH_LONG).show();
        // Add more functionality as needed
    }
}