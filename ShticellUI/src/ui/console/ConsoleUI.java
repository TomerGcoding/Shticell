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
import ui.console.menu.impl.SubMenuItem;
import ui.console.utils.SheetPrinter;
import ui.console.utils.TablePrinter;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private Engine engine;

    public ConsoleUI(Engine engine) {
        this.scanner = new Scanner(System.in);
        this.engine = engine;
    }

    public void start() {
        Menu mainMenu = createMainMenu();
        while (true) {
            mainMenu.display();
            int choice = getUserChoice();
            if (choice == 7) {
                mainMenu.executeOption(choice);
                break;
            }// This should ideally be handled as a MenuItem action
            mainMenu.executeOption(choice);
        }
    }

    private Menu createMainMenu() {
        Menu menu = new Menu("Main Menu");
        Menu subMenu = new Menu("Save/Load current system state");

        menu.addMenuItem(new SimpleActionMenuItem("Load a file", this::handleOption1));

        menu.addMenuItem(new SimpleActionMenuItem("Show spreadsheet", this::handleOption2));

        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to see its data", this::handleOption3));

        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to update", this::handleOption4));

        menu.addMenuItem(new SimpleActionMenuItem("Show versions", this::handleOption5));

        menu.addMenuItem(new SubMenuItem("Save/Load current system state",subMenu));
        subMenu.addMenuItem(new SimpleActionMenuItem("Save current system state", this::handleOption6));
        subMenu.addMenuItem(new SimpleActionMenuItem("Load saved system state",this::handleOption7));

        menu.addMenuItem(new SimpleActionMenuItem("Exit", this::handleExit));

        // You can add more options or submenus here
        return menu;
    }



    private void handleOption1() {
        System.out.println("Please enter the full path to the XML file you want to load or Q/q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path.trim()))
            return;
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
        System.out.println("Please select a cell to view its details or Q/q to go back to the main menu: ");
        String cellId = scanner.nextLine();
        if(checkIfQuit(cellId))
            return;
        try {
            CellDTO cellInfo = engine.getCellInfo(cellId);
            if (cellInfo != null) {
                System.out.println("Cell ID: " + cellId + "\nOriginal Value: " + cellInfo.getOriginalValue() +
                        "\nEffective Value: " + cellInfo.getEffectiveValue() + "\nLast updated version: " + cellInfo.getVersion());
                System.out.println("\nDependencies of this cell: ");
                cellInfo.getDependsOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
                System.out.println("\nInfluences of this cell: ");
                cellInfo.getInfluencingOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
            } else {
                System.out.println("\nCell " + cellId + " has not been initialized yet.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void handleOption4() {
        System.out.println("\nPlease select a cell to update or Q/q to go back to the main menu: ");
        String cellId = scanner.nextLine();
        if(checkIfQuit(cellId))
            return;
        CellDTO cell = engine.getCellInfo(cellId);
        if(cell != null) {
            System.out.println("\nCell ID: " + cell.getId() + "\nOriginal Value: " + cell.getOriginalValue() +
                    "\nEffective Value: " + cell.getEffectiveValue().toString());
        }
        else {
            System.out.println("\nCell " + cellId + " has not been initialized yet.");
        }
        System.out.println("\nPlease enter the new cell's value or Q/q to go back to the main menu: ");
        String value = scanner.nextLine();
        if(checkIfQuit(value))
            return;
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
            System.out.println("Please choose a version to see its data or Q/q to go back to the main menu: ");
            String version = scanner.nextLine();
            if(checkIfQuit(version))
                return;
            SheetPrinter.printSheet(engine.showChosenVersion(Integer.parseInt(version)));
        }
        catch (IllegalStateException e) {
            System.out.println("Error showing version: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Error showing version: " + e.getMessage());
        }
    }

    private void handleExit() {
        System.out.println("Exiting...");
    }

    private void handleOption6() {
        System.out.println("Please enter the full path to where you want to save the file or q/Q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path))
            return;
        try{
            engine.writeEngineToFile(path);
            System.out.println("System saved successfully");
        }
        catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void handleOption7() {
        System.out.println("Please enter the full path to where your saved file is or q/Q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path))
            return;
        try{
            engine = engine.readEngineFromFile(path);
            System.out.println("System loaded successfully");
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
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

    private boolean checkIfQuit(String input) {
        if (input.toUpperCase().equals("Q")) {
            return true;
        }
        return false;
    }

}
