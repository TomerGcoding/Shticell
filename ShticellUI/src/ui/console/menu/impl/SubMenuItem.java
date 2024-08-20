package ui.console.menu.impl;

import ui.console.menu.api.MenuItem;

import java.util.Scanner;

public class SubMenuItem implements MenuItem {
    private String description;
    private Menu submenu;

    public SubMenuItem(String description, Menu submenu) {
        this.description = description;
        this.submenu = submenu;
    }

    @Override
    public void execute() {
        submenu.display();
        submenu.executeOption(getUserChoice());
    }

    @Override
    public String getDescription() {
        return description;
    }

    private int getUserChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }
}

