import engine.Engine;
import engine.EngineImpl;
import engine.sheet.api.Sheet;
import engine.sheet.impl.SheetImpl;
import ui.console.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        Sheet sheet = new SheetImpl("First Sheet",10,10,10,10);
        for (int i = 0; i < sheet.getProperties().getNumRows(); i++) {
            for (int j = 0; j < sheet.getProperties().getNumCols(); j++) {
                sheet.setCell(i,j,"{MINUS,9,{POW,2,3}}");
            }
        }
        Engine engine = new EngineImpl(sheet);  // Your engine implementation
        ConsoleUI ui = new ConsoleUI(engine);
        ui.start();
    }
}
