import engine.Engine;
import engine.EngineImpl;
import engine.sheet.api.Sheet;
import engine.sheet.impl.SheetImpl;
import ui.console.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        Sheet sheet = new SheetImpl("First Sheet",4,4,4,4);
        for (int i = 0; i < sheet.getProperties().getNumRows(); i++) {
            for (int j = 0; j < sheet.getProperties().getNumCols(); j++) {
                sheet.setCell(i,j,"{SUB,TOMER,2.2,4.2}");
            }
        }
        Engine engine = new EngineImpl(sheet);  // Your engine implementation
        ConsoleUI ui = new ConsoleUI(engine);
        ui.start();
    }
}
