import engine.Engine;
import engine.EngineImpl;
import ui.console.ConsoleUI;

    public class Main {
        public static void main(String[] args) {

            Engine engine = new EngineImpl();
            ConsoleUI ui = new ConsoleUI(engine);
            ui.start();
        }
    }

