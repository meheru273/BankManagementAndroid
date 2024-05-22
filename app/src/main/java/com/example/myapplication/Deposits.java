package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Deposits extends AppCompatActivity {

    private static final String TAG = "Deposits";
    private TextView textViewBalance;
    private TextView textViewInterestRate;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposits);

        textViewBalance = findViewById(R.id.textViewBalance);
        textViewInterestRate = findViewById(R.id.textViewInterestRate);

        // Assume the email is passed to this activity or retrieved from FirebaseAuth
        String email = "jane.smith@example.com";  // Replace with the actual email from the logged-in user
        getUserIdFromEmail(email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getUserIdFromEmail(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        if (userId != null) {
                            // Now fetch the deposit information using this userId
                            fetchDepositInfo(userId);
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

    private void fetchDepositInfo(String userId) {
        DatabaseReference depositsRef = FirebaseDatabase.getInstance().getReference("deposits").child(userId);
        depositsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.child("balance").getValue(Double.class);
                    Double interestRate = dataSnapshot.child("interest_rate").getValue(Double.class);

                    // Display the balance and interest rate
                    textViewBalance.setText("Balance: " + (balance != null ? balance : "N/A"));
                    textViewInterestRate.setText("Interest Rate: " + (interestRate != null ? interestRate + "%" : "N/A"));
                } else {
                    Log.e(TAG, "No deposit information found for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }
}
