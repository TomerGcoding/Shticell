package com.shticell.engine.utils;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellType;

import java.util.Comparator;

public class NumericCellComparator implements Comparator<Cell> {
    @Override
    public int compare(Cell cell1, Cell cell2) {
        if (cell1.getEffectiveValue().getCellType() == CellType.NUMERIC
                && cell2.getEffectiveValue().getCellType() == CellType.NUMERIC) {
            try {
                double value1 = Double.parseDouble(cell1.getOriginalValue());
                double value2 = Double.parseDouble(cell2.getOriginalValue());
                return Double.compare(value1, value2);
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails
                // You might want to log this or handle it differently based on your requirements
                return 0;
            }
        }
        // If either cell is not numeric, consider them equal
        return 0;
    }
}
