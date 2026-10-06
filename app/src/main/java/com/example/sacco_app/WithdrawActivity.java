package com.example.sacco_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WithdrawActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        EditText etWithdrawAmount = findViewById(R.id.etWithdrawAmount);
        Button btnConfirmWithdraw = findViewById(R.id.btnConfirmWithdraw);

        btnConfirmWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etWithdrawAmount.getText().toString().trim();

                if (amountStr.isEmpty()) {
                    Toast.makeText(WithdrawActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(WithdrawActivity.this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount <= 0) {
                    Toast.makeText(WithdrawActivity.this, "Please enter an amount greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userId == -1) {
                    Toast.makeText(WithdrawActivity.this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double currentBalance = dbHelper.getBalance(userId);

                if (amount > currentBalance) {
                    Toast.makeText(WithdrawActivity.this, "Insufficient funds! Balance: KES " + String.format("%.2f", currentBalance), Toast.LENGTH_LONG).show();
                    return;
                }

                double newBalance = currentBalance - amount;

                if (dbHelper.updateBalance(userId, newBalance)) {
                    dbHelper.addTransaction(userId, "Withdrawal", amount);
                    Toast.makeText(WithdrawActivity.this, "Withdrew KES " + String.format("%.2f", amount), Toast.LENGTH_SHORT).show();
                    finish(); // Return to dashboard
                } else {
                    Toast.makeText(WithdrawActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}