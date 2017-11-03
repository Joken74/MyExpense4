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
import java.util.Comparator;
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
        Button sort = (Button) findViewById(R.id.sort);

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
                Expense expense = (Expense) element; // sollte eigentlich erst im if gecastet werden, aber so funktioniert es

                if(element instanceof Expense) {
                    //Expense expense = (Expense) element; // element wird zu einem Expense gecastet
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

                    // Neue Daten für Sortierfunktion
                    Expense expense1 = new Expense("111", "Essen");
                    Calendar c1 = Calendar.getInstance();
                    c1.set(2017, 11, 03);
                    expense1.setDate(c1);
                    database.createExpense(expense1);

                    Expense expense2 = new Expense("222", "Auto");
                    Calendar c2 = Calendar.getInstance();
                    c2.set(2015, 11, 02);
                    expense2.setDate(c2);
                    database.createExpense(expense2);

                    Expense expense3 = new Expense("333", "Wohnen");
                    database.createExpense(expense3);

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
// Array adapter hat sort funktion
        if(sort!= null) {
            sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    adapter.sort(new Comparator<Expense>() {
                        @Override
                        // Vergleichswert ist ein integer, 0 ist weder pos noch neg. Neg = 0.
                        // wenn expense2 höherwertig ist, dann ist es eine negative Zahl und vice versa
                        public int compare(final Expense expense1, final Expense expense2) {

                            // Zuerst nach Datum, wenn es kein Datum hat dann nach Amount
                            // Innerhalb der Datumswerte den Wert nach oben, welcher das heutige Datum am nächsten hat

                            if(expense1.getDate() != null && expense2.getDate() != null) {
                                long date = (expense1.getDate().getTimeInMillis() / 1000) - (expense2.getDate().getTimeInMillis() / 1000);

                                if (date != 0) {
                                    return (int) date;
                                }

                            } else if(expense1.getDate() != null) {
                                return -1;
                            } else if(expense2.getDate() != null) {
                                return 1;
                            }

                            // Wenn kein Datum, dann nach Betrag
                            return expense1.getAmount().compareToIgnoreCase(expense2.getAmount());
                        }
                    });
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
