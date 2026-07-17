package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CashIn extends AppCompatActivity {

    private FinancialServicesFacade facade;
    private TextInputLayout tilAmount;
    private TextInputEditText etAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        facade = new FinancialServicesFacade(this);
        ((TextView) findViewById(R.id.balance)).setText(MoneyUtils.format(facade.getBalance()));

        tilAmount = findViewById(R.id.tilAmount);
        etAmount = findViewById(R.id.etAmount);

        MaterialButton confirm = findViewById(R.id.btnConfirm);
        confirm.setOnClickListener(v -> {
            Double amount = InputUtils.readAmount(tilAmount, etAmount, getString(R.string.err_enter_amount));
            if (amount == null) return;

            FinancialServicesFacade.Result result = facade.cashIn(amount);
            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
            if (result.ok) finish();
        });
    }
}
