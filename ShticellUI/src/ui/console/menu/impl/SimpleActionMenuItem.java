package ui.console.menu.impl;

import ui.console.menu.api.MenuItem;

public class SimpleActionMenuItem implements MenuItem {
    private String description;
    private Runnable action;

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
