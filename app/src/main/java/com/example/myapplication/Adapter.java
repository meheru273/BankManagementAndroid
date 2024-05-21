package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    Context context;
    ArrayList<Model> list;

    public Adapter(Context context, ArrayList<Model> list) {
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
        Model model = list.get(position);
        holder.amount.setText(String.format(Locale.getDefault(), "%.2f", model.getAmount()));
        holder.date.setText(model.getDate());
        holder.receiver.setText(model.getReceiverAccountNumber() == null ? "N/A" : model.getReceiverAccountNumber());
        holder.type.setText(model.getType());
        holder.description.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, receiver, type, description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount1);
            date = itemView.findViewById(R.id.date1);
            receiver = itemView.findViewById(R.id.receiver1);
            type = itemView.findViewById(R.id.type1);
            description = itemView.findViewById(R.id.description1);
        }
    }
}
