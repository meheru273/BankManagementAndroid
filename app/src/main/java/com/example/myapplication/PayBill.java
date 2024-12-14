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

public class PayBill extends AppCompatActivity {
    private EditText editTextAmount;
    private EditText editTextReceiverAccountNumber;
    private Button payButton;
    private DatabaseReference databaseReference; // Firebase database reference to the accounts

    private static final String TAG = "PayBill";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);

        // Initialize Firebase Database reference to the accounts node
        databaseReference = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.ACCOUNTS);

        editTextAmount = findViewById(R.id.billAmount);
        editTextReceiverAccountNumber = findViewById(R.id.billReceiverAccountNumber);
        payButton = findViewById(R.id.payButton);

        // Example account ID passed from MainActivity, should be dynamically obtained based on your application logic
        String payerAccountId = "account1";  // For example, retrieve this dynamically based on user's selection or login
        if (payerAccountId == null) {
            Toast.makeText(this, "Payer Account ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        payButton.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString().trim();
            String receiverAccountNumber = editTextReceiverAccountNumber.getText().toString().trim();
            if (!amountText.isEmpty() && !receiverAccountNumber.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    // Now you can use this amount to pay bill
                    payBill(payerAccountId, receiverAccountNumber, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all required details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payBill(String payerAccountId, String receiverAccountNumber, double amount) {
        DatabaseReference payerAccountRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER).child("total");

        payerAccountRef.get().addOnSuccessListener(payerSnapshot -> {
            if (payerSnapshot.exists()) {
                Double payerBalance = payerSnapshot.getValue(Double.class);
                if (payerBalance != null) {
                    if (payerBalance >= amount) {
                        payerAccountRef.setValue(payerBalance - amount)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PayBill.this, "Bill paid successfully.", Toast.LENGTH_LONG).show();
                                    logTransaction(payerAccountId, receiverAccountNumber, amount);
                                })
                                .addOnFailureListener(e -> Toast.makeText(PayBill.this, "Failed to update payer's balance", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to fetch current balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Payer account does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error fetching balance", Toast.LENGTH_SHORT).show());
    }

    private void logTransaction(String payerAccountId, String receiverAccountNumber, double amount) {
        DatabaseReference transactionsRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.TRANSACTIONS);
        String transactionKey = transactionsRef.push().getKey();
        if (transactionKey != null) {
            Model transaction = new Model();
            transaction.setType("pay bill");
            transaction.setAmount(amount);
            transaction.setDate("2024-05-01 10:00:00");  // This should be dynamically set to the current date and time
            transaction.setDescription("Paid bill to " + receiverAccountNumber);
            transaction.setReceiverAccountNumber(receiverAccountNumber);

            transactionsRef.child(transactionKey).setValue(transaction)
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to log transaction: " + e.getMessage()));
        }
    }
}
