package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SendMoney extends AppCompatActivity {

    private static final String TAG = "SendMoney";
    private EditText editTextSenderEmail, editTextReceiverAccount, editTextAmount;
    private Button buttonSendMoney;
    private DatabaseReference usersRef, accountsRef, transactionsRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        accountsRef = FirebaseDatabase.getInstance().getReference("accounts");
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        editTextSenderEmail = findViewById(R.id.editTextSenderEmail);
        editTextReceiverAccount = findViewById(R.id.editTextReceiverAccount);
        editTextAmount = findViewById(R.id.editTextAmount);
        buttonSendMoney = findViewById(R.id.buttonSendMoney);

        buttonSendMoney.setOnClickListener(v -> {
            String senderEmail = editTextSenderEmail.getText().toString().trim();
            String receiverAccount = editTextReceiverAccount.getText().toString().trim();
            String amountText = editTextAmount.getText().toString().trim();

            if (!senderEmail.isEmpty() && !receiverAccount.isEmpty() && !amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    processSendMoney(senderEmail, receiverAccount, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void processSendMoney(String senderEmail, String receiverAccount, double amount) {
        usersRef.orderByChild("email").equalTo(senderEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        if (userId != null) {
                            fetchSenderAccount(userId, receiverAccount, amount);
                        }
                    }
                } else {
                    Log.e(TAG, "No user found with this email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchSenderAccount(String userId, String receiverAccount, double amount) {
        accountsRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                        String accountId = accountSnapshot.getKey();
                        Double balance = accountSnapshot.child("balance").getValue(Double.class);

                        if (accountId != null && balance != null && balance >= amount) {
                            // Deduct the amount from sender's account
                            double newBalance = balance - amount;
                            accountsRef.child(accountId).child("balance").setValue(newBalance)
                                    .addOnSuccessListener(aVoid -> {
                                        // Fetch receiver account to credit the amount
                                        fetchReceiverAccount(receiverAccount, accountId, amount, newBalance);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update sender's balance: " + e.getMessage()));
                        } else {
                            Toast.makeText(SendMoney.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(TAG, "No account found for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchReceiverAccount(String receiverAccount, String senderAccountId, double amount, double senderNewBalance) {
        accountsRef.orderByChild("account_number").equalTo(receiverAccount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                        String accountId = accountSnapshot.getKey();
                        Double balance = accountSnapshot.child("balance").getValue(Double.class);

                        if (accountId != null && balance != null) {
                            // Credit the amount to receiver's account
                            double newBalance = balance + amount;
                            accountsRef.child(accountId).child("balance").setValue(newBalance)
                                    .addOnSuccessListener(aVoid -> {
                                        // Log the transaction for sender
                                        logTransaction(senderAccountId, amount, senderNewBalance, receiverAccount);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update receiver's balance: " + e.getMessage()));
                        } else {
                            Log.e(TAG, "Receiver account balance error");
                        }
                    }
                } else {
                    Log.e(TAG, "No receiver account found with this account number");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void logTransaction(String accountId, double amount, double newBalance, String receiverAccount) {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(accountId);
        String transactionKey = transactionsRef.push().getKey();
        if (transactionKey != null) {
            Model transaction = new Model();
            transaction.setType("send money");
            transaction.setAmount(amount);
            transaction.setDate("2024-05-01 10:00:00");  // This should be dynamically set to the current date and time
            transaction.setDescription("Send money operation");
            transaction.setReceiverAccountNumber(receiverAccount);

            transactionsRef.child(transactionKey).setValue(transaction)
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to log transaction: " + e.getMessage()));
        }
    }
}
