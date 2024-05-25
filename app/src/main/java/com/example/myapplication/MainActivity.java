package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public CardView card1, card2, card3, card4, card5, card6;

    private FirebaseAuth mAuth;

    private TextView textViewAccountNumber;
    private TextView textViewEmail;
    private TextView textViewCoins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        textViewAccountNumber = findViewById(R.id.name);
        textViewEmail = findViewById(R.id.name2);
        textViewCoins = findViewById(R.id.coins);

        // Fetch and display user data
        fetchUserData("user1");


        NavigationManager navManager = NavigationManager.getInstance();

        card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            navManager.navigateToCashIn(this);
        });

        card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            navManager.navigateToCashOut(this);
        });

        card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            navManager.navigateToSendMoney(this);
        });

        card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> {
            navManager.navigateToSavings(this);
        });

        card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> {
            navManager.navigateToTransactions(this);
        });

        card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> {
            navManager.navigateToPayBill(this);
        });

//        ShowInfo("user1");
    }


    private void fetchUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String accountNumber = dataSnapshot.child("account_number").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    // Set the fetched user data to the TextViews
                    textViewAccountNumber.setText(accountNumber);
                    textViewEmail.setText(email);

                    // Assuming balance from multiple accounts needs to be summed up
                    long totalCoins = 0;
                    for (DataSnapshot accountSnapshot : dataSnapshot.child("accounts").getChildren()) {
                        Long balance = accountSnapshot.child("balance").getValue(Long.class);
                        if (balance != null) {
                            totalCoins += balance;
                        }
                    }
                    // Display the total coins
                    textViewCoins.setText(String.valueOf(totalCoins));
                } else {
                    Log.e("Firebase", "No user data found for user ID: " + userId);
                    textViewAccountNumber.setText("Account Number: N/A");
                    textViewEmail.setText("Email: N/A");
                    textViewCoins.setText("Total Coins: N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Database error", databaseError.toException());
                textViewAccountNumber.setText("Database Error");
                textViewEmail.setText("Database Error");
                textViewCoins.setText("Database Error");
            }
        });
    }


}
