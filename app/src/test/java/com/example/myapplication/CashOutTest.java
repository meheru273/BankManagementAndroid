package com.example.myapplication;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class CashOutTest {

    @Mock
    DatabaseReference mockDatabaseReference;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Initialize Firebase
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testLogTransaction_Success() {
        // Mock DatabaseReference
        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
        Mockito.when(mockedFirebaseDatabase.getReference("transactions").child(Mockito.anyString())).thenReturn(mockDatabaseReference);

        // Test data
        String accountId = "account1";
        double amount = 100.0;

        // Create CashOut instance
        CashOut cashOut = new CashOut();

        // Invoke logTransaction method
        cashOut.logTransaction(accountId, amount);

        // Verify that setValue method is called on DatabaseReference
        Mockito.verify(mockDatabaseReference, Mockito.times(1)).setValue(Mockito.any());
    }
}
