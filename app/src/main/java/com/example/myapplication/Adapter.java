package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        holder.amount.setText(String.valueOf(model.getAmount()));
        holder.date.setText(model.getDate());
        holder.reciever.setText(model.getReciever());
        holder.savings.setText(String.valueOf(model.getSavings()));
        holder.type.setText(model.getType());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, reciever, savings, type;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount1);
            date = itemView.findViewById(R.id.date1);
            reciever = itemView.findViewById(R.id.reciever1);
            savings = itemView.findViewById(R.id.savings1);
            type = itemView.findViewById(R.id.type1);
        }
    }
}

