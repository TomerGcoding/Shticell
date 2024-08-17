package ui.console.utils;

import engine.dto.SheetDTO;
import engine.sheet.cell.api.Cell;

public class SheetPrinter {

    private final SheetDTO sheetDTO;

    public SheetPrinter(SheetDTO sheet) {
        this.sheetDTO = sheet;
    }

    public void printSheet() {
        // Print metadata
        System.out.println("Sheet Name: " + sheetDTO.getSheetName());
        System.out.println("Version: " + sheetDTO.getCurrVersion());
        System.out.println();

        int rows = sheetDTO.getProperties().getNumRows();
        int columns = sheetDTO.getProperties().getNumCols();
        int columnWidth = sheetDTO.getProperties().getColWidth();
        int lineWidth = sheetDTO.getProperties().getRowHeight();

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
                Cell cell = sheetDTO.getCell(row, col);
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
