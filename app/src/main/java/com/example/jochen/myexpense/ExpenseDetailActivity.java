package com.example.jochen.myexpense;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jochen.myexpense.db.MyExpenseOpenHandler;
import com.example.jochen.myexpense.model.Expense;

import java.util.Calendar;

public class ExpenseDetailActivity extends AppCompatActivity {
    public static final String EXPENSE_ID_KEY = "ID";

    private TextView amount;
    private TextView date;
    private TextView description;
    private CheckBox important;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        long id = getIntent().getLongExtra(EXPENSE_ID_KEY,0);

        amount = (TextView) findViewById(R.id.amount);
        date = (TextView) findViewById(R.id.date);
        description = (TextView) findViewById(R.id.description);
        important = (CheckBox) findViewById(R.id.important);

        Expense expense = MyExpenseOpenHandler.getInstance(this).readExpense(id);

        // serialisiertes element wird hier wieder entpackt
        //Expense expense = (Expense) getIntent().getSerializableExtra(EXPENSE_ID_KEY);

        amount.setText(expense.getAmount());
        date.setText(expense.getDate() == null ? "-" : getDateInString(expense.getDate()));
        description.setText(expense.getDescription() == null ? "no description" : expense.getDescription());
        important.setChecked(expense.isImportant());

        Log.e("expense id", String.valueOf(expense.getId()));
        Log.e("expense amount", expense.getAmount());
    }

    private String getDateInString (Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }
}
