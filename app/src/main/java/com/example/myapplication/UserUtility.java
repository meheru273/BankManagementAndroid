package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserUtility {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    String email = "john@gmail.com";

    // Interface for callbacks
    public interface DataCallback {
        void onDataRetrieved(String userId); // Passes userId to another class
        void onDataNotFound();
    }

    // Method to fetch user ID by email from the database
    public void getUserIdByEmail(DataCallback callback) {
        mDatabase.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                Log.d(TAG, "User ID: " + userId);
                                callback.onDataRetrieved(userId); // Pass userId to callback
                                return;
                            }
                        } else {
                            Log.d(TAG, "No user found with email: " + email);
                            callback.onDataNotFound();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUserByEmail:onCancelled", databaseError.toException());
                        callback.onDataNotFound();
                    }
                });
    }
}
