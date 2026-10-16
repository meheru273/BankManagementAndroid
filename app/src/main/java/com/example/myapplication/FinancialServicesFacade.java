package com.example.myapplication;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade over the wallet's Firebase-backed operations. Identity comes from
 * Firebase Auth; balance, savings and transaction history live under
 * {@code users/$uid} in the Realtime Database.
 *
 * Every method is asynchronous (network I/O) and reports back on a callback.
 */
public class FinancialServicesFacade {

    public interface OpCallback {
        void onResult(boolean ok, String message);
    }

    public interface UserCallback {
        void onUser(String name, double balance);
    }

    public interface StringCallback {
        void onValue(String value); // value may be null
    }

    public interface BoolCallback {
        void onResult(boolean value);
    }

    private interface BalanceHandler {
        void onBalance(double balance);
    }

    private static final double DEFAULT_BALANCE = 4500d;

    private final DatabaseReference userRef; // null if not signed in

    public FinancialServicesFacade() {
        DatabaseReference ref = null;
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            }
        } catch (Exception e) {
            // No Realtime Database URL configured yet (google-services.json has no
            // firebase_url because the database hasn't been created). Degrade to a
            // clear error instead of crashing.
            ref = null;
        }
        userRef = ref;
    }

    // --- Profile ---------------------------------------------------------

    /** One-time KYC profile creation right after phone verification. */
    public void createProfile(Profile p, String pinHash, OpCallback cb) {
        if (userRef == null) {
            cb.onResult(false, "Database unavailable. Check your Firebase setup.");
            return;
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", p.name);
        profile.put("phone", p.phone);
        profile.put("nid", p.nid);
        profile.put("dob", p.dob);
        profile.put("address", p.address);
        profile.put("bankAccount", p.bankAccount);
        profile.put("pinHash", pinHash);
        profile.put("balance", DEFAULT_BALANCE);
        profile.put("savings", 0d);
        userRef.updateChildren(profile).addOnCompleteListener(task ->
                cb.onResult(task.isSuccessful(),
                        task.isSuccessful() ? "Account ready" : "Could not set up your account"));
    }

    /** True if this signed-in user already has a profile (returning user). */
    public void checkProfileExists(BoolCallback cb) {
        if (userRef == null) {
            cb.onResult(false);
            return;
        }
        userRef.child("name").get().addOnCompleteListener(task ->
                cb.onResult(task.isSuccessful()
                        && task.getResult() != null
                        && task.getResult().getValue() != null));
    }

    public void loadPinHash(StringCallback cb) {
        if (userRef == null) {
            cb.onValue(null);
            return;
        }
        userRef.child("pinHash").get().addOnCompleteListener(task ->
                cb.onValue(task.isSuccessful() && task.getResult() != null
                        ? task.getResult().getValue(String.class) : null));
    }

    /** Live-updating name + balance for the dashboard. Returns the listener so it can be removed. */
    public ValueEventListener observeUser(UserCallback cb) {
        if (userRef == null) return null;
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                cb.onUser(name == null ? "User" : name, num(snapshot.child("balance")));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onUser("User", 0d);
            }
        };
        userRef.addValueEventListener(listener);
        return listener;
    }

    public void removeObserver(ValueEventListener listener) {
        if (userRef != null && listener != null) userRef.removeEventListener(listener);
    }

    public interface TxListCallback {
        void onList(List<Transaction> transactions);

        void onError(String message);
    }

    /** Live-updating transaction history, newest first. Returns the listener so it can be removed. */
    public ValueEventListener observeTransactions(TxListCallback cb) {
        if (userRef == null) {
            cb.onError("Not signed in");
            return null;
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction tx = child.getValue(Transaction.class);
                    if (tx != null) list.add(tx);
                }
                Collections.sort(list, (a, b) -> Long.compare(b.timestamp, a.timestamp));
                cb.onList(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onError(error.getMessage());
            }
        };
        userRef.child("transactions").addValueEventListener(listener);
        return listener;
    }

    public void removeTxObserver(ValueEventListener listener) {
        if (userRef != null && listener != null) {
            userRef.child("transactions").removeEventListener(listener);
        }
    }

    // --- Operations ------------------------------------------------------

    public void loadBalance(BalanceCallbackPublic cb) {
        if (userRef == null) {
            cb.onResult(false, 0d);
            return;
        }
        userRef.child("balance").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cb.onResult(true, num(task.getResult()));
            } else {
                cb.onResult(false, 0d);
            }
        });
    }

    public interface BalanceCallbackPublic {
        void onResult(boolean ok, double balance);
    }

    public void cashIn(double amount, OpCallback cb) {
        if (amount <= 0) {
            cb.onResult(false, "Amount must be greater than 0");
            return;
        }
        readBalance(cb, balance -> {
            double nb = balance + amount;
            applyUpdate(nb, null, "Cash In", "Added to wallet", amount, true,
                    "Cashed in " + MoneyUtils.format(amount) + ". Balance: " + MoneyUtils.format(nb), cb);
        });
    }

    public void cashOut(double amount, OpCallback cb) {
        outgoing(amount, "Cash Out", "Withdrawn from wallet", cb);
    }

    public void sendMoney(String recipient, double amount, OpCallback cb) {
        if (recipient == null || recipient.trim().isEmpty()) {
            cb.onResult(false, "Please enter a recipient");
            return;
        }
        outgoing(amount, "Send Money", "To " + recipient.trim(), cb);
    }

    public void payBill(String biller, double amount, OpCallback cb) {
        if (biller == null || biller.trim().isEmpty()) {
            cb.onResult(false, "Please enter a biller");
            return;
        }
        outgoing(amount, "Pay Bill", biller.trim(), cb);
    }

    public void addToSavings(double amount, OpCallback cb) {
        if (userRef == null) {
            cb.onResult(false, "Database unavailable. Check your Firebase setup.");
            return;
        }
        if (amount <= 0) {
            cb.onResult(false, "Amount must be greater than 0");
            return;
        }
        userRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                cb.onResult(false, "Couldn't reach the server. Check your connection.");
                return;
            }
            DataSnapshot snap = task.getResult();
            double balance = num(snap.child("balance"));
            double savings = num(snap.child("savings"));
            if (amount > balance) {
                cb.onResult(false, "Insufficient balance");
                return;
            }
            double nb = balance - amount;
            double ns = savings + amount;
            applyUpdate(nb, ns, "Savings", "Moved to savings", amount, false,
                    "Moved " + MoneyUtils.format(amount) + " to savings. Savings: " + MoneyUtils.format(ns), cb);
        });
    }

    // --- internals -------------------------------------------------------

    private void outgoing(double amount, String type, String title, OpCallback cb) {
        if (amount <= 0) {
            cb.onResult(false, "Amount must be greater than 0");
            return;
        }
        readBalance(cb, balance -> {
            if (amount > balance) {
                cb.onResult(false, "Insufficient balance");
                return;
            }
            double nb = balance - amount;
            applyUpdate(nb, null, type, title, amount, false,
                    type + " of " + MoneyUtils.format(amount) + " done. Balance: " + MoneyUtils.format(nb), cb);
        });
    }

    private void readBalance(OpCallback cb, BalanceHandler handler) {
        if (userRef == null) {
            cb.onResult(false, "Database unavailable. Check your Firebase setup.");
            return;
        }
        userRef.child("balance").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                cb.onResult(false, "Couldn't reach the server. Check your connection.");
                return;
            }
            handler.onBalance(num(task.getResult()));
        });
    }

    private void applyUpdate(double newBalance, Double newSavings, String type, String title,
                             double amount, boolean incoming, String successMsg, OpCallback cb) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("balance", newBalance);
        if (newSavings != null) updates.put("savings", newSavings);
        String key = userRef.child("transactions").push().getKey();
        Transaction tx = new Transaction(type, title, amount, incoming, System.currentTimeMillis(), newBalance);
        updates.put("transactions/" + key, tx.toMap());
        userRef.updateChildren(updates).addOnCompleteListener(task ->
                cb.onResult(task.isSuccessful(),
                        task.isSuccessful() ? successMsg : "Transaction failed. Please try again."));
    }

    private static double num(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        return value instanceof Number ? ((Number) value).doubleValue() : 0d;
    }
}
