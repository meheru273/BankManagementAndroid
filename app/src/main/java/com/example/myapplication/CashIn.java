package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CashIn extends AppCompatActivity {
    private EditText editTextAmount;
    private Button cashInButton;
    private DatabaseReference databaseReference; // Firebase database reference to the accounts

    private static final String TAG = "CashIn";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        // Initialize Firebase Database reference to the accounts node
        databaseReference = FirebaseDatabase.getInstance().getReference("accounts");

        editTextAmount = findViewById(R.id.cashamount);
        cashInButton = findViewById(R.id.button);

        // Example account ID passed from MainActivity, should be dynamically obtained based on your application logic
        String accountId = "account1";  // For example, retrieve this dynamically based on user's selection or login
        if (accountId == null) {
            Toast.makeText(this, "Account ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        cashInButton.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString().trim();
            if (!amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    // Now you can use this amount to cash in
                    cashInMoney(accountId, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cashInMoney(String accountId, double amount) {
        DatabaseReference accountRef = databaseReference.child(accountId).child("balance");
        accountRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Double currentBalance = dataSnapshot.getValue(Double.class);
                if (currentBalance != null) {
                    double newBalance = currentBalance + amount;
                    accountRef.setValue(newBalance)
                            .addOnSuccessListener(aVoid -> Toast.makeText(CashIn.this, "Cash in successful. New balance: " + newBalance, Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(CashIn.this, "Failed to update balance", Toast.LENGTH_SHORT).show());

                    // Optionally log this transaction in the 'transactions' node
                    logTransaction(accountId, amount, newBalance);
                } else {
                    Toast.makeText(this, "Failed to fetch current balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error fetching balance", Toast.LENGTH_SHORT).show());
    }

    private void logTransaction(String accountId, double amount, double newBalance) {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(accountId);
        String transactionKey = transactionsRef.push().getKey();
        if (transactionKey != null) {
            Model transaction = new Model();
            transaction.setType("deposit");
            transaction.setAmount(amount);
            transaction.setDate("2024-05-01 10:00:00");  // This should be dynamically set to the current date and time
            transaction.setDescription("Cash in ");
            transaction.setReceiverAccountNumber(null);

            transactionsRef.child(transactionKey).setValue(transaction)
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to log transaction: " + e.getMessage()));
        }
    }
}
