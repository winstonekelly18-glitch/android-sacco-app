package com.example.sacco_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvTransactions;
    private TextView tvEmpty;
    private int userId;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        dbHelper = new DatabaseHelper(this);
        lvTransactions = findViewById(R.id.lvTransactions);
        tvEmpty = findViewById(R.id.tvEmpty);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("SaccoSession", MODE_PRIVATE);
            userId = prefs.getInt("USER_ID", -1);
        }

        Button btnFilterAll = findViewById(R.id.btnFilterAll);
        Button btnFilterDeposit = findViewById(R.id.btnFilterDeposit);
        Button btnFilterWithdraw = findViewById(R.id.btnFilterWithdraw);

        if (btnFilterAll != null) {
            btnFilterAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "ALL";
                    loadTransactions(currentFilter);
                }
            });
        }

        if (btnFilterDeposit != null) {
            btnFilterDeposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "Deposit";
                    loadTransactions(currentFilter);
                }
            });
        }

        if (btnFilterWithdraw != null) {
            btnFilterWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "Withdrawal";
                    loadTransactions(currentFilter);
                }
            });
        }

        loadTransactions(currentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(currentFilter);
    }

    private void loadTransactions(String filterType) {
        if (userId == -1) {
            if (tvEmpty != null) {
                tvEmpty.setText("Session error. Please login again.");
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (lvTransactions != null) {
                lvTransactions.setVisibility(View.GONE);
            }
            return;
        }

        ArrayList<String> transactions = dbHelper.getFilteredTransactions(userId, filterType);
        if (transactions == null || transactions.isEmpty()) {
            if (tvEmpty != null) {
                tvEmpty.setText("No transactions found.");
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (lvTransactions != null) {
                lvTransactions.setVisibility(View.GONE);
            }
        } else {
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.GONE);
            }
            if (lvTransactions != null) {
                lvTransactions.setVisibility(View.VISIBLE);
                TransactionAdapter adapter = new TransactionAdapter(this, transactions);
                lvTransactions.setAdapter(adapter);
            }
        }
    }
}