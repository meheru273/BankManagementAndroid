package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AccountDetailActivity extends AppCompatActivity {

    public static final String EXTRA_INDEX = "account_index";

    private FinancialServicesFacade facade;
    private ValueEventListener txListener;
    private LinearLayout txList;
    private TextView txEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        List<Account> accounts = DemoData.accounts();
        if (index < 0 || index >= accounts.size()) index = 0;
        Account a = accounts.get(index);

        facade = new FinancialServicesFacade();
        txList = findViewById(R.id.detail_tx_list);
        txEmpty = findViewById(R.id.detail_tx_empty);

        findViewById(R.id.detail_back).setOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.detail_name)).setText(a.name);

        // Colored balance card (color comes from the account)
        LinearLayout card = findViewById(R.id.detail_card);
        card.setBackground(a.bordered
                ? Ui.roundedStroke(a.bgColor, this, 26, android.graphics.Color.parseColor("#ECE0E6"), 1.5f)
                : Ui.rounded(a.bgColor, this, 26));
        setText(R.id.detail_label, a.balanceLabel, a.textColor);
        setText(R.id.detail_balance, MoneyUtils.format(a.balance), a.textColor);
        setText(R.id.detail_number, a.number, a.textColor);

        findViewById(R.id.detail_cashin).setOnClickListener(v -> startTransact("cashin"));
        findViewById(R.id.detail_cashout).setOnClickListener(v -> startTransact("cashout"));
        // Statement is a placeholder for now.
    }

    private void startTransact(String flow) {
        Intent i = new Intent(this, TransactActivity.class);
        i.putExtra(TransactActivity.EXTRA_FLOW, flow);
        startActivity(i);
    }

    private void setText(int id, String value, int color) {
        TextView tv = findViewById(id);
        tv.setText(value);
        tv.setTextColor(color);
    }

    @Override
    protected void onStart() {
        super.onStart();
        txListener = facade.observeTransactions(new FinancialServicesFacade.TxListCallback() {
            @Override
            public void onList(List<Transaction> transactions) {
                render(transactions);
            }

            @Override
            public void onError(String message) {
                render(java.util.Collections.emptyList());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        facade.removeTxObserver(txListener);
    }

    private void render(List<Transaction> all) {
        txList.removeAllViews();
        int count = Math.min(8, all.size());
        txEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        for (int i = 0; i < count; i++) {
            View row = getLayoutInflater().inflate(R.layout.item, txList, false);
            TransactionBinder.bind(this, row, all.get(i));
            txList.addView(row);
            if (i < count - 1) {
                View d = new View(this);
                d.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, Ui.dp(this, 1)));
                d.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
                txList.addView(d);
            }
        }
    }
}
