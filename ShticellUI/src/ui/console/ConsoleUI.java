package ui.console;

import engine.Engine;
import engine.dto.SheetDTO;
import jakarta.xml.bind.JAXBException;
import ui.console.menus.Menu;
import ui.console.utils.SheetPrinter;

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
            if (choice == 6) break;
            mainMenu.executeOption(choice);
        }
    }


    private Menu createMainMenu() {
        Menu menu = new Menu("Main Menu");
        menu.addOption("Load a file", this::handleOption1);
        menu.addOption("Show spreadsheet", this::handleOption2);
        menu.addOption("Choose a cell to see it's data", this::handleOption3);
        menu.addOption("Choose a cell to update", this::handleOption4);
        menu.addOption("Show versions", this::handleOption5);
        menu.addOption("Exit", this::handleOption6);
        // Add more options as needed
        return menu;
    }

    private void handleOption5() {
    }

    private void handleOption6() {
        
    }

    private void handleOption4() {
        System.out.println("Please choose a cell to update: ");
        String cell = scanner.nextLine();
        System.out.println("Please write the new cell value: ");
        String value = scanner.nextLine();
        engine.setCell(cell, value);
        handleOption2();
    }

    private void handleOption3() {

    }

    private void handleOption1(){
        System.out.println("Please enter the full path to the XML file you want to load:  ");
        String path = scanner.nextLine();
        try {
            engine.loadSheetFile(path);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleOption2() {
        SheetDTO result = engine.showSheet();   // Example method
        SheetPrinter printer = new SheetPrinter(result);
        printer.printSheet();
    }

    private int getUserChoice() {
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }
}

