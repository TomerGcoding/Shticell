package com.shticell.engine.utils;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.dto.DTOCreator;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.expression.impl.ref.RefExpression;
import com.shticell.engine.expression.parser.FunctionParser;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;

import java.util.*;

public class SheetSorter {
    private Sheet sheet;
    private final List<List<Cell>> rangeToSort;
    private final List<Integer> columnsToSortBy;

    public SheetSorter(Sheet sheet, String rangeToSort, String columnsToSortBy) {
        this.sheet = sheet.copySheet();
        this.rangeToSort = getRangeToSort(rangeToSort);
        this.columnsToSortBy = parseColumnsToSortBy(columnsToSortBy);
    }

    private List<Integer> parseColumnsToSortBy(String columnsToSortBy) {
        List<Integer> columnsToSort = new ArrayList<Integer>();
        for (String column : columnsToSortBy.split(",")) {
            columnsToSort.add(CoordinateFormatter.getColumnIndex(column.trim()));
        }
        return columnsToSort;
    }

    private List<List<Cell>> getRangeToSort(String rangeToSort) {
        List<List<Cell>> rangeToSortList = new ArrayList<>();
        List<String> rangeCellsIds = parseRangeToSort(rangeToSort);
        Map<Integer,List<Cell>> rowIndexToRowCellsInRange = new HashMap<>();
        for (String rangeCellId : rangeCellsIds) {
            int[] indexes = CoordinateFormatter.cellIdToIndex(rangeCellId);
            Cell cell = sheet.getCell(rangeCellId);
            if (cell == null) {
                cell = new CellImpl(indexes[0], indexes[1], "", sheet.getVersion(), sheet);
            }
            if(!rowIndexToRowCellsInRange.containsKey(indexes[0])) {
                rowIndexToRowCellsInRange.put(indexes[0], new ArrayList<>());
            }
            rowIndexToRowCellsInRange.get(indexes[0]).add(cell);
        }
        for (List<Cell> row : rowIndexToRowCellsInRange.values()) {
            rangeToSortList.add(row);
        }
        return rangeToSortList;
    }

    private List<String> parseRangeToSort(String rangeToSort) {
        List<String> cells = new ArrayList<>();

        String[] parts = rangeToSort.split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format");
        }

        char startCol = parts[0].charAt(0);
        int startRow = Integer.parseInt(parts[0].substring(1));
        char endCol = parts[1].charAt(0);
        int endRow = Integer.parseInt(parts[1].substring(1));

        for (char col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                cells.add("" + col + row);
            }
        }

        return cells;
    }


    public SheetDTO sort() {

        // Create a comparator for rows
        Comparator<List<Cell>> rowComparator = (row1, row2) -> {
            for (Integer column : columnsToSortBy) {
                Cell cell1 = getCellAtColumn(row1, column);
                cell1.calculateEffectiveValue();
                Cell cell2 = getCellAtColumn(row2, column);
                cell2.calculateEffectiveValue();

                if (cell1.getEffectiveValue().getCellType() == CellType.NUMERIC
                        && cell2.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                    try {
                        double value1 = Double.parseDouble(cell1.getOriginalValue());
                        double value2 = Double.parseDouble(cell2.getOriginalValue());
                        int comparison = Double.compare(value1, value2);
                        if (comparison != 0) {
                            return comparison;
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing errors
                    }
                }
                else if(cell1.getEffectiveValue().getCellType() == CellType.STRING) {
                        if (cell2.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                            return 1;
                        }else {
                            return 0;
                        }
                }
                else if (cell2.getEffectiveValue().getCellType() == CellType.STRING) {
                        if (cell1.getEffectiveValue().getCellType() == CellType.NUMERIC) {
                            return -1;
                        }else {
                            return 0;
                        }
                }
            }
            return 0;
        };

        // Sort the rows
        List<List<Cell>> originalOrder = new ArrayList<>(rangeToSort);
        rangeToSort.sort(rowComparator);

        // Swap the values for all cells in the rows
        for (int i = 0; i < rangeToSort.size(); i++) {
            List<Cell> originalRow = originalOrder.get(i);
            List<Cell> sortedRow = rangeToSort.get(i);

            for (int j = 0; j < originalRow.size(); j++) {
                Cell originalCell = originalRow.get(j);
                Cell sortedCell = sortedRow.get(j);

                if(originalCell.getDependsOn().isEmpty()&&sortedCell.getDependsOn().isEmpty()) {
                    this.sheet = sheet.setCell(originalCell.getId(), sortedCell.getOriginalValue());
                }
            }
        }

        return DTOCreator.sheetToDTO(sheet);
    }

    private Cell getCellAtColumn(List<Cell> row, int column) {
        return row.stream()
                .filter(cell -> cell.getCoordinate().getColumn() == column)
                .findFirst()
                .orElse(new CellImpl(row.get(0).getCoordinate().getRow(), column, "", sheet.getVersion(), sheet));
    }


}
