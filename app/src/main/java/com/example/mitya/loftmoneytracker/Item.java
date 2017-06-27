package com.example.mitya.loftmoneytracker;

/**
 * Created by Mitya on 23.06.2017.
 */

public class Item {
    final String name;
    final int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    private final String[] types = {"expense",
            "income"};

    static final String TYPE_EXPENSE = "expense";
    static final String TYPE_INCOME = "income";


}
