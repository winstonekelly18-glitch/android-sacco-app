package com.example.sacco_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvSavingsBalance;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("SaccoSession", MODE_PRIVATE);
            userId = prefs.getInt("USER_ID", -1);
        }

        tvSavingsBalance = findViewById(R.id.tvSavingsBalance);
        Button btnDeposit = findViewById(R.id.btnDeposit);
        Button btnWithdraw = findViewById(R.id.btnWithdraw);
        Button btnTransactions = findViewById(R.id.btnTransactions);

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DepositActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, WithdrawActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, TransactionsActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("SaccoSession", MODE_PRIVATE);
            userId = prefs.getInt("USER_ID", -1);
        }

        if (userId != -1 && tvSavingsBalance != null) {
            double currentBalance = dbHelper.getBalance(userId);
            tvSavingsBalance.setText("KES " + String.format("%.2f", currentBalance));
        }
    }
}