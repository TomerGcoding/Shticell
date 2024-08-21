package ui.console;

import engine.Engine;
import engine.dto.SheetDTO;
import engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;
import ui.console.menu.api.MenuItem;
import ui.console.menu.impl.Menu;
import ui.console.menu.impl.SimpleActionMenuItem;
import ui.console.menu.impl.SubMenuItem;
import ui.console.utils.SheetPrinter;
import ui.console.utils.TablePrinter;

import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final Engine engine;

    public ConsoleUI(Engine engine) {
        this.scanner = new Scanner(System.in);
        this.engine = engine;
    }

    public void start() {
        Menu mainMenu = createMainMenu();
        while (true) {
            mainMenu.display();
            int choice = getUserChoice();
            if (choice == 6) break;  // This should ideally be handled as a MenuItem action
            mainMenu.executeOption(choice);
        }
    }

    private Menu createMainMenu() {
        Menu menu = new Menu("Main Menu");

        menu.addMenuItem(new SimpleActionMenuItem("Load a file", this::handleOption1));
        menu.addMenuItem(new SimpleActionMenuItem("Show spreadsheet", this::handleOption2));
        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to see its data", this::handleOption3));
        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to update", this::handleOption4));
        menu.addMenuItem(new SimpleActionMenuItem("Show versions", this::handleOption5));
        menu.addMenuItem(new SimpleActionMenuItem("Exit", this::handleExit));
        Menu subMenu = createSubMenu("Save/Load an existing sheet",menu);
        menu.addMenuItem(new SubMenuItem("Save/Load an existing sheet", subMenu));

        // You can add more options or submenus here
        return menu;
    }
    private Menu createSubMenu(String title,Menu mainMenu) {
        Menu subMenu = new Menu(title);
        subMenu.addMenuItem(new SimpleActionMenuItem("Load existing sheet", () -> System.out.println("Sub Option 1 selected")));
        subMenu.addMenuItem(new SimpleActionMenuItem("Save existing sheet to file ", () -> System.out.println("Sub Option 2 selected")));
        subMenu.addMenuItem(new SimpleActionMenuItem("Back to Main Menu", () -> mainMenu.display()));
        return subMenu;
    }

    private void handleOption1() {
        System.out.println("Please enter the full path to the XML file you want to load: ");
        String path = scanner.nextLine();
        try {
            engine.loadSheetFile(path);
            System.out.println("File loaded successfully");
        } catch (JAXBException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private void handleOption2() {
        try {
            SheetDTO result = engine.showSheet();
            SheetPrinter.printSheet(result);

        }
        catch (IllegalStateException e){
            System.out.println("Error showing sheet: " + e.getMessage());
        }
        // Example method

    }

    private void handleOption3() {
        System.out.println("Please choose a cell to see its data: ");
        String cell = scanner.nextLine();
//        String data = engine.getCellData(cell);  // Assuming a method to get cell data
//        System.out.println("Data in cell " + cell + ": " + data);
    }

    private void handleOption4() {
        System.out.println("Please choose a cell to update: ");
        String cell = scanner.nextLine();
        System.out.println("Please write the new cell value: ");
        String value = scanner.nextLine();
        engine.setCell(cell, value);
        handleOption2(); // Display the updated spreadsheet
    }

    private void handleOption5() {
        try {
            Map<Integer, Integer> versionTable = engine.showVersionTable();
            TablePrinter.printVersionToCellChangedTable(versionTable);
            System.out.println("Please choose a version to see its data: ");
            int version = scanner.nextInt();
            SheetPrinter.printSheet(engine.showChosenVersion(version));
        }
        catch (IllegalStateException e) {
            System.out.println("Error showing version: " + e.getMessage());
        }
    }

    private void handleExit() {
        System.out.println("Exiting...");
    }

    private void handleOption7() {
        System.out.println("Save/Load an existing sheet not implemented yet.");
        // Implement your save/load logic here
    }

    private int getUserChoice() {
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline
        return choice;
    }
}
