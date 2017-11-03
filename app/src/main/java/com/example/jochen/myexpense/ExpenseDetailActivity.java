package com.example.jochen.myexpense;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.jochen.myexpense.db.MyExpenseOpenHandler;
import com.example.jochen.myexpense.model.Expense;

public class ExpenseDetailActivity extends AppCompatActivity {
    public static final String EXPENSE_ID_KEY = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        long id = getIntent().getLongExtra(EXPENSE_ID_KEY,0);

        Expense expense = MyExpenseOpenHandler.getInstance(this).readExpense(id);

        // serialisiertes element wird hier wieder entpackt
        //Expense expense = (Expense) getIntent().getSerializableExtra(EXPENSE_ID_KEY);

        Log.e("expense id", String.valueOf(expense.getId()));
        Log.e("expense amount", expense.getAmount());
    }
}
