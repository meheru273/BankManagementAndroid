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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    Adapter adapter;
    public ArrayList<Model> list;
    private static final String TAG = "TransactionsActivity";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerView = findViewById(R.id.tran);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        // Example account ID passed from MainActivity, should be dynamically obtained based on your application logic
        String accountId = "account1";
        if (accountId != null) {
            fetchTransactions(accountId);
        } else {
            Toast.makeText(this, "Account ID is not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchTransactions(String accountId) {
       //DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(accountId);
        // Initialize Firebase Database reference to the accounts node
        DatabaseReference transactionsRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.TRANSACTIONS).child(accountId);


        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot transSnapshot : dataSnapshot.getChildren()) {
                    Model model = transSnapshot.getValue(Model.class);
                    if (model != null) {
                        list.add(model);
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error in transactions fetch: " + error.getMessage());
            }
        });
    }
}
