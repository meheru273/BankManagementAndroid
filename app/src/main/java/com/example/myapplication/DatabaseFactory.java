package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseFactory {

    // Private constructor to prevent instantiation
    private DatabaseFactory() {}

    // Enum to define the type of database reference needed
    public enum ReferenceType {
        ACCOUNTS,
        TRANSACTIONS
    }

    // Factory method to get the database reference
    public static DatabaseReference getDatabaseReference(ReferenceType type) {
        String email = "jhon@gmail.com";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        switch (type) {
            case ACCOUNTS:
                return database.getReference("accounts");
            case TRANSACTIONS:
                return database.getReference("transactions");
            default:
                throw new IllegalArgumentException("Invalid reference type");
        }
    }

}
