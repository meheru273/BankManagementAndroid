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


        editTextAmount = findViewById(R.id.cashamount);
        cashInButton = findViewById(R.id.button);

        databaseReference = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER);

        // Example user ID and account ID passed from MainActivity, should be dynamically obtained based on your application logic
        String userId = "user1";  // For example, retrieve this dynamically based on user's selection or login
        String accountId = "account1";

        if (userId == null || accountId == null) {
            Toast.makeText(this, "User ID or Account ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        cashInButton.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString().trim();
            if (!amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    // Now you can use this amount to cash in
                    cashInMoney(userId, accountId, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cashInMoney(String userId, String accountId, double amount) {
        DatabaseReference accountRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER).child("total");
        accountRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Double currentBalance = dataSnapshot.getValue(Double.class);
                if (currentBalance != null) {
                    double newBalance = currentBalance + amount;
                    accountRef.setValue(newBalance)
                            .addOnSuccessListener(aVoid -> Toast.makeText(CashIn.this, "Cash in successful. New balance: " + newBalance, Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(CashIn.this, "Failed to update balance", Toast.LENGTH_SHORT).show());

                    // Optionally log this transaction in the 'transactions' node
                    logTransaction(userId, accountId, amount, newBalance);
                } else {
                    Toast.makeText(this, "Failed to fetch current balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error fetching balance", Toast.LENGTH_SHORT).show());
    }

    private void logTransaction(String userId, String accountId, double amount, double newBalance) {
        DatabaseReference transactionsRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.TRANSACTIONS);
        String transactionKey = transactionsRef.push().getKey();
        if (transactionKey != null) {
            Model transaction = new Model();
            transaction.setType("Cash In");
            transaction.setAmount(amount);
            transaction.setDate("2024-05-01 10:00:00");  // This should be dynamically set to the current date and time
            transaction.setDescription("Cash in ");
            transaction.setReceiverAccountNumber(null);


            transactionsRef.child(transactionKey).setValue(transaction)
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to log transaction: " + e.getMessage()));
        }
    }
}
