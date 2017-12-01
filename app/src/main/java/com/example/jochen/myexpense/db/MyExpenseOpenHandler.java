package com.example.jochen.myexpense.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import com.example.jochen.myexpense.model.Expense;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jochen on 01.10.2017.
 */

public class MyExpenseOpenHandler extends SQLiteOpenHelper {

    public static MyExpenseOpenHandler INSTANCE = null;

    private static final String LOG_TAG = MyExpenseOpenHandler.class.getSimpleName();

    private static final String DB_NAME = "EXPENSES";
    private static final int VERSION = 12;
    private static final String TABLE_NAME = "expenses";

    public static final String ID_COLUMN = "ID";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String CATEGORY_COLUMN = "category";
    public static final String DATE_COLUMN = "date";
    public static final String IMPORTANT_COLUMN = "important";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";

    // Cursor factory wird nicht benötigt

    private MyExpenseOpenHandler(final Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static MyExpenseOpenHandler getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MyExpenseOpenHandler(context);
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(final SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME + " (" + ID_COLUMN + " INTEGER PRIMARY KEY, " + AMOUNT_COLUMN + " TEXT NOT NULL, " + CATEGORY_COLUMN + " TEXT, " + DATE_COLUMN + " INTEGER DEFAULT NULL, " + IMPORTANT_COLUMN + " INTEGER DEFAULT 0, " + DESCRIPTION_COLUMN + " TEXT DEFAULT NULL, " + LATITUDE_COLUMN + " REAL DEFAULT NULL, " + LONGITUDE_COLUMN + " REAL DEFAULT NULL)";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTable);

        onCreate(db);
    }

    public Expense createExpense(final Expense expense) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AMOUNT_COLUMN, expense.getAmount());
        values.put(CATEGORY_COLUMN, expense.getCategory());
        values.put(DATE_COLUMN, expense.getDate() == null ? null : expense.getDate().getTimeInMillis() / 1000);  // Inlay If da Date optional
        values.put(IMPORTANT_COLUMN, expense.isImportant() ? 1 : 0); // wenn das true ist, dann 1, sonst 0. Standard ist ja false = 0.
        values.put(DESCRIPTION_COLUMN, expense.getDescription() == null ? null : expense.getDescription());

        values.put(LATITUDE_COLUMN, expense.getLocation() == null ? null : expense.getLocation().latitude);
        values.put(LONGITUDE_COLUMN, expense.getLocation() == null ? null : expense.getLocation().longitude);

        long newID = database.insert(TABLE_NAME, null, values); // wawrum gibt das die ID zurück? Ohne SQL Abfrage...

        database.close();

        return readExpense(newID); // oben wird ein manuelles Expense reingeschmissen und es kommt ein strukturiertes Expense Objekt aus der DB zurück
    }

    public Expense readExpense(final long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        // Tabellen-Name, dann welche Spalten, dann Selection = WHERE clause,
        Cursor cursor = database.query(TABLE_NAME, new String[]{ID_COLUMN, AMOUNT_COLUMN, CATEGORY_COLUMN, DATE_COLUMN, IMPORTANT_COLUMN, DESCRIPTION_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN}, ID_COLUMN + " = ?", new String[]{String.valueOf(id)}, null, null, null);  // Cursor zeigt auf das Ergebnis einer DB Abfrage
        // SELECT id, amount, column, date WHERE id = ?

        Expense expense = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            expense = new Expense(cursor.getString(cursor.getColumnIndex(AMOUNT_COLUMN)), cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN)));
            expense.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
            expense.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION_COLUMN)));


            Calendar calendar = null;
            if (!cursor.isNull(cursor.getColumnIndex(DATE_COLUMN))) {
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DATE_COLUMN)) * 1000);
            }

            expense.setImportant(cursor.getInt(cursor.getColumnIndex(IMPORTANT_COLUMN)) == 1); // wenn true wird es zu 1, sonst 0.
            expense.setDate(calendar);

            if (!cursor.isNull(cursor.getColumnIndex(LATITUDE_COLUMN)) && !cursor.isNull(cursor.getColumnIndex(LONGITUDE_COLUMN))) {
                expense.setLocation(new LatLng(cursor.getFloat(cursor.getColumnIndex(LATITUDE_COLUMN)), cursor.getFloat(cursor.getColumnIndex(LONGITUDE_COLUMN))));
            }
        }
        database.close();
        return expense;
    }

    public List<Expense> readAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        // Jetzt braucht es die DB Abfrage
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null); // hier wäre noch ein ORDER BY möglich

        if (cursor.moveToFirst()) {
            do {
                Expense expense = readExpense(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                if (expense != null) {
                    expenses.add(expense);
                }
            } while (cursor.moveToNext());
        }

        database.close();
        return expenses;
    }

    public Expense updateExpense(final Expense expense) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AMOUNT_COLUMN, expense.getAmount());
        values.put(CATEGORY_COLUMN, expense.getCategory());
        values.put(DATE_COLUMN, expense.getDate() == null ? null : expense.getDate().getTimeInMillis() / 1000);
        values.put(IMPORTANT_COLUMN, expense.isImportant() ? 1 : 0);
        values.put(DESCRIPTION_COLUMN, expense.getDescription());

        values.put(LATITUDE_COLUMN, expense.getLocation() == null ? null : expense.getLocation().latitude);
        values.put(LONGITUDE_COLUMN, expense.getLocation() == null ? null : expense.getLocation().longitude);

        database.update(TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{String.valueOf(expense.getId())});

        database.close();

        return this.readExpense(expense.getId());
    }

    public void deleteExpense(final Expense expense) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, ID_COLUMN + " = ?", new String[]{String.valueOf(expense.getId())});
        database.close();
    }

    public void deleteAllExpenses() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
        database.close();
    }

    // cursor braucht _id als Spaltenname in der Tabelle
    public Cursor getAllExpensesAsCursor() {
        return this.getReadableDatabase().rawQuery("SELECT " + ID_COLUMN + " as _id, " + AMOUNT_COLUMN + ", " + CATEGORY_COLUMN + ", " + DATE_COLUMN + " FROM " + TABLE_NAME, null);
    }

    public Expense getFirstExpense() {
        List<Expense> expenses = this.readAllExpenses();

        if(expenses.size() > 0) {
            return expenses.get(0); // was macht get(0) = 0 gleich erste Position in Liste
        } else {
            return null;
        }
    }
}
