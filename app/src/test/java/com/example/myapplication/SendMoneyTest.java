package com.example.myapplication;

import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.testng.annotations.Test;

public class SendMoneyTest extends TestCase {

    @Test
    public void test_send_money_success() {
        // Arrange
        SendMoney activity = new SendMoney();
        activity.onCreate(null);
        EditText editTextAmount = activity.findViewById(R.id.sendAmount);
        EditText editTextReceiverAccountNumber = activity.findViewById(R.id.receiverAccountNumber);
        Button sendButton = activity.findViewById(R.id.sendButton);

        editTextAmount.setText("100");
        editTextReceiverAccountNumber.setText("1234567890");

        DatabaseReference mockDatabaseRef = Mockito.mock(DatabaseReference.class);
        Mockito.when(mockDatabaseRef.child(Mockito.anyString())).thenReturn(mockDatabaseRef);
        Mockito.when(mockDatabaseRef.get()).thenReturn(Tasks.forResult(Mockito.mock(DataSnapshot.class)));
        Mockito.when(mockDatabaseRef.setValue(Mockito.any())).thenReturn(Tasks.forResult(null));

        // Act
        sendButton.performClick();
    }
    @Test
    public void test_send_money_insufficient_balance() {
        // Arrange
        SendMoney activity = new SendMoney();
        activity.onCreate(null);
        EditText editTextAmount = activity.findViewById(R.id.sendAmount);
        EditText editTextReceiverAccountNumber = activity.findViewById(R.id.receiverAccountNumber);
        Button sendButton = activity.findViewById(R.id.sendButton);

        editTextAmount.setText("500");  // Assuming sender has less balance than the amount to send
        editTextReceiverAccountNumber.setText("1234567890");

        DatabaseReference mockDatabaseRef = Mockito.mock(DatabaseReference.class);
        Mockito.when(mockDatabaseRef.child(Mockito.anyString())).thenReturn(mockDatabaseRef);
        Mockito.when(mockDatabaseRef.get()).thenReturn(Tasks.forResult(Mockito.mock(DataSnapshot.class)));

        // Act
        sendButton.performClick();
    }
}