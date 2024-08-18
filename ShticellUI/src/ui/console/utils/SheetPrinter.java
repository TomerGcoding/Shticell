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
        int rowHeight = sheetDTO.getProperties().getRowHeight();

        // Print column headers (A, B, C, etc.)
        System.out.print("    "); // Initial padding for row numbers
        for (int col = 0; col < columns; col++) {
            System.out.print(String.format("%-" + columnWidth + "s", (char) ('A' + col)));
        }
        System.out.println();

        // Print each row, considering row height
        for (int row = 0; row < rows; row++) {
            // Print each row's block (considering row height)
            for (int h = 0; h < rowHeight; h++) {
                if (h == 0) {
                    // Print row number with two digits on the first row of the block
                    System.out.print(String.format("%02d", row + 1) + " ");
                } else {
                    // Print padding for the row number
                    System.out.print("   ");
                }

                // Print each cell in the row
                for (int col = 0; col < columns; col++) {
                    Cell cell = sheetDTO.getCell(row, col);
                    String value = "";

                    if (cell != null) {
                        // Check if EffectiveValue is not null and retrieve its value
                        if (cell.getEffectiveValue() != null && cell.getEffectiveValue().getValue() != null) {
                            value = cell.getEffectiveValue().getValue().toString();
                        }
                    }

                    // Truncate the value to fit within the cell's width
                    String truncatedValue = truncateOrPad(value, columnWidth);

                    // Print the value only in the first row of the cell block, padded to the left
                    if (h == 0) {
                        System.out.print(String.format("%-" + columnWidth + "s", truncatedValue));
                    } else {
                        // For other rows in the cell block, print spaces
                        System.out.print(String.format("%-" + columnWidth + "s", ""));
                    }
                }
                System.out.println();
            }
        }
    }

    private String truncateOrPad(String value, int width) {
        if (value.length() > width) {
            return value.substring(0, width); // Truncate if too long
        } else {
            return value; // No padding needed since we're printing in the top-left corner
        }
    }
}


