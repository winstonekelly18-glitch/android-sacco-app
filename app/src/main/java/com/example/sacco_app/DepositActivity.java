package com.example.sacco_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DepositActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        EditText etDepositAmount = findViewById(R.id.etDepositAmount);
        Button btnConfirmDeposit = findViewById(R.id.btnConfirmDeposit);

        btnConfirmDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etDepositAmount.getText().toString().trim();

                if (amountStr.isEmpty()) {
                    Toast.makeText(DepositActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(DepositActivity.this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount <= 0) {
                    Toast.makeText(DepositActivity.this, "Please enter an amount greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userId == -1) {
                    Toast.makeText(DepositActivity.this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double currentBalance = dbHelper.getBalance(userId);
                double newBalance = currentBalance + amount;

                if (dbHelper.updateBalance(userId, newBalance)) {
                    dbHelper.addTransaction(userId, "Deposit", amount);
                    Toast.makeText(DepositActivity.this, "Deposited KES " + String.format("%.2f", amount), Toast.LENGTH_SHORT).show();
                    finish(); // Return to dashboard
                } else {
                    Toast.makeText(DepositActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}