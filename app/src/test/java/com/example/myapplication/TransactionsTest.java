package com.example.myapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class TransactionsTest {

    @Mock
    private DatabaseReference mockDatabaseReference;
    @Mock
    private DataSnapshot mockDataSnapshot;
    @Mock
    private FirebaseDatabase mockFirebaseDatabase;
    @Mock
    private RecyclerView.Adapter adapter;

    private Transactions transactionsActivity;
    private List<DataSnapshot> mockDataSnapshots;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the mock DataSnapshots
        mockDataSnapshots = new ArrayList<>();
        DataSnapshot mockSnapshot1 = Mockito.mock(DataSnapshot.class);
        DataSnapshot mockSnapshot2 = Mockito.mock(DataSnapshot.class);

        when(mockSnapshot1.getValue(String.class)).thenReturn("{\"type\":\"deposit\",\"amount\":500.00,\"date\":\"2024-05-01 10:00:00\",\"description\":\"Deposit cash\",\"receiver_account_number\":null}");
        when(mockSnapshot2.getValue(String.class)).thenReturn("{\"type\":\"withdrawal\",\"amount\":200.00,\"date\":\"2024-05-02 11:00:00\",\"description\":\"ATM withdrawal\",\"receiver_account_number\":null}");

        mockDataSnapshots.add(mockSnapshot1);
        mockDataSnapshots.add(mockSnapshot2);

        // Mock the FirebaseDatabase instance and its methods
        when(mockFirebaseDatabase.getReference()).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.child(anyString())).thenReturn(mockDatabaseReference);

        // Create the Activity with Robolectric
        transactionsActivity = Robolectric.buildActivity(Transactions.class)
                .create()
                .start()
                .resume()
                .get();
        injectMockFirebaseDatabase(transactionsActivity, mockFirebaseDatabase);
    }

    private void injectMockFirebaseDatabase(Transactions activity, FirebaseDatabase mockFirebaseDatabase) {
        try {
            java.lang.reflect.Field databaseField = Transactions.class.getDeclaredField("database");
            databaseField.setAccessible(true);
            databaseField.set(activity, mockFirebaseDatabase);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOnCreate_initializesRecyclerView() {
        RecyclerView recyclerView = transactionsActivity.findViewById(R.id.tran);
        assertNotNull(recyclerView);
        assertNotNull(recyclerView.getAdapter());
    }

    @Test
    public void testFetchTransactions_successful() {
        ArgumentCaptor<ValueEventListener> argumentCaptor = ArgumentCaptor.forClass(ValueEventListener.class);

        transactionsActivity.fetchTransactions("account1");

        verify(mockDatabaseReference).addListenerForSingleValueEvent(argumentCaptor.capture());

        ValueEventListener listener = argumentCaptor.getValue();
        when(mockDataSnapshot.getChildren()).thenReturn(mockDataSnapshots);
        listener.onDataChange(mockDataSnapshot);

        assertEquals(2, transactionsActivity.list.size());
        assertEquals("{\"type\":\"deposit\",\"amount\":500.00,\"date\":\"2024-05-01 10:00:00\",\"description\":\"Deposit cash\",\"receiver_account_number\":null}", transactionsActivity.list.get(0).toString());
        assertEquals("{\"type\":\"withdrawal\",\"amount\":200.00,\"date\":\"2024-05-02 11:00:00\",\"description\":\"ATM withdrawal\",\"receiver_account_number\":null}", transactionsActivity.list.get(1).toString());
    }

    @Test
    public void testFetchTransactions_failure() {
        ArgumentCaptor<ValueEventListener> argumentCaptor = ArgumentCaptor.forClass(ValueEventListener.class);

        transactionsActivity.fetchTransactions("account1");

        verify(mockDatabaseReference).addListenerForSingleValueEvent(argumentCaptor.capture());

        ValueEventListener listener = argumentCaptor.getValue();
        listener.onCancelled(DatabaseError.fromException(new Exception("Database error")));

        assertEquals(0, transactionsActivity.list.size());
    }

    // Handling null account ID when fetching transactions
    @Test
    public void testFetchTransactions_nullAccountId() {
        transactionsActivity.fetchTransactions(null);

        verify(mockDatabaseReference, never()).addListenerForSingleValueEvent(any());
        assertEquals(0, transactionsActivity.list.size());
    }

    // Handling empty data snapshot when fetching transactions
    @Test
    public void testFetchTransactions_emptyDataSnapshot() {
        ArgumentCaptor<ValueEventListener> argumentCaptor = ArgumentCaptor.forClass(ValueEventListener.class);

        transactionsActivity.fetchTransactions("account1");

        verify(mockDatabaseReference).addListenerForSingleValueEvent(argumentCaptor.capture());

        ValueEventListener listener = argumentCaptor.getValue();
        when(mockDataSnapshot.getChildren()).thenReturn(Collections.emptyList());
        listener.onDataChange(mockDataSnapshot);

        assertEquals(0, transactionsActivity.list.size());
    }

    // Handling a null DataSnapshot in onDataChange
    @Test
    public void testNullDataSnapshotInOnDataChange() {
        ArgumentCaptor<ValueEventListener> argumentCaptor = ArgumentCaptor.forClass(ValueEventListener.class);

        transactionsActivity.fetchTransactions("account1");

        verify(mockDatabaseReference).addListenerForSingleValueEvent(argumentCaptor.capture());

        ValueEventListener listener = argumentCaptor.getValue();
        listener.onDataChange(null);

        assertEquals(0, transactionsActivity.list.size());
    }

    // Testing if notifyDataSetChanged is called after fetching transactions
    @Test
    public void testNotifyDataSetChangedCalledAfterFetchingTransactions() {
        ArgumentCaptor<ValueEventListener> argumentCaptor = ArgumentCaptor.forClass(ValueEventListener.class);

        transactionsActivity.fetchTransactions("account1");

        verify(mockDatabaseReference).addListenerForSingleValueEvent(argumentCaptor.capture());

        ValueEventListener listener = argumentCaptor.getValue();
        when(mockDataSnapshot.getChildren()).thenReturn(mockDataSnapshots);
        listener.onDataChange(mockDataSnapshot);

        verify(adapter).notifyDataSetChanged();
    }

    @After
    public void tearDown() {
        // No need to close static mocks since we are not using them
    }
}
