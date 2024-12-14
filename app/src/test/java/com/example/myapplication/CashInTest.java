package com.example.myapplication;

import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import junit.framework.TestCase;

import org.junit.Test;

public class CashInTest extends TestCase {

    @Test
    public void test_successful_cash_in() {
        // Setup
        CashIn activity = new CashIn();
        activity.onCreate(null);
        EditText editText = activity.findViewById(R.id.cashamount);
        editText.setText("100");
        Button cashInButton = activity.findViewById(R.id.button);

        // Act
        cashInButton.performClick();

        // Assert
        DatabaseReference accountRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER).child("total");
        accountRef.get().addOnSuccessListener(dataSnapshot -> {
            Double newBalance = dataSnapshot.getValue(Double.class);
            assertEquals(100.0, newBalance, 0.01);
        });
    }

    @Test
    public void test_non_numeric_input() {
        // Setup
        CashIn activity = new CashIn();
        activity.onCreate(null);
        EditText editText = activity.findViewById(R.id.cashamount);
        editText.setText("abc"); // Non-numeric input
        Button cashInButton = activity.findViewById(R.id.button);

        // Act
        cashInButton.performClick();

        // Assert
        DatabaseReference accountRef = DatabaseFactory.getDatabaseReference(DatabaseFactory.ReferenceType.USER).child("total");
        accountRef.get().addOnSuccessListener(dataSnapshot -> {
            assertNull(dataSnapshot.getValue(Double.class));
        });
    }
}