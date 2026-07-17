package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/** Applies an Account's colors + data to item_account_row / item_account_card. */
final class AccountBinder {

    private AccountBinder() {}

    static void bindRow(Context ctx, View view, Account a) {
        common(ctx, view, a, 22f);
    }

    static void bindCard(Context ctx, View view, Account a) {
        common(ctx, view, a, 24f);
        text(view, R.id.acc_label, a.balanceLabel, a.textColor);
        ((ImageView) view.findViewById(R.id.acc_chevron)).setColorFilter(a.textColor);
    }

    private static void common(Context ctx, View view, Account a, float radius) {
        View root = view.findViewById(R.id.acc_root);
        if (a.bordered) {
            root.setBackground(Ui.roundedStroke(a.bgColor, ctx, radius, Color.parseColor("#ECE0E6"), 1.5f));
        } else {
            root.setBackground(Ui.rounded(a.bgColor, ctx, radius));
        }

        view.findViewById(R.id.acc_icon_box).setBackground(Ui.rounded(a.iconBgColor, ctx, 14));
        ImageView icon = view.findViewById(R.id.acc_icon);
        icon.setImageResource(a.iconRes);
        icon.setColorFilter(a.textColor);

        text(view, R.id.acc_name, a.name, a.textColor);
        text(view, R.id.acc_number, a.number, a.textColor);
        text(view, R.id.acc_balance, MoneyUtils.format(a.balance), a.textColor);
    }

    private static void text(View parent, int id, String value, int color) {
        TextView tv = parent.findViewById(id);
        tv.setText(value);
        tv.setTextColor(color);
    }
}
