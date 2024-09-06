package com.shticell.ui.console.menu.impl;

import com.shticell.ui.console.menu.api.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String title;
    private final List<MenuItem> items;

    public Menu(String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public void display() {
        System.out.println("\n" + title);
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getDescription());
        }
    }

    public void executeOption(int choice) {
        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid option, please try again.");
        }
        else {
            items.get(choice - 1).execute();
        }
    }
}

