package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendMoney extends AppCompatActivity {
    private EditText editTextAmount;
    private EditText editTextReceiverAccountNumber;
    private Button sendButton;
    private DatabaseReference databaseReference; // Firebase database reference to the accounts

    private static final String TAG = "SendMoney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        // Initialize Firebase Database reference to the accounts node
// Initialize Firebase Database reference to the accounts node
        databaseReference = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.ACCOUNTS);

        editTextAmount = findViewById(R.id.sendAmount);
        editTextReceiverAccountNumber = findViewById(R.id.receiverAccountNumber);
        sendButton = findViewById(R.id.sendButton);

        // Example account ID passed from MainActivity, should be dynamically obtained based on your application logic
        String senderAccountId = "account1";  // For example, retrieve this dynamically based on user's selection or login
        if (senderAccountId == null) {
            Toast.makeText(this, "Sender Account ID is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        sendButton.setOnClickListener(v -> {
            String amountText = editTextAmount.getText().toString().trim();
            String receiverAccountNumber = editTextReceiverAccountNumber.getText().toString().trim();
            if (!amountText.isEmpty() && !receiverAccountNumber.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    // Now you can use this amount to send money
                    sendMoney(senderAccountId, receiverAccountNumber, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all required details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMoney(String senderAccountId, String receiverAccountNumber, double amount) {
        DatabaseReference senderAccountRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER).child("total");

        senderAccountRef.get().addOnSuccessListener(senderSnapshot -> {
            if (senderSnapshot.exists()) {
                Double senderBalance = senderSnapshot.getValue(Double.class);
                if (senderBalance != null) {
                    if (senderBalance >= amount) {
                        senderAccountRef.setValue(senderBalance - amount)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SendMoney.this, "Money sent successfully.", Toast.LENGTH_LONG).show();
                                    logTransaction(senderAccountId, receiverAccountNumber, amount);
                                })
                                .addOnFailureListener(e -> Toast.makeText(SendMoney.this, "Failed to update sender's balance", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to fetch current balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Sender account does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error fetching balance", Toast.LENGTH_SHORT).show());
    }

    private void logTransaction(String senderAccountId, String receiverAccountNumber, double amount) {
        DatabaseReference transactionsRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.TRANSACTIONS);
        String transactionKey = transactionsRef.push().getKey();
        if (transactionKey != null) {
            Model transaction = new Model();
            transaction.setType("transfer");
            transaction.setAmount(amount);
            transaction.setDate("2024-05-01 10:00:00");  // This should be dynamically set to the current date and time
            transaction.setDescription("Sent money to " + receiverAccountNumber);
            transaction.setReceiverAccountNumber(receiverAccountNumber);

            transactionsRef.child(transactionKey).setValue(transaction)
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to log transaction: " + e.getMessage()));
        }
    }
}
