package com.example.myapplication;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Build;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class DepositsTest {

    private Deposits depositsActivity;

    @Before
    public void setup() {
        depositsActivity = new Deposits();
    }

    @Test
    public void testRetrieveDepositDetails() {
        // Mock Firebase objects
        FirebaseDatabase mockFirebaseDatabase = mock(FirebaseDatabase.class);
        DatabaseReference mockDatabaseReference = mock(DatabaseReference.class);
        DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);

        // Set up mock behavior for successful deposit details retrieval
        when(mockFirebaseDatabase.getReference("deposits")).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.child("userId123")).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.addListenerForSingleValueEvent(any(ValueEventListener.class))).thenAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(mockDataSnapshot);
            return null;
        });
        when(mockDataSnapshot.exists()).thenReturn(true);
        when(mockDataSnapshot.child("amount").getValue()).thenReturn(100.0);

        // Call the method under test
        double depositAmount = depositsActivity.fetchDepositInfo("userId123");

        // Verify that the correct deposit amount was retrieved
        assertEquals(100.0, depositAmount, 0.001);
    }
    // Add more test cases as needed
}