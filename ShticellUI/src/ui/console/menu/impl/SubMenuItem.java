package ui.console.menu.impl;

import ui.console.menu.api.MenuItem;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SubMenuItem implements MenuItem {
    private final String description;
    private final Menu submenu;

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
        int choice = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                validInput = true; // Exit loop if the input is a valid integer
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Clear the invalid input from the scanner
            }
        }

        return choice;
    }


}

