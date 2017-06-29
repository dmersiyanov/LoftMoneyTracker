package com.example.mitya.loftmoneytracker;

import java.io.Serializable;

/**
 * Created by Mitya on 23.06.2017.
 */

public class Item implements Serializable {
    public String name;
    public String type;
    final int price;
    public int id = -1;

    public Item(String name, int price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    private final String[] types = {"expense",
            "income"};

    static final String TYPE_EXPENSE = "expense";
    static final String TYPE_INCOME = "income";


}
