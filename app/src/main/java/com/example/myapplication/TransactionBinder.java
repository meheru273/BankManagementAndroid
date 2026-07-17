package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/** Binds a Transaction into the item.xml row layout (shared by the adapter and inline lists). */
final class TransactionBinder {

    private TransactionBinder() {}

    static void bind(Context ctx, View row, Transaction tx) {
        boolean in = tx.incoming;
        int fg = ContextCompat.getColor(ctx, in ? R.color.green_in : R.color.red_out);
        int bg = ContextCompat.getColor(ctx, in ? R.color.green_in_bg : R.color.red_out_bg);

        View box = row.findViewById(R.id.tx_icon_box);
        box.setBackground(Ui.rounded(bg, ctx, 14));

        ImageView icon = row.findViewById(R.id.tx_icon);
        icon.setImageResource(in ? R.drawable.ic_arrow_down_left : R.drawable.ic_arrow_up_right);
        icon.setColorFilter(fg);

        ((TextView) row.findViewById(R.id.tx_type)).setText(tx.type);
        ((TextView) row.findViewById(R.id.tx_sub))
                .setText(tx.title + " · " + tx.formattedDate());
        TextView amount = row.findViewById(R.id.tx_amount);
        amount.setText(MoneyUtils.formatSigned(tx.amount, in));
        amount.setTextColor(fg);
    }
}
