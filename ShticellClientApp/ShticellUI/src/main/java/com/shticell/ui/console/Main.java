package com.shticell.ui.console;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;

public class Main {
    public static void main(String[] args) {
        Engine engine = new EngineImpl();
        ConsoleUI ui = new ConsoleUI(engine);
        ui.start();
    }
}

