package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<Model> list;
    private static final String TAG = "TransactionsActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.tran);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        // Fetch the user ID dynamically or use a static ID
        String userId = "user1"; // Use dynamic ID in real scenarios
        if (userId != null) {
            fetchTransactions(userId);
        } else {
            Toast.makeText(this, "User ID is not available", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if there's no user ID
        }
    }

    @SuppressLint("RestrictedApi")
    private void fetchTransactions(String userId) {
        // Get the correct Firebase database reference
        DatabaseReference transactionsRef =  DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.TRANSACTIONS);
        Log.d(TAG, "Connection made to: " + transactionsRef.getPath().toString());
        Toast.makeText(this, "Connection made to: " + transactionsRef.getPath().toString(), Toast.LENGTH_SHORT).show();

        // Listen for a single snapshot of the data at this location
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // Clear the existing data in the list

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Snapshot: " + snapshot.toString()); // Log each snapshot for debugging
                        Model model = snapshot.getValue(Model.class);
                        if (model != null) {
                            list.add(model);
                            Log.d(TAG, "Model added: " + model.getDescription());
                        }
                    }
                    adapter.notifyDataSetChanged();  // Notify the adapter to update the view

                } else {

                    Toast.makeText(Transactions.this, "No transactions found for this account.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error in transactions fetch: " + error.getMessage());
                Toast.makeText(Transactions.this, "Failed to load transactions. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
