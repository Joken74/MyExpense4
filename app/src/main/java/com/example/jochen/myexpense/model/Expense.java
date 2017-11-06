package com.example.jochen.myexpense.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Jochen on 28.10.2017.
 */

// Eine Modelklasse / Datenhalter hat Konstruktoren, Variablen/Fields, Getter und Setter

public class Expense implements Serializable {

    private long id;

    private String amount;
    private String category;
    private Calendar date;
    private boolean important;
    private String description;

    // Zwei Konstruktoren, Date ist optional
    // Im reduzierten Konstruktor wird der andere mit NULL aufgerufen um nichts zu vergessen

    public Expense(final String amount, final String category) {
        this(amount, category, null, false, null);
    }

    public Expense(final String amount, final String category, final Calendar date, final boolean important, final String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.important = important;
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(final Calendar date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
