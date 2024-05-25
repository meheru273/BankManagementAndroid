package com.example.myapplication;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    private FirebaseDatabase mockedFirebaseDatabase;
    @Mock
    private DatabaseReference mockedDatabaseReference;
    @Mock
    private DataSnapshot mockedDataSnapshot;
    @Mock
    private DatabaseError mockedDatabaseError;

    @Before
    public void setUp() {
        when(mockedFirebaseDatabase.getReference(anyString())).thenReturn(mockedDatabaseReference);
    }

    @Test
    public void fetchUserData_userExists_updatesUI() {
        // Prepare data
        when(mockedDataSnapshot.exists()).thenReturn(true);
        when(mockedDataSnapshot.child("account_number").getValue(String.class)).thenReturn("1234567890");
        when(mockedDataSnapshot.child("email").getValue(String.class)).thenReturn("test@example.com");
        when(mockedDataSnapshot.child("accounts").child("balance").getValue(Long.class)).thenReturn(5000L);

        // Execute
        ValueEventListener valueEventListener = captureEventListener();
        valueEventListener.onDataChange(mockedDataSnapshot);

        // Verify
        verify(mockedDatabaseReference).addListenerForSingleValueEvent(valueEventListener);
        // Assertions to check if TextViews are updated as expected would go here.
        // For example:
        // assertEquals("1234567890", textViewAccountNumber.getText().toString());
    }

    @Test
    public void fetchUserData_userDoesNotExist_showsError() {
        // Prepare data
        when(mockedDataSnapshot.exists()).thenReturn(false);

        // Execute
        ValueEventListener valueEventListener = captureEventListener();
        valueEventListener.onDataChange(mockedDataSnapshot);

        // Verify
        verify(mockedDatabaseReference).addListenerForSingleValueEvent(valueEventListener);
        // Check if error message is displayed
        // assertEquals("No user data found", textViewError.getText().toString());
    }

    private ValueEventListener captureEventListener() {
        // Capture the ValueEventListener added to the DatabaseReference
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            return listener;
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
        return any(ValueEventListener.class);
    }
}
