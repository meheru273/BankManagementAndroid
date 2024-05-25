package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseFactory {

    // Private constructor to prevent instantiation
    private DatabaseFactory() {}

    // Enum to define the type of database reference needed
    public enum ReferenceType {
        ACCOUNTS,
        TRANSACTIONS,
        USER,
        DEPOSITE
    }


    // Factory method to get the database reference
    public static DatabaseReference getDatabaseReference(ReferenceType type) {

        String  userId="user1";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        switch (type) {
            case ACCOUNTS:
                return FirebaseDatabase.getInstance().getReference("users").child(userId).child("accounts");
            case TRANSACTIONS:
                return FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions");
            case USER:
                return FirebaseDatabase.getInstance().getReference("users").child(userId);
            case DEPOSITE:
                return FirebaseDatabase.getInstance().getReference("users").child(userId).child("deposits");
            default:
                throw new IllegalArgumentException("Invalid reference type");
        }
    }

}
