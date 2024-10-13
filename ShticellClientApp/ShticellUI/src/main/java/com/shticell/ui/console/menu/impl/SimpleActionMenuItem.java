package com.shticell.ui.console.menu.impl;

import com.shticell.ui.console.menu.api.MenuItem;

public class SimpleActionMenuItem implements MenuItem {
    private final String description;
    private final Runnable action;

    public SimpleActionMenuItem(String description, Runnable action) {
        this.description = description;
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
