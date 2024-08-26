import engine.Engine;
import engine.EngineImpl;
//import engine.sheet.api.Sheet;
//import engine.sheet.cell.api.Cell;
//import engine.sheet.coordinate.Coordinate;
//import engine.sheet.coordinate.CoordinateImpl;
import engine.sheet.impl.SheetImpl;
import ui.console.ConsoleUI;

//public class Main {
//    public static void main(String[] args) {
//        Engine engine = new EngineImpl();  // Your engine implementation
//        ConsoleUI ui = new ConsoleUI(engine);
//        ui.start();
//    }
//}
    public class Main {
        public static void main(String[] args) {
            SheetImpl sheet = new SheetImpl("TestSheet", 20, 10, 2, 10);
//          sheet.setCell("B12", "100");
//            sheet.setCell("C12", "{REF, B12}");

            Engine engine = new EngineImpl();  // Your engine implementation
            ConsoleUI ui = new ConsoleUI(engine);
            ui.start();
        }
    }
//public class Main {
//    public static void main(String[] args) {
//        Sheet sheet = new SheetImpl();
//        sheet.updateCellValueAndCalculate(0, 0, "Hello, World!");
//
//        Cell cell = sheet.getCell(0, 0);
//        cell.calculateEffectiveValue();
//        Object value = cell.getEffectiveValue().getValue();
//        System.out.println(value);
//
//        sheet.updateCellValueAndCalculate(1,1, "{plus, 1, 2}");
//
//        cell = sheet.getCell(1, 1);
//
//        cell.calculateEffectiveValue();
//        Double result = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
//        System.out.println("result: " + result);
//    }
//}

