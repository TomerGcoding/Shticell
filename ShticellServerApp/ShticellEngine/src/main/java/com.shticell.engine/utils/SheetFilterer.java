package com.shticell.engine.utils;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import dto.SheetDTO;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;

import java.util.ArrayList;
import java.util.List;

public class SheetFilterer {
    private Sheet sheet;
    private final List<List<Cell>> rangeToFilter;
    private final int columnToFilterBy;

    public SheetFilterer(Sheet sheet, String rangeToFilter, String columnToFilterBy) {
        this.sheet = sheet.copySheet();
        this.rangeToFilter = getRangeToFilter(rangeToFilter);
        this.columnToFilterBy = CoordinateFormatter.getColumnIndex(columnToFilterBy.trim().toUpperCase());
    }

    private List<List<Cell>> getRangeToFilter(String rangeToFilter) {
        List<List<Cell>> rangeToFilterList = new ArrayList<>();
        String[] parts = rangeToFilter.split("\\.\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format");
        }

        int[] startCoords = CoordinateFormatter.cellIdToIndex(parts[0]);
        int[] endCoords = CoordinateFormatter.cellIdToIndex(parts[1]);

        for (int row = startCoords[0]; row <= endCoords[0]; row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int col = startCoords[1]; col <= endCoords[1]; col++) {
                String cellId = CoordinateFormatter.indexToCellId(row, col);
                Cell cell = sheet.getCell(cellId);
                if (cell == null) {
                    cell = new CellImpl(row, col, "", sheet.getVersion(), sheet);
                }
                rowCells.add(cell);
            }
            rangeToFilterList.add(rowCells);
        }
        return rangeToFilterList;
    }

    public SheetDTO filter(List<String> filterValues) {
        List<List<Cell>> filteredRows = new ArrayList<>();

        for (List<Cell> row : rangeToFilter) {
            Cell cellToFilter = getCellAtColumn(row, columnToFilterBy);
            cellToFilter.calculateEffectiveValue();
            if (cellMatchesFilter(cellToFilter, filterValues)) {
                filteredRows.add(row);
            }
        }

        int startRow = rangeToFilter.get(0).get(0).getCoordinate().getRow();

        for (int i = 0; i < rangeToFilter.size(); i++) {
            List<Cell> currentRow = rangeToFilter.get(i); // Current row in original data

            boolean isRowFiltered = filteredRows.contains(currentRow);

            for (Cell cell : currentRow) {
                String newValue = "";
                if (isRowFiltered) {
                    newValue = cell.getEffectiveValue().getValue().toString();
                }


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

    private boolean cellMatchesFilter(Cell cell, List<String> filterValue) {
        return filterValue.contains(cell.getEffectiveValue().getValue().toString());
    }
}