package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Binds local {@link Transaction} records to the transaction list rows. */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private final Context context;
    private final List<Transaction> list;

    public Adapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transaction tx = list.get(position);
        holder.type.setText(tx.type);
        holder.title.setText(tx.title);
        holder.date.setText(tx.formattedDate());
        holder.amount.setText(MoneyUtils.formatSigned(tx.amount, tx.incoming));
        int color = ContextCompat.getColor(context, tx.incoming ? R.color.income : R.color.expense);
        holder.amount.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type, title, date, amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.txType);
            title = itemView.findViewById(R.id.txTitle);
            date = itemView.findViewById(R.id.txDate);
            amount = itemView.findViewById(R.id.txAmount);
        }
    }
}
