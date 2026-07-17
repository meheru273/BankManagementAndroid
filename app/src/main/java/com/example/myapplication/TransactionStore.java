package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Local, offline transaction history persisted as a JSON array in SharedPreferences. */
public class TransactionStore {

    private static final String PREFS = "bankmgmt_tx";
    private static final String KEY_LIST = "tx_list";

    private final SharedPreferences prefs;

    public TransactionStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Adds a record to the front of the history (newest first). */
    public void add(Transaction tx) {
        try {
            JSONArray arr = readArray();
            JSONArray updated = new JSONArray();
            updated.put(tx.toJson());
            for (int i = 0; i < arr.length(); i++) {
                updated.put(arr.getJSONObject(i));
            }
            prefs.edit().putString(KEY_LIST, updated.toString()).apply();
        } catch (JSONException ignored) {
            // A corrupt entry should never crash the app; skip persisting it.
        }
    }

    public List<Transaction> getAll() {
        List<Transaction> out = new ArrayList<>();
        JSONArray arr = readArray();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o != null) {
                out.add(Transaction.fromJson(o));
            }
        }
        return out;
    }

    private JSONArray readArray() {
        String raw = prefs.getString(KEY_LIST, "[]");
        try {
            return new JSONArray(raw);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}
