package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposits);

        textViewBalance = findViewById(R.id.textViewBalance);
        textViewInterestRate = findViewById(R.id.textViewInterestRate);



        String userId = "user1"; // Use dynamic ID in real scenarios
        if (userId != null) {
            fetchDepositInfo(userId);
        } else {
            Toast.makeText(this, "User ID is not available", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if there's no user ID
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets.consumeSystemWindowInsets();
        });
    }





    private void fetchDepositInfo(String userId) {
        DatabaseReference depositsRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.DEPOSITE);
        depositsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.child("balance").getValue(Double.class);
                    Double interestRate = dataSnapshot.child("interest_rate").getValue(Double.class);

                    // Logging to debug values
                    Log.d(TAG, "Balance retrieved: " + balance);
                    Log.d(TAG, "Interest Rate retrieved: " + interestRate);

                    // Display the balance and interest rate
                    textViewBalance.setText("Balance: " + (balance != null ? balance : "N/A"));
                    textViewInterestRate.setText("Interest Rate: " + (interestRate != null ? interestRate + "1.5%" : "N/A"));
                } else {
                    Log.e(TAG, "No deposit information found for this user");
                    textViewBalance.setText("Balance: N/A");
                    textViewInterestRate.setText("Interest Rate: N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                textViewBalance.setText("Database Error");
                textViewInterestRate.setText("Database Error");
            }
        });
    }

}
