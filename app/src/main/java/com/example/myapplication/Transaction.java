package com.example.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** A single local transaction record (Cash In, Send Money, Pay Bill, ...). */
public class Transaction {

    public final String type;        // e.g. "Cash In"
    public final String title;       // counterparty / detail, e.g. "To John" or "Wallet"
    public final double amount;      // always positive
    public final boolean incoming;   // true = money in, false = money out
    public final long timestamp;     // millis
    public final double balanceAfter;

    public Transaction(String type, String title, double amount, boolean incoming,
                       long timestamp, double balanceAfter) {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.incoming = incoming;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
    }

    public String formattedDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        return fmt.format(new Date(timestamp));
    }

    JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("type", type);
        o.put("title", title);
        o.put("amount", amount);
        o.put("incoming", incoming);
        o.put("timestamp", timestamp);
        o.put("balanceAfter", balanceAfter);
        return o;
    }

    static Transaction fromJson(JSONObject o) {
        return new Transaction(
                o.optString("type", ""),
                o.optString("title", ""),
                o.optDouble("amount", 0d),
                o.optBoolean("incoming", false),
                o.optLong("timestamp", 0L),
                o.optDouble("balanceAfter", 0d));
    }
}
