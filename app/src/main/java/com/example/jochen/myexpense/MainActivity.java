package com.example.jochen.myexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.jochen.myexpense.adapter.listview.ExpenseOverviewAdapter;
import com.example.jochen.myexpense.db.MyExpenseOpenHandler;
import com.example.jochen.myexpense.model.Expense;

import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    // private static final String LOG_TAG = MyExpenseOpenHandler.class.getSimpleName();

    private ListView resultList;
    private List<Expense> dataSource;
    private ExpenseOverviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button create = (Button) findViewById(R.id.create);
        Button updateFirst = (Button) findViewById(R.id.updateFirst);
        Button clearFirst = (Button) findViewById(R.id.clearFirst);
        Button clearAll = (Button) findViewById(R.id.clearAll);

        this.resultList = (ListView) findViewById(R.id.resultList) ;

        this.dataSource = MyExpenseOpenHandler.getInstance(this).readAllExpenses();

        this.adapter = new ExpenseOverviewAdapter(this, dataSource);

        // Kontext der Activity = this, layout wie jeder einzelne Eintrag angezeigt werden soll, Datenquelle
        // wird gelöscht: android.R.layout.simple_list_item_1,
        this.resultList.setAdapter(adapter);
        // Was passiert bei OnClick?
        this.resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, final long l) {
                Object element = adapterView.getAdapter().getItem(i);

                if(element instanceof Expense) {
                    Expense expense = (Expense) element; // element wird zu einem Expense gecastet
                    Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                    intent.putExtra(ExpenseDetailActivity.EXPENSE_ID_KEY, expense.getId()); // Element wird "serialisiert, muss dann in der Detail wieder entpackt werden
                    startActivity(intent);
                }
                Log.e("Click on List: ", element.toString());
            }
        });

        if(updateFirst != null) {
            updateFirst.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    if(dataSource.size() > 0) {
                        MyExpenseOpenHandler database = MyExpenseOpenHandler.getInstance(MainActivity.this);
                        Random r = new Random();
                        dataSource.get(0).setAmount(String.valueOf(r.nextInt()));
                        database.updateExpense(dataSource.get(0));
                        refreshListView();
                    }

                    Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                    startActivity(intent);


                }
        });
        }

        if(clearFirst != null) {
            clearFirst.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    if(dataSource.size() > 0) {
                        MyExpenseOpenHandler database = MyExpenseOpenHandler.getInstance(MainActivity.this);
                        database.deleteExpense(dataSource.get(0));
                        refreshListView();
                    }

                }
            });
        }

        if(create != null) {
            create.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    MyExpenseOpenHandler database = MyExpenseOpenHandler.getInstance(MainActivity.this);
                    database.createExpense(new Expense("123", "Auto"));
                    database.createExpense(new Expense("234", "Essen", Calendar.getInstance()));

                    //database.createExpense(new Expense("234", "Essen", "2016"));
                    refreshListView();
                }
            });
        }

        if(clearAll != null) {
            clearAll.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    MyExpenseOpenHandler database = MyExpenseOpenHandler.getInstance(MainActivity.this);
                    database.deleteAllExpenses();
                    refreshListView();

                }
            });
        }
    }

    private void refreshListView() {
        dataSource.clear();
        dataSource.addAll(MyExpenseOpenHandler.getInstance(this).readAllExpenses());
        adapter.notifyDataSetChanged();
    }
}
