package com.example.myapplication;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A single transaction record. Public no-arg constructor + public fields so
 * Firebase can serialize it with setValue(...) and deserialize it with
 * getValue(Transaction.class).
 */
@IgnoreExtraProperties
public class Transaction {

    public String type;        // e.g. "Cash In"
    public String title;       // counterparty / detail, e.g. "To John"
    public double amount;      // always positive
    public boolean incoming;   // true = money in, false = money out
    public long timestamp;     // millis
    public double balanceAfter;

    /** Required by Firebase for deserialization. */
    public Transaction() {}

    public Transaction(String type, String title, double amount, boolean incoming,
                       long timestamp, double balanceAfter) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.incoming = incoming;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
    }

    @Exclude
    public String formattedDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        return fmt.format(new Date(timestamp));
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("type", type);
        m.put("title", title);
        m.put("amount", amount);
        m.put("incoming", incoming);
        m.put("timestamp", timestamp);
        m.put("balanceAfter", balanceAfter);
        return m;
    }
}
