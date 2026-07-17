package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class AccountsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        List<Account> accounts = DemoData.accounts();

        ((TextView) v.findViewById(R.id.accounts_subtitle))
                .setText(accounts.size() + " accounts · net worth " + MoneyUtils.format(DemoData.netWorth()));

        LinearLayout box = v.findViewById(R.id.accounts_box);
        for (int i = 0; i < accounts.size(); i++) {
            View card = getLayoutInflater().inflate(R.layout.item_account_card, box, false);
            AccountBinder.bindCard(requireContext(), card, accounts.get(i));
            final int index = i;
            card.setOnClickListener(x -> {
                Intent it = new Intent(requireContext(), AccountDetailActivity.class);
                it.putExtra(AccountDetailActivity.EXTRA_INDEX, index);
                startActivity(it);
            });
            box.addView(card);
        }
    }
}
