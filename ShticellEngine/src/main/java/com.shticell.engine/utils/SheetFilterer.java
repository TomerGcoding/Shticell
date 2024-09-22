package com.shticell.engine.utils;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.dto.DTOCreator;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;

import java.util.*;
import java.util.function.Predicate;

public class SheetFilterer {
    private Sheet sheet;
    private final List<List<Cell>> rangeToFilter;
    private final List<Integer> columnsToFilterBy;
    private final List<Predicate<Cell>> filterConditions;
    private final List<String> valuesToFilterBy;

    public SheetFilterer(Sheet sheet, String rangeToFilter, String columnsToFilterBy, String valuesToFilterBy) {
        this.sheet = sheet.copySheet();
        this.rangeToFilter = getRangeToFilter(rangeToFilter);
        this.columnsToFilterBy = parseColumnsToFilterBy(columnsToFilterBy);
        this.filterConditions = new ArrayList<>();
        this.valuesToFilterBy = parseValuesTofilterBy(valuesToFilterBy);
        if (this.columnsToFilterBy.size() != this.valuesToFilterBy.size()) {
            throw new IllegalArgumentException("Number of columns and filter values must match");
        }
    }

    private List<String> parseValuesTofilterBy(String valuesToFilterBy) {
        List<String> valuesToFilter = new ArrayList<>();
        for (String value : valuesToFilterBy.split(",")) {
            valuesToFilter.add(value.trim());
        }
        return valuesToFilter;
    }

    private List<Integer> parseColumnsToFilterBy(String columnsToFilterBy) {
        List<Integer> columnsToFilter = new ArrayList<>();
        for (String column : columnsToFilterBy.split(",")) {
            columnsToFilter.add(CoordinateFormatter.getColumnIndex(column.trim()));
        }
        return columnsToFilter;
    }

    private List<List<Cell>> getRangeToFilter(String rangeToFilter) {
        List<List<Cell>> rangeToFilterList = new ArrayList<>();
        List<String> rangeCellsIds = parseRangeToFilter(rangeToFilter);
        Map<Integer, List<Cell>> rowIndexToRowCellsInRange = new HashMap<>();
        for (String rangeCellId : rangeCellsIds) {
            int[] indexes = CoordinateFormatter.cellIdToIndex(rangeCellId);
            Cell cell = sheet.getCell(rangeCellId);
            if (cell == null) {
                cell = new CellImpl(indexes[0], indexes[1], "", sheet.getVersion(), sheet);
            }
            if (!rowIndexToRowCellsInRange.containsKey(indexes[0])) {
                rowIndexToRowCellsInRange.put(indexes[0], new ArrayList<>());
            }
            rowIndexToRowCellsInRange.get(indexes[0]).add(cell);
        }
        for (List<Cell> row : rowIndexToRowCellsInRange.values()) {
            rangeToFilterList.add(row);
        }
        return rangeToFilterList;
    }

    private List<String> parseRangeToFilter(String rangeToFilter) {
        List<String> cells = new ArrayList<>();

        String[] parts = rangeToFilter.split("\\.\\.");
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

    private void addFilterConditions() {
        for (String value : valuesToFilterBy) {
            filterConditions.add(cell -> cell.getOriginalValue().equals(value));
        }
    }

    public SheetDTO filter() {
        List<List<Cell>> filteredRows = new ArrayList<>();

        for (List<Cell> row : rangeToFilter) {
            boolean rowMatches = true;
            for (int i = 0; i < columnsToFilterBy.size(); i++) {
                Cell cell = getCellAtColumn(row, columnsToFilterBy.get(i));
                cell.calculateEffectiveValue();
                if (!cell.getEffectiveValue().getValue().toString().equals(valuesToFilterBy.get(i))) {
                    rowMatches = false;
                    break;
                }
            }
            if (rowMatches) {
                filteredRows.add(row);
            }
        }

        // Get the starting row of the range
        int startRow = rangeToFilter.get(0).get(0).getCoordinate().getRow();

        // Move filtered rows to the top of the range
        for (int i = 0; i < rangeToFilter.size(); i++) {
            List<Cell> currentRow = i < filteredRows.size() ? filteredRows.get(i) : null;

            for (Cell cell : rangeToFilter.get(i)) {
                String newValue = "";
                if (currentRow != null) {
                    Cell filteredCell = getCellAtColumn(currentRow, cell.getCoordinate().getColumn());
                    if (filteredCell.getDependsOn().isEmpty()) {
                        newValue = filteredCell.getOriginalValue();
                    }
                }

                // Set the cell value in the new row position
                sheet = sheet.setCell(CoordinateFormatter.indexToCellId(startRow + i, cell.getCoordinate().getColumn()), newValue);
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