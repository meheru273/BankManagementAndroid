package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class BillsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        GridLayout grid = v.findViewById(R.id.bills_grid);
        List<DemoData.Biller> billers = DemoData.billers();
        for (DemoData.Biller b : billers) {
            View cell = getLayoutInflater().inflate(R.layout.item_biller, grid, false);
            ((ImageView) cell.findViewById(R.id.biller_icon)).setImageResource(b.iconRes);
            ((TextView) cell.findViewById(R.id.biller_name)).setText(b.name);
            cell.setOnClickListener(x -> startBill());

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            lp.setMargins(0, Ui.dp(requireContext(), 7), 0, Ui.dp(requireContext(), 7));
            cell.setLayoutParams(lp);
            grid.addView(cell);
        }

        v.findViewById(R.id.bill_pay_desco).setOnClickListener(x -> startBill());
        v.findViewById(R.id.bill_pay_link3).setOnClickListener(x -> startBill());
    }

    private void startBill() {
        Intent i = new Intent(requireContext(), TransactActivity.class);
        i.putExtra(TransactActivity.EXTRA_FLOW, "bill");
        startActivity(i);
    }
}
