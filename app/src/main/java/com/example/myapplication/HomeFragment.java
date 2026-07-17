package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeFragment extends Fragment {

    private FinancialServicesFacade facade;
    private ValueEventListener userListener, txListener;
    private TextView balanceView, nameView, initialsView, txEmpty;
    private ImageView eyeIcon;
    private LinearLayout accountsBox, txList;

    private boolean balanceHidden = false;
    private double balance = 0d;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        facade = new FinancialServicesFacade();

        balanceView = v.findViewById(R.id.home_balance);
        nameView = v.findViewById(R.id.home_name);
        initialsView = v.findViewById(R.id.home_initials);
        eyeIcon = v.findViewById(R.id.home_eye);
        accountsBox = v.findViewById(R.id.home_accounts);
        txList = v.findViewById(R.id.home_tx_list);
        txEmpty = v.findViewById(R.id.home_tx_empty);

        v.findViewById(R.id.qa_cash_in).setOnClickListener(x -> startTransact("cashin"));
        v.findViewById(R.id.qa_cash_out).setOnClickListener(x -> startTransact("cashout"));
        v.findViewById(R.id.qa_transfer).setOnClickListener(x -> startTransact("transfer"));
        v.findViewById(R.id.qa_bills).setOnClickListener(x -> ((MainActivity) requireActivity()).showBills());
        v.findViewById(R.id.home_view_all).setOnClickListener(x -> ((MainActivity) requireActivity()).showAccounts());
        v.findViewById(R.id.home_eye_btn).setOnClickListener(x -> {
            balanceHidden = !balanceHidden;
            renderBalance();
        });

        buildAccounts();
    }

    private void startTransact(String flow) {
        Intent i = new Intent(requireContext(), TransactActivity.class);
        i.putExtra(TransactActivity.EXTRA_FLOW, flow);
        startActivity(i);
    }

    private void buildAccounts() {
        accountsBox.removeAllViews();
        List<Account> accounts = DemoData.accounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            View row = getLayoutInflater().inflate(R.layout.item_account_row, accountsBox, false);
            AccountBinder.bindRow(requireContext(), row, a);
            final int index = i;
            row.setOnClickListener(x -> {
                Intent it = new Intent(requireContext(), AccountDetailActivity.class);
                it.putExtra(AccountDetailActivity.EXTRA_INDEX, index);
                startActivity(it);
            });
            accountsBox.addView(row);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        userListener = facade.observeUser((name, bal) -> {
            balance = bal;
            nameView.setText(name);
            initialsView.setText(initials(name));
            renderBalance();
        });
        txListener = facade.observeTransactions(new FinancialServicesFacade.TxListCallback() {
            @Override
            public void onList(List<Transaction> transactions) {
                renderTransactions(transactions);
            }

            @Override
            public void onError(String message) {
                renderTransactions(java.util.Collections.emptyList());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        facade.removeObserver(userListener);
        facade.removeTxObserver(txListener);
    }

    private void renderBalance() {
        balanceView.setText(balanceHidden ? MoneyUtils.CURRENCY + " • • • • •" : MoneyUtils.format(balance));
        eyeIcon.setImageResource(balanceHidden ? R.drawable.ic_eye_off : R.drawable.ic_eye);
    }

    private void renderTransactions(List<Transaction> all) {
        txList.removeAllViews();
        int count = Math.min(4, all.size());
        txEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        for (int i = 0; i < count; i++) {
            View row = getLayoutInflater().inflate(R.layout.item, txList, false);
            TransactionBinder.bind(requireContext(), row, all.get(i));
            txList.addView(row);
            if (i < count - 1) txList.addView(divider());
        }
    }

    private View divider() {
        View d = new View(requireContext());
        d.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(requireContext(), 1)));
        d.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider));
        return d;
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        String s = parts[0].substring(0, 1);
        if (parts.length > 1) s += parts[parts.length - 1].substring(0, 1);
        return s.toUpperCase();
    }
}
