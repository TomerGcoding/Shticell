package ui.console.utils;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellType;

public class SheetPrinter {


    public static void printSheet(SheetDTO sheetDTO) {
        // Print metadata
        System.out.println("Sheet Name: " + sheetDTO.getSheetName());
        System.out.println("Version: " + sheetDTO.getCurrVersion());
        System.out.println();

        int rows = sheetDTO.getProperties().getNumRows();
        int columns = sheetDTO.getProperties().getNumCols();
        int columnWidth = sheetDTO.getProperties().getColWidth();
        int rowHeight = sheetDTO.getProperties().getRowHeight();

        // Print column headers (A, B, C, etc.)
        System.out.print("   "); // Initial padding for row numbers
        for (int col = 0; col < columns; col++) {
            String header = String.valueOf((char) ('A' + col));
            int padding = (columnWidth - header.length())/2 ; // Calculate padding for centering
            System.out.print(" ".repeat(padding) + header + " ".repeat(columnWidth - header.length() - padding));
            System.out.print(" ");
        }
        System.out.println();

        // Print each row, considering row height
        for (int row = 0; row < rows; row++) {
            // Print each row's block (considering row height)
            for (int h = 0; h < rowHeight; h++) {
                if (h == 0) {
                    // Print row number with two digits on the first row of the block
                    System.out.print(String.format("%02d", row + 1)+"|");
                } else {
                    // Print padding for the row number
                    System.out.print("  |");
                }

                // Print each cell in the row
                for (int col = 0; col < columns; col++) {
                    CellDTO cell = sheetDTO.getCell(row, col);
                    String value = "";

                    if (cell != null) {
                        // Check if EffectiveValue is not null and retrieve its value
                        if (cell.getEffectiveValue() != null && cell.getEffectiveValue().getValue() != null) {
                            if(cell.getEffectiveValue().getCellType() == CellType.NUMERIC)
                                value = NumberFormatter.formatNumber((Double)cell.getEffectiveValue().getValue());
                            else {
                                value = cell.getEffectiveValue().getValue().toString();
                            }
                        }
                    }

                    // Truncate the value to fit within the cell's width
                    String truncatedValue = truncateOrPad(value, columnWidth);

                    // Print the value only in the first row of the cell block, padded to the left
                    if (h == 0) {
                        System.out.print(String.format("%-" + (columnWidth) + "s", truncatedValue)+"|");
                    } else {
                        // For other rows in the cell block, print spaces followed by a pipe
                        System.out.print(String.format("%-" + (columnWidth) + "s", "") + "|");
                    }
                }
                System.out.println();
            }
        }
    }

    private static String truncateOrPad(String value, int width) {
        if (value.length() > width) {
            return value.substring(0, width); // Truncate if too long
        } else {
            return value; // No padding needed since we're printing in the top-left corner
        }
    }


}
