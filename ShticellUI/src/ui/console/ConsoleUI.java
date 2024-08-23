package ui.console;

import engine.Engine;
import engine.dto.CellDTO;
import engine.dto.SheetDTO;
//import engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;
//import ui.console.menu.api.MenuItem;
import ui.console.menu.impl.Menu;
import ui.console.menu.impl.SimpleActionMenuItem;
//import ui.console.menu.impl.SubMenuItem;
import ui.console.utils.SheetPrinter;
import ui.console.utils.TablePrinter;

import java.util.InputMismatchException;
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
        //Menu subMenu = createSubMenu("Save an existing sheet",menu);
        menu.addMenuItem(new SimpleActionMenuItem("Save an existing sheet",this::handleOption7 ));

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
        System.out.println("Please select a cell to view its details: ");
        String cellId = scanner.nextLine();

        try {
            CellDTO cellInfo = engine.getCellInfo(cellId);
            if (cellInfo != null) {
                System.out.println("Cell ID: " + cellId + "\n Original Value: " + cellInfo.getOriginalValue() +
                        "\n Effective Value: " + cellInfo.getEffectiveValue() + "\nLast updated version: " + cellInfo.getVersion());
                System.out.println("\nDependencies of this cell:\n ");
                cellInfo.getDependsOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
                System.out.println("\nInfluences of this cell:\n ");
                cellInfo.getInfluencingOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
            } else {
                System.out.println("\nCell " + cellId + " has not been initialized yet.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void handleOption4() {
        System.out.println("\nPlease select a cell to update: ");
        String cellId = scanner.nextLine();
        CellDTO cell = engine.getCellInfo(cellId);
        if(cell != null) {
            System.out.println("\nCell ID: " + cell.getId() + "\nOriginal Value: " + cell.getOriginalValue() +
                    "\nEffective Value: " + cell.getEffectiveValue().toString());
        }
        else {
            System.out.println("\nCell " + cellId + " has not been initialized yet.");
        }
        System.out.println("\nPlease enter the new cell's value: ");
        String value = scanner.nextLine();
        try{
            engine.setCell(cellId, value);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error updating cell: " + e.getMessage());
        }
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
        System.out.println("Please enter the full path to where you want to save the file: ");
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
