package com.example.sacco_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SaccoApp.db";
    private static final int DATABASE_VERSION = 4; // Bumped version for complete multi-user migration

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // User Columns
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BALANCE = "balance";

    // Transaction Columns
    private static final String COLUMN_TX_ID = "tx_id";
    private static final String COLUMN_TX_USER_ID = "user_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULL_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_BALANCE + " REAL DEFAULT 0.0)";
        db.execSQL(createUsersTable);

        String createTxTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TX_USER_ID + " INTEGER, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL)";
        db.execSQL(createTxTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }

    // Register new user
    public boolean registerUser(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FULL_NAME, fullName);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_BALANCE, 0.0);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    // Validate login credentials and return user_id (-1 if invalid)
    public int checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password});
        int userId = -1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
            cursor.close();
        }
        return userId;
    }

    // Get user balance by user ID
    public double getBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        double balance = 0.0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0);
            }
            cursor.close();
        }
        return balance;
    }

    // Update user balance
    public boolean updateBalance(int userId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BALANCE, newBalance);
        int result = db.update(TABLE_USERS, cv, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Log transaction for specific user
    public void addTransaction(int userId, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TX_USER_ID, userId);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_AMOUNT, amount);
        db.insert(TABLE_TRANSACTIONS, null, cv);
    }

    // Get all user transactions
    public ArrayList<String> getUserTransactions(int userId) {
        return getFilteredTransactions(userId, "ALL");
    }

    // Get filtered user transactions
    public ArrayList<String> getFilteredTransactions(int userId, String filterType) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_TX_USER_ID + "=?";
        ArrayList<String> argsList = new ArrayList<>();
        argsList.add(String.valueOf(userId));

        if (filterType != null && !filterType.equalsIgnoreCase("ALL")) {
            query += " AND " + COLUMN_TYPE + "=?";
            argsList.add(filterType);
        }

        query += " ORDER BY " + COLUMN_TX_ID + " DESC";

        Cursor cursor = db.rawQuery(query, argsList.toArray(new String[0]));

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int typeIdx = cursor.getColumnIndex(COLUMN_TYPE);
                int amountIdx = cursor.getColumnIndex(COLUMN_AMOUNT);
                do {
                    if (typeIdx != -1 && amountIdx != -1) {
                        String type = cursor.getString(typeIdx);
                        double amount = cursor.getDouble(amountIdx);
                        list.add(type + ": KES " + String.format("%.2f", amount));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}