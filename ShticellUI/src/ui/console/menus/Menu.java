package ui.console.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu {
    private String title;
    private List<String> options;
    private Map<Integer, Runnable> actions;

    public Menu(String title) {
        this.title = title;
        this.options = new ArrayList<>();
        this.actions = new HashMap<>();
    }

    public void addOption(String option, Runnable action) {
        options.add(option);
        actions.put(options.size(), action);
    }

    public void display() {
        System.out.println("\n" + title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    public void executeOption(int choice) {
        if (choice == 6) {
            System.out.println("Exiting...");
            return;
        }
        Runnable action = actions.get(choice);
        if (action != null) {
            action.run();
        } else {
            System.out.println("Invalid option, please try again.");
        }
    }
}

