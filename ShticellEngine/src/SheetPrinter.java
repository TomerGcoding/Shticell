
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;

public class SheetPrinter {

    private final Sheet sheet;

    public SheetPrinter(Sheet sheet) {
        this.sheet = sheet;
    }

    public void printSheet() {
        // Print metadata
        System.out.println("Sheet Name: " + sheet.getSheetName());
        System.out.println("Version: " + sheet.getVersion());
        System.out.println();

        int rows = sheet.getRowCount();
        int columns = sheet.getColumnCount();
        int columnWidth = sheet.getColumnWidth();
        int lineWidth = sheet.getRowHeight();

        // Print column headers (A, B, C, etc.)
        System.out.print("    "); // Initial padding for row numbers
        for (int col = 0; col < columns; col++) {
            System.out.print(String.format("%-" + columnWidth + "s", (char) ('A' + col)));
            if (col < columns - 1) {
                System.out.print("|");
            }
        }
        System.out.println();

        // Print each row
        for (int row = 0; row < rows; row++) {
            // Print row number with two digits
            System.out.print(String.format("%02d", row + 1) + " ");

            // Print each cell in the row
            for (int col = 0; col < columns; col++) {
                Cell cell = sheet.getCell(row, col);
                String value = cell.getEffectiveValue().toString();
                System.out.print(String.format("%-" + columnWidth + "s", truncateOrPad(value, columnWidth)));

                if (col < columns - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }
    }

    private String truncateOrPad(String value, int width) {
        if (value.length() > width) {
            return value.substring(0, width); // Truncate if too long
        } else {
            return String.format("%-" + width + "s", value); // Pad if too short
        }
    }
}
