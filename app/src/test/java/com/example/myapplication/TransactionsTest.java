package com.example.myapplication;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}) // Specify a suitable SDK version
public class TransactionsTest {

    @Mock
    FirebaseDatabase mockedDatabase;
    @Mock
    DatabaseReference mockedDatabaseReference;
    @Mock
    DataSnapshot dataSnapshot;

    private Transactions transactions;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<Model> list;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockedDatabase.getReference("transaction")).thenReturn(mockedDatabaseReference);

        // Initialize the activity using Robolectric
        transactions = Robolectric.buildActivity(Transactions.class).create().get();

        // Initialize the list and adapter
        list = new ArrayList<>();
        adapter = new Adapter(transactions, list);

        // Initialize the RecyclerView
        recyclerView = new RecyclerView(transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(transactions));
        recyclerView.setAdapter(adapter);

        // Set the mock database and adapter in the transactions instance
        transactions.database = mockedDatabaseReference;
        transactions.recyclerView = recyclerView;
        transactions.adapter = adapter;
        transactions.list = list;
    }

    @Test
    public void fetchDataFromFirebase_Test() {
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            // Simulate onDataChange with mocked DataSnapshot
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(mockedDatabaseReference).addValueEventListener(any(ValueEventListener.class));

        // Assume dataSnapshot returns a list of Models
        when(dataSnapshot.getChildren()).thenReturn(someIterableOfModels());
        when(dataSnapshot.getValue(Model.class)).thenReturn(new Model());

        transactions.fetchDataFromFirebase();

        // Verify list was updated
        verify(transactions.adapter).notifyDataSetChanged();
    }

    private Iterable<DataSnapshot> someIterableOfModels() {
        // Create mocks or dummy DataSnapshots for testing
        ArrayList<DataSnapshot> snapshots = new ArrayList<>();
        snapshots.add(dataSnapshot);
        return snapshots;
    }
}
