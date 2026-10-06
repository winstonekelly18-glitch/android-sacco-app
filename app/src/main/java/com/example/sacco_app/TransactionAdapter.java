package com.example.sacco_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<String> {

    public TransactionAdapter(Context context, ArrayList<String> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }

        String item = getItem(position);
        TextView tvType = convertView.findViewById(R.id.tvTxType);
        TextView tvAmount = convertView.findViewById(R.id.tvTxAmount);

        if (item != null && item.contains(":")) {
            String[] parts = item.split(":");
            String type = parts[0].trim();
            String amount = parts[1].trim();

            tvType.setText(type);

            if (type.equalsIgnoreCase("Deposit")) {
                tvAmount.setText("+ " + amount);
                tvAmount.setTextColor(Color.parseColor("#2E7D32")); // Green
            } else {
                tvAmount.setText("- " + amount);
                tvAmount.setTextColor(Color.parseColor("#C62828")); // Red
            }
        }

        return convertView;
    }
}