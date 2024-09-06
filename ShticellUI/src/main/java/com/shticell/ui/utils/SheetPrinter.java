package com.shticell.ui.utils;

import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.cell.impl.CellType;

public class SheetPrinter {


    public static void printSheet(SheetDTO sheetDTO) {
        System.out.println("Sheet Name: " + sheetDTO.getSheetName());
        System.out.println("Version: " + sheetDTO.getCurrVersion());
        System.out.println();

        int rows = sheetDTO.getProperties().getNumRows();
        int columns = sheetDTO.getProperties().getNumCols();
        int columnWidth = sheetDTO.getProperties().getColWidth();
        int rowHeight = sheetDTO.getProperties().getRowHeight();

        System.out.print("   ");
        for (int col = 0; col < columns; col++) {
            String header = String.valueOf((char) ('A' + col));
            int padding = (columnWidth - header.length())/2 ;
            System.out.print(" ".repeat(padding) + header + " ".repeat(columnWidth - header.length() - padding));
            System.out.print(" ");
        }
        System.out.println();

        for (int row = 0; row < rows; row++) {
            for (int h = 0; h < rowHeight; h++) {
                if (h == 0) {
                    System.out.print(String.format("%02d", row + 1)+"|");
                } else {
                    System.out.print("  |");
                }

                for (int col = 0; col < columns; col++) {
                    CellDTO cell = sheetDTO.getCell(row, col);
                    String value = "";

                    if (cell != null) {
                        if (cell.getEffectiveValue() != null && cell.getEffectiveValue().getValue() != null) {
                            if(cell.getEffectiveValue().getCellType() == CellType.NUMERIC)
                                value = NumberFormatter.formatNumber((Double)cell.getEffectiveValue().getValue());
                            else if(cell.getEffectiveValue().getCellType()==CellType.BOOLEAN)
                                value = cell.getEffectiveValue().getValue().toString().toUpperCase();
                            else
                                value = cell.getEffectiveValue().getValue().toString();
                        }
                    }

                    String truncatedValue = truncateOrPad(value, columnWidth);

                    if (h == 0)
                        System.out.print(String.format("%-" + (columnWidth) + "s", truncatedValue)+"|");
                     else
                        System.out.print(String.format("%-" + (columnWidth) + "s", "") + "|");
                }
                System.out.println();
            }
        }
    }

    private static String truncateOrPad(String value, int width) {
        if (value.length() > width)
            return value.substring(0, width);
        else
            return value;

    }
}
